package com.hfm350.tarea3dweshfm350.servicios;

import com.hfm350.tarea3dweshfm350.modelo.Cliente;
import com.hfm350.tarea3dweshfm350.repositorios.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ServiciosCliente {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void almacenarContraseña(String contraseña) {
        String contraseñaCodificada = passwordEncoder.encode(contraseña);
        
    }

    public boolean verificarContraseña(String contraseña, String contraseñaCodificada) {
        return passwordEncoder.matches(contraseña, contraseñaCodificada);
    }
}
