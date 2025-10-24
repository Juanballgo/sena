package Co.edu.sena.socialnet.msvc.users.controller;

import Co.edu.sena.socialnet.msvc.users.model.User;
import Co.edu.sena.socialnet.msvc.users.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    //  Crear un nuevo usuario
    // ============================
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea un usuario nuevo validando correo, contraseña y nombre de usuario.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario creado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o duplicados",
                            content = @Content(mediaType = "text/plain"))
            }
    )
    @PostMapping
    public ResponseEntity<?> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto del usuario a crear",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "username": "juan123",
                                      "firstName": "Juan",
                                      "lastName": "Pérez",
                                      "email": "juan@example.com",
                                      "password": "123456",
                                      "phone": "3001234567",
                                      "country": "Colombia"
                                    }
                                    """)
                    )
            )
            @RequestBody User user) {

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("La contraseña no puede ser nula o vacía.");
        }
        if (user.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("La contraseña debe tener al menos 6 caracteres.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El correo electrónico es obligatorio.");
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.badRequest().body("El formato del correo electrónico no es válido.");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo electrónico ya está registrado.");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre de usuario es obligatorio.");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso.");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setStatus(user.getStatus() != null ? user.getStatus() : "ACTIVE");

        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    // ============================
    //  Listar todos los usuarios
    // ============================
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Devuelve una lista con todos los usuarios registrados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)))
            }
    )
    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // ============================
    //  Obtener usuario por ID
    // ============================
    @Operation(
            summary = "Obtener un usuario por ID",
            description = "Devuelve la información de un usuario específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ============================
    //  Actualizar usuario
    // ============================
    @Operation(
            summary = "Actualizar un usuario existente",
            description = "Permite modificar los datos de un usuario. Solo los campos enviados serán actualizados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @Parameter(description = "ID del usuario a actualizar") @PathVariable String id,
            @RequestBody User datos) {

        return userRepository.findById(id)
                .map(user -> {
                    if (datos.getFirstName() != null) user.setFirstName(datos.getFirstName());
                    if (datos.getLastName() != null) user.setLastName(datos.getLastName());
                    if (datos.getEmail() != null) user.setEmail(datos.getEmail());
                    if (datos.getCountry() != null) user.setCountry(datos.getCountry());
                    if (datos.getDepartment() != null) user.setDepartment(datos.getDepartment());
                    if (datos.getCity() != null) user.setCity(datos.getCity());
                    if (datos.getProfession() != null) user.setProfession(datos.getProfession());
                    if (datos.getBirthDate() != null) user.setBirthDate(datos.getBirthDate());
                    if (datos.getGender() != null) user.setGender(datos.getGender());
                    if (datos.getPhone() != null) user.setPhone(datos.getPhone());
                    if (datos.getAvatar() != null) user.setAvatar(datos.getAvatar());
                    if (datos.getCoverPhoto() != null) user.setCoverPhoto(datos.getCoverPhoto());
                    if (datos.getStatus() != null) user.setStatus(datos.getStatus());
                    if (datos.getDescription() != null) user.setDescription(datos.getDescription());
                    if (datos.getUsername() != null) user.setUsername(datos.getUsername());
                    if (datos.getResetCode() != null) user.setResetCode(datos.getResetCode());
                    if (datos.getPassword() != null)
                        user.setPassword(passwordEncoder.encode(datos.getPassword()));

                    User updated = userRepository.save(user);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<?> delete(
            @Parameter(description = "ID del usuario a desactivar") @PathVariable String id) {

        return userRepository.findById(id)
                .map(user -> {
                    if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
                        return ResponseEntity.badRequest().body("El usuario ya está inactivo.");
                    }
                    user.setStatus("INACTIVE");
                    userRepository.save(user);
                    System.out.println("Usuario marcado como INACTIVE con ID: " + id);
                    return ResponseEntity.message("Usuario desactivado correctamente.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ============================
    //  Activar usuario
    // ============================
    @Operation(
            summary = "Reactivar usuario",
            description = "Cambia el estado del usuario a ACTIVE si está inactivo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario reactivado correctamente"),
                    @ApiResponse(responseCode = "400", description = "El usuario ya está activo"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(
            @Parameter(description = "ID del usuario a reactivar") @PathVariable String id) {

        return userRepository.findById(id)
                .map(user -> {
                    if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
                        return ResponseEntity.badRequest().body("El usuario ya está activo.");
                    }
                    user.setStatus("ACTIVE");
                    userRepository.save(user);
                    System.out.println("Usuario reactivado con ID: " + id);
                    return ResponseEntity.message("Usuario reactivado correctamente.");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
