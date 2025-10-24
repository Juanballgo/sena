package Co.edu.sena.socialnet.msvc.users.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Co.edu.sena.socialnet.msvc.users.model.Rol;
import Co.edu.sena.socialnet.msvc.users.model.User;
import Co.edu.sena.socialnet.msvc.users.repository.RolRepository;
import java.util.List;
import java.util.Optional;
import Co.edu.sena.socialnet.msvc.users.repository.UserRepository;

@RestController
@RequestMapping("/api/roles")
public class RolController {
    // Aquí puedes agregar los métodos para manejar las solicitudes relacionadas con los roles
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Rol>> getAllRoles() {
        List<Rol> roles = rolRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    public ResponseEntity<Rol> createRole(@RequestBody Rol rol) {
        Rol newRol = rolRepository.save(rol);
        return ResponseEntity.status(201).body(newRol);
    }

    @PutMapping("/asignar")
    public ResponseEntity<?> asignarRolAUsuario(
            @RequestParam String usuarioId,
            @RequestParam String rolNombre) {

        Optional<User> usuarioOpt = userRepository.findById(usuarioId);
        Optional<Rol> rolOpt = rolRepository.findAll().stream()
            .filter(r -> r.getNombre().equalsIgnoreCase(rolNombre))
            .findFirst();

        if (usuarioOpt.isPresent() && rolOpt.isPresent()) {
            User usuario = usuarioOpt.get();
            usuario.setRole(rolOpt.get().getNombre()); // O usa el ID si prefieres
            userRepository.save(usuario);
            return ResponseEntity.ok("Rol asignado correctamente.");
        } else {
            return ResponseEntity.status(404).body("Usuario o rol no encontrado.");
        }
    }
}

