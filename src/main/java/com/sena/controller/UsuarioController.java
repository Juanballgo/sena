package com.sena.controller;

import com.sena.model.Usuario;
import com.sena.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ POST: Registrar nuevo usuario
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Usuario usuario) {
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("La contraseña no puede ser nula o vacía.");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario saved = usuarioRepository.save(usuario);
        return ResponseEntity.ok(saved);
    }

    // GET: Listar todos los usuarios
    @GetMapping
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    // GET: Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable String id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: Actualizar usuario por ID
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable String id, @RequestBody Usuario usuarioActualizado) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    if (usuarioActualizado.getNames() != null)
                        usuario.setNames(usuarioActualizado.getNames());

                    if (usuarioActualizado.getLastName() != null)
                        usuario.setLastName(usuarioActualizado.getLastName());

                    if (usuarioActualizado.getSecondLastName() != null)
                        usuario.setSecondLastName(usuarioActualizado.getSecondLastName());

                    if (usuarioActualizado.getEmail() != null)
                        usuario.setEmail(usuarioActualizado.getEmail());

                    if (usuarioActualizado.getPassword() != null)
                        usuario.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));

                    if (usuarioActualizado.getRole() != null)
                        usuario.setRole(usuarioActualizado.getRole());

                    Usuario actualizado = usuarioRepository.save(usuario);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: Eliminar usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            System.out.println("Usuario eliminado con ID: " + id);
            return ResponseEntity.noContent().build();
        } else {
            System.out.println("Usuario no encontrado con ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }
}
