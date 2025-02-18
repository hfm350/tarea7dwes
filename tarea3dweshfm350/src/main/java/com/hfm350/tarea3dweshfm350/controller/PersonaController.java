package com.hfm350.tarea3dweshfm350.controller;

import java.lang.annotation.Repeatable;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hfm350.tarea3dweshfm350.fachada.FachadaAdmin;
import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPersona;

@Controller
public class PersonaController {

	@Autowired
	private ServiciosCredenciales servCrendeciales;

	@Autowired
	private ServiciosPersona servPersona;

	@GetMapping("/registroPersona")
	public String registroPersonas(Model model) {
		List<Credencial> listaCredencials = servCrendeciales.findAll().stream()
				.filter(credencial -> !credencial.getUsuario().equalsIgnoreCase("ADMIN")).collect(Collectors.toList());
		model.addAttribute("credenciales", listaCredencials);
		return "registroPersona";
	}
	
	

	@PostMapping("/registroPersona")
	public String registroPersona(@RequestParam String nombre, @RequestParam String email, @RequestParam String usuario,
			@RequestParam String contraseña, Model model) {
		Persona p = new Persona();
		Credencial credencial = new Credencial();
		boolean valido = true;

		if (nombre.matches("[A-Z][a-z]*")) {
			p.setNombre(nombre);
		} else {
			List<Credencial> listaCredencials = servCrendeciales.findAll().stream()
					.filter(c -> !c.getUsuario().equalsIgnoreCase("ADMIN")).collect(Collectors.toList());
			model.addAttribute("credenciales", listaCredencials);
			model.addAttribute("errorMessageNombre", "El NOMBRE debe comenzar con mayúscula.");
			valido = false;
		}

		if (email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.com$")) {
			p.setEmail(email);
		} else {
			model.addAttribute("errorMessageEmail", "El email debe tener el formato xxxxx@xxxx.com.");
			valido = false;
		}

		if (servCrendeciales.existeUsuario(usuario)) {
			model.addAttribute("errorMessageUsuario",
					"El usuario " + usuario + " ya está registrado. Elige otro nombre.");
			valido = false;
		} else {
			credencial.setUsuario(usuario);
		}

		if (contraseña.matches("\\d{4}")) {
			credencial.setPassword(contraseña);
		} else {
			List<Credencial> listaCredencials = servCrendeciales.findAll().stream()
					.filter(c -> !c.getUsuario().equalsIgnoreCase("ADMIN")).collect(Collectors.toList());
			model.addAttribute("credenciales", listaCredencials);
			model.addAttribute("errorMessageContraseña", "La CONTRASEÑA debe tener exactamente 4 dígitos.");
			valido = false;
		}

		if (!valido) {
			return "registroPersona";
		}

		servPersona.insertar(p);
		Long idPersonaLong = p.getId();
		servCrendeciales.insertar(usuario, contraseña, idPersonaLong);
		List<Credencial> listaCredencials = servCrendeciales.findAll().stream()
				.filter(c -> !c.getUsuario().equalsIgnoreCase("ADMIN")).collect(Collectors.toList());
		model.addAttribute("credenciales", listaCredencials);
		model.addAttribute("successMessage", "¡Usuario registrado exitosamente!");

		return "registroPersona";
	}

}
