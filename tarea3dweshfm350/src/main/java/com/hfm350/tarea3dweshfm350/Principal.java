package com.hfm350.tarea3dweshfm350;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.hfm350.tarea3dweshfm350.fachada.FachadaInvitado;

public class Principal implements CommandLineRunner {


	
	@Autowired
	private FachadaInvitado fachadaInvitado;

	@Override
	public void run(String... args) throws Exception {
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "1111"; // Cambia si necesitas otra
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Contraseña encriptada: " + encodedPassword);
        
		fachadaInvitado.mostrarMenuInvitado();
		
		
	}
}
