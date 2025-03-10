package com.hfm350.tarea3dweshfm350.controller;

import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.repositorios.CredencialRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    public String login(@RequestBody Credencial credencial) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credencial.getUsuario(), credencial.getPassword()));

        if (authentication.isAuthenticated()) {
            return "Autenticación exitosa para el usuario: " + credencial.getUsuario();
        } else {
            return "Error en la autenticación";
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody Credencial credencial) {
        Optional<Credencial> existingUser = credencialRepository.findByUsuario(credencial.getUsuario());
        if (existingUser.isPresent()) {
            return "El usuario ya existe";
        }

        credencial.setPassword(passwordEncoder.encode(credencial.getPassword()));
        credencialRepository.save(credencial);
        return "Usuario registrado exitosamente";
    }
}
