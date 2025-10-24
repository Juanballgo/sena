package Co.edu.sena.socialnet.msvc.users.controller;

import Co.edu.sena.socialnet.msvc.users.model.User;
import Co.edu.sena.socialnet.msvc.users.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Operaciones del servicio de usuarios del sistema SocialNet")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================
    // Crear un nuevo usuario
    // ============================
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario nuevo validando correo, contraseña y nombre de usuario.", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario creado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o duplicados")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "La contraseña no puede ser nula o vacía.");
            return ResponseEntity.badRequest().body(response);
        }
        if (user.getPassword().length() < 6) {
            response.put("success", false);
            response.put("message", "La contraseña debe tener al menos 6 caracteres.");
            return ResponseEntity.badRequest().body(response);
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "El correo electrónico es obligatorio.");
            return ResponseEntity.badRequest().body(response);
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            response.put("success", false);
            response.put("message", "El formato del correo electrónico no es válido.");
            return ResponseEntity.badRequest().body(response);
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            response.put("success", false);
            response.put("message", "El correo electrónico ya está registrado.");
            return ResponseEntity.badRequest().body(response);
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "El nombre de usuario es obligatorio.");
            return ResponseEntity.badRequest().body(response);
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            response.put("success", false);
            response.put("message", "El nombre de usuario ya está en uso.");
            return ResponseEntity.badRequest().body(response);
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "El nombre es obligatorio.");
            return ResponseEntity.badRequest().body(response);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(user.getStatus() != null ? user.getStatus() : "ACTIVE");
        user.setRole("USER");

        User saved = userRepository.save(user);
        response.put("success", true);
        response.put("message", "Usuario creado correctamente.");
        response.put("user", saved);

        return ResponseEntity.ok(response);
    }

    // ============================
    // Listar todos los usuarios
    // ============================
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista con todos los usuarios registrados.", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userRepository.findAll();

        response.put("success", true);
        response.put("count", users.size());
        response.put("users", users);
        return ResponseEntity.ok(response);
    }

    // ============================
    // Obtener usuario por ID
    // ============================
    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve la información de un usuario específico.", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @Parameter(description = "ID del usuario", required = true) @PathVariable String id) {

        Map<String, Object> response = new HashMap<>();
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            response.put("success", true);
            response.put("user", user.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Usuario no encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ============================
    // Actualizar usuario
    // ============================
    @Operation(summary = "Actualizar un usuario existente", description = "Permite modificar los datos de un usuario. Solo los campos enviados serán actualizados.", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @Parameter(description = "ID del usuario a actualizar") @PathVariable String id,
            @RequestBody User datos) {

        Map<String, Object> response = new HashMap<>();

        return userRepository.findById(id)
                .map(user -> {
                    if (datos.getFirstName() != null)
                        user.setFirstName(datos.getFirstName());
                    if (datos.getLastName() != null)
                        user.setLastName(datos.getLastName());
                    if (datos.getEmail() != null)
                        user.setEmail(datos.getEmail());
                    if (datos.getCountry() != null)
                        user.setCountry(datos.getCountry());
                    if (datos.getDepartment() != null)
                        user.setDepartment(datos.getDepartment());
                    if (datos.getCity() != null)
                        user.setCity(datos.getCity());
                    if (datos.getProfession() != null)
                        user.setProfession(datos.getProfession());
                    if (datos.getBirthDate() != null)
                        user.setBirthDate(datos.getBirthDate());
                    if (datos.getGender() != null)
                        user.setGender(datos.getGender());
                    if (datos.getPhone() != null)
                        user.setPhone(datos.getPhone());
                    if (datos.getAvatar() != null)
                        user.setAvatar(datos.getAvatar());
                    if (datos.getCoverPhoto() != null)
                        user.setCoverPhoto(datos.getCoverPhoto());
                    if (datos.getStatus() != null)
                        user.setStatus(datos.getStatus());
                    if (datos.getDescription() != null)
                        user.setDescription(datos.getDescription());
                    if (datos.getUsername() != null)
                        user.setUsername(datos.getUsername());
                    if (datos.getResetCode() != null)
                        user.setResetCode(datos.getResetCode());
                    if (datos.getPassword() != null)
                        user.setPassword(passwordEncoder.encode(datos.getPassword()));

                    User updated = userRepository.save(user);

                    response.put("success", true);
                    response.put("message", "Usuario actualizado correctamente.");
                    response.put("user", updated);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Usuario no encontrado.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }
    // ============================
    //  Desactivar usuario
    // ============================
@Operation(
        summary = "Desactivar usuario",
        description = "Cambia el estado del usuario a INACTIVE.",
        responses = {
                @ApiResponse(responseCode = "200", description = "Usuario desactivado correctamente"),
                @ApiResponse(responseCode = "400", description = "El usuario ya está inactivo"),
                @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        }
)
@DeleteMapping("/{id}")
public ResponseEntity<Map<String, Object>> delete(
        @Parameter(description = "ID del usuario a desactivar") @PathVariable String id) {

    return userRepository.findById(id)
            .map(user -> {
                Map<String, Object> response = new HashMap<>();

                if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
                    response.put("success", false);
                    response.put("message", "El usuario ya está inactivo.");
                    return ResponseEntity.badRequest().body(response);
                }

                user.setStatus("INACTIVE");
                userRepository.save(user);

                System.out.println("Usuario marcado como INACTIVE con ID: " + id);

                response.put("success", true);
                response.put("message", "Usuario desactivado correctamente.");
                response.put("userId", id);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Usuario no encontrado.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            });
}

    // ============================
    // Activar usuario
    // ============================
    @Operation(summary = "Reactivar usuario", description = "Cambia el estado del usuario a ACTIVE si está inactivo.", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario reactivado correctamente"),
            @ApiResponse(responseCode = "400", description = "El usuario ya está activo"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Map<String, Object>> activate(
            @Parameter(description = "ID del usuario a reactivar") @PathVariable String id) {

        return userRepository.findById(id)
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();

                    if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
                        response.put("success", false);
                        response.put("message", "El usuario ya está activo.");
                        return ResponseEntity.badRequest().body(response);
                    }

                    user.setStatus("ACTIVE");
                    userRepository.save(user);

                    System.out.println("Usuario reactivado con ID: " + id);

                    response.put("success", true);
                    response.put("message", "Usuario reactivado correctamente.");
                    response.put("userId", id);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Usuario no encontrado.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                });
    }
}
