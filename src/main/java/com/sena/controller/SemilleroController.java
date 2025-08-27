package com.sena.controller;

import com.sena.model.Semillero;
import com.sena.dto.SemilleroConUsuariosDTO;
import com.sena.repository.SemilleroRepositorio;
import com.sena.model.Usuario;
import com.sena.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/semilleros")
public class SemilleroController {

    @Autowired
    private SemilleroRepositorio semilleroRepositorio;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // POST: Crear un nuevo semillero
    @PostMapping
    public ResponseEntity<?> crearSemillero(@RequestBody Semillero semillero) {
        Semillero guardado = semilleroRepositorio.save(semillero);
        return ResponseEntity.ok(guardado);
    }

    // PUT: Asignar semillero a un usuario
    @PutMapping("/asignar-semillero")
    public ResponseEntity<?> asignarSemillero(
            @RequestParam String usuarioId,
            @RequestParam String semilleroId) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setSemillero(semilleroId);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Semillero asignado correctamente al usuario con ID: " + usuarioId);
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }

    // GET: Obtener semillero por ID
    @GetMapping("/{id}")
    public ResponseEntity<Semillero> getById(@PathVariable String id) {
        Optional<Semillero> semillero = semilleroRepositorio.findById(id);
        return semillero.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener todos los semilleros
    @GetMapping
    public List<Semillero> getAll() {
        return semilleroRepositorio.findAll();
    }

    @GetMapping("/con-usuarios")
    public ResponseEntity<?> getSemillerosConUsuarios() {
        List<Semillero> semilleros = semilleroRepositorio.findAll();

        List<SemilleroConUsuariosDTO> resultado = semilleros.stream().map(semillero -> {
            List<Usuario> usuarios = usuarioRepository.findAll().stream()
                    .filter(u -> semillero.getId().equals(u.getSemillero()))
                    .collect(Collectors.toList());

            return new SemilleroConUsuariosDTO(semillero, usuarios);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }
}