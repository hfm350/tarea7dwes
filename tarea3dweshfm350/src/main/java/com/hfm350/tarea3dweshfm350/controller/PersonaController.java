package com.hfm350.tarea3dweshfm350.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.seguridad.SecurityConfig;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPersona;

@Controller
public class PersonaController {

    @Autowired
    private ServiciosCredenciales servCredenciales;

    @Autowired
    private ServiciosPersona servPersona;
    
    @Autowired
    private SecurityConfig securityConfig;

    @GetMapping("/registroPersona")
    public String registroPersonas(Model model) {
        List<Credencial> listaCredenciales = servCredenciales.findAll().stream()
                .filter(credencial -> credencial.getPersona() != null) // Filtra credenciales sin persona
                .filter(credencial -> !credencial.getRol().equalsIgnoreCase("ROLE_ADMIN")&& !credencial.getRol().equalsIgnoreCase("ROLE_CLIENTE"))
                .collect(Collectors.toList());

        model.addAttribute("credenciales", listaCredenciales);
        return "registroPersona";
    }
    

    @PostMapping("/registroPersona")
    public String registroPersona(@RequestParam String nombre, 
                                  @RequestParam String email, 
                                  @RequestParam String usuario, 
                                  @RequestParam String contraseña, 
                                  Model model) {
        boolean valido = true;

        // Validaciones
        if (nombre.isEmpty() || !nombre.matches("[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(\\s[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)*")) {
            model.addAttribute("errorMessageNombre", "El nombre debe comenzar con mayúscula y solo contener letras.");
            valido = false;
        }

        if (email.isEmpty() || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.com$")) {
            model.addAttribute("errorMessageEmail", "El email debe tener el formato correcto.");
            valido = false;
        } else if (servPersona.existeEmail(email)) {
            model.addAttribute("errorMessageEmail", "El email ya está registrado.");
            valido = false;
        }

        if (usuario.isEmpty() || usuario.contains(" ")) {
            model.addAttribute("errorMessageUsuario", "El usuario no puede contener espacios en blanco.");
            valido = false;
        } else if (servCredenciales.existeUsuario(usuario)) {
            model.addAttribute("errorMessageUsuario", "El usuario ya está registrado.");
            valido = false;
        }

        if (contraseña.isEmpty() || !contraseña.matches("\\d{4}")) {
            model.addAttribute("errorMessageContraseña", "La contraseña debe tener exactamente 4 dígitos numéricos.");
            valido = false;
        }

        if (!valido) {
            List<Credencial> listaCredenciales = servCredenciales.findAll()
                    .stream()
                    .filter(credencial -> !credencial.getUsuario().equalsIgnoreCase("ADMIN"))
                    .collect(Collectors.toList());
            model.addAttribute("credenciales", listaCredenciales);
            return "registroPersona";
        }
        Persona p = new Persona();
        p.setNombre(nombre);
        p.setEmail(email);
        servPersona.insertar(p);


        // Crea la Credencial y asigna el rol ROLE_PERSONAL
        Credencial credencial = new Credencial();
        credencial.setUsuario(usuario);
        credencial.setPassword(contraseña);
        credencial.setPersona(p);
        credencial.setRol("ROLE_PERSONAL");

        //Guarda la credencial en la base de datos
        servCredenciales.insertar(credencial.getUsuario(), credencial.getPassword(), credencial.getPersona().getId());

        model.addAttribute("successMessage", "¡Usuario registrado exitosamente!");
        
        List<Credencial> listaCredenciales = servCredenciales.findAll()
                .stream()
                .filter(credencialExistente -> credencialExistente.getPersona()  != null)
                .filter(credencialExistente -> !credencialExistente.getRol().equalsIgnoreCase("ROLE_ADMIN")&& !credencialExistente.getRol().equalsIgnoreCase("ROLE_CLIENTE"))
                .collect(Collectors.toList());
        model.addAttribute("credenciales", listaCredenciales);

        return "registroPersona";
    }

}
