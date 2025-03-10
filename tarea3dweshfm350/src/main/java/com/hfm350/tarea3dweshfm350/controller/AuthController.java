package com.hfm350.tarea3dweshfm350.controller;

import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.repositorios.CredencialRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CredencialRepository credencialRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, CredencialRepository credencialRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.credencialRepository = credencialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    	


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Credencial credencial) {
        Optional<Credencial> existingUser = credencialRepository.findByUsuario(credencial.getUsuario());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe");
        }

        credencial.setPassword(passwordEncoder.encode(credencial.getPassword()));
        credencial.setRol("PERSONAL");
        credencialRepository.save(credencial);

        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

}
