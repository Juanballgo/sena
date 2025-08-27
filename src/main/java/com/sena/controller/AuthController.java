package com.sena.controller;

import com.sena.dto.Login;
import com.sena.model.Usuario;
import com.sena.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.sena.dto.LoginResponse;
import com.sena.security.JwtUtil;

import io.jsonwebtoken.Claims;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private com.sena.repository.SemilleroRepositorio semilleroRepositorio;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login loginRequest) {
        Optional<Usuario> userOptional = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                String nombreSemillero = null;
                if (usuario.getSemillero() != null && !usuario.getSemillero().isEmpty()) {
                    Optional<com.sena.model.Semillero> semilleroOpt = semilleroRepositorio
                            .findById(usuario.getSemillero());
                    nombreSemillero = semilleroOpt.map(com.sena.model.Semillero::getNombre)
                            .orElse("Semillero no encontrado");
                }

                String token = JwtUtil.generateToken(
                        usuario.getEmail(),
                        usuario.getId(),
                        usuario.getRole() 
                );

                LoginResponse response = new LoginResponse(
                        "Inicio de sesión exitoso",
                        usuario.getNames(),
                        usuario.getLastName(),
                        usuario.getEmail(),
                        token,
                        nombreSemillero);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("Contraseña incorrecta");
            }
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }

    @GetMapping("/semilleros")
    public ResponseEntity<?> getSemilleroByEmail(@RequestParam String email) {
        Optional<Usuario> userOptional = usuarioRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();
            // Suponiendo que Usuario tiene un método getSemillero()
            if (usuario.getSemillero() != null) {
                return ResponseEntity.ok(usuario.getSemillero());
            } else {
                return ResponseEntity.status(404).body("El usuario no tiene un semillero asignado");
            }
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }

    @PutMapping("/asignar-semillero")
    public ResponseEntity<?> asignarSemillero(
            @RequestParam String email,
            @RequestParam String semilleroId) {

        Optional<Usuario> userOptional = usuarioRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();
            usuario.setSemillero(semilleroId);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Semillero asignado correctamente al usuario " + email);
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }
    @GetMapping("/decode")
public ResponseEntity<?> decode(@RequestParam String token) {
    // Si el token viene con "Bearer " al inicio, quítalo:
    if (token.startsWith("Bearer ")) {
        token = token.substring(7);
    }
    Claims claims = JwtUtil.decodeToken(token);
    String userId = claims.get("userId", String.class);
    String role = claims.get("role", String.class);
    String email = claims.getSubject();

    // Puedes devolver los datos como JSON
    return ResponseEntity.ok(
        String.format("userId: %s, role: %s, email: %s", userId, role, email)
    );
}
}
