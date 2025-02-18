package com.hfm350.tarea3dweshfm350;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.hfm350.tarea3dweshfm350.fachada.FachadaInvitado;

public class Principal implements CommandLineRunner {


	
	@Autowired
	private FachadaInvitado fachadaInvitado;

	@Override
	public void run(String... args) throws Exception {
		fachadaInvitado.mostrarMenuInvitado();
	}
}
