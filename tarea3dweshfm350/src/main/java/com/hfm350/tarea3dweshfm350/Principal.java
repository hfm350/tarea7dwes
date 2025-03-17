package com.hfm350.tarea3dweshfm350;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//import com.hfm350.tarea3dweshfm350.fachada.FachadaInvitado;

public class Principal implements CommandLineRunner {


	
//	@Autowired
//	private FachadaInvitado fachadaInvitado;

	@Override
	public void run(String... args) throws Exception {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String storedPassword = "$2a$10$vZVPxEW572BvgBggrNNap..YUlg6XAk07PhwXXoSnXx4gbt6dUR8O";
        
        String inputPassword = "1234";

        boolean match = encoder.matches(inputPassword, storedPassword);

        System.out.println("Contraseñas coinciden: " + match);
		
        String rawPassword = "1234";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Contraseña encriptada: " + encodedPassword);
        
//		fachadaInvitado.mostrarMenuInvitado();
		
		
	}
}
