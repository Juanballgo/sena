package com.sena.controller;

import com.sena.dto.Login;
import com.sena.model.Usuario;
import com.sena.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Login loginRequest) {
        Optional<Usuario> userOptional = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                return ResponseEntity.ok("Login exitoso. Bienvenido " + usuario.getEmail());
            } else {
                return ResponseEntity.status(401).body("Contraseña incorrecta");
            }
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }
}
