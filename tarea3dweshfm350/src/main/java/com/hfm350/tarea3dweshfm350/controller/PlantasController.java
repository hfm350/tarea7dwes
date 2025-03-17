package com.hfm350.tarea3dweshfm350.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;

import jakarta.servlet.http.HttpSession;

@Controller
public class PlantasController {

	@Autowired
	private ServiciosPlanta serviciosPlanta;
	
	
	
	@GetMapping("/gestionPlantas")
	public String gestionPlantas(Model model, HttpSession session) {
		model.addAttribute("perfil", session.getAttribute("perfil"));
		return "gestionPlantas";
	}

	@GetMapping("/registrarPlantas")
	public String registrarPlantas(Model model) {
		List<Planta> listaPlantas = serviciosPlanta.findAll();
		model.addAttribute("plantas", listaPlantas);
		return "registrarPlantas";
	}

	@PostMapping("/registrarPlantas")
	public String registrarPlanta(@RequestParam String codigo, @RequestParam String nombreComun,
			@RequestParam String nombreCientifico, Model model) {
		boolean codigoCorrecto = serviciosPlanta.validarCodigo(codigo);
		if (!codigoCorrecto) {
			model.addAttribute("errorMessage", "CODIGO NO VALIDO");
			List<Planta> plantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", plantas);
			return "registrarPlantas";
		}

		if (nombreComun.isBlank() || nombreCientifico.isBlank()) {
			model.addAttribute("errorMessage", "El nombre común y el nombre científico no pueden estar vacíos.");
			List<Planta> plantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", plantas);
			return "registrarPlantas";
		}

		Planta planta = new Planta(codigo, nombreComun, nombreCientifico);

		boolean valido = serviciosPlanta.validarPlanta(planta);
		if (!valido) {
			List<Planta> plantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", plantas);
			model.addAttribute("errorMessage", "Los datos que has introducido no son correctos.");
			return "registrarPlantas";
		}

		try {
			serviciosPlanta.insertar(planta);
			List<Planta> plantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", plantas);
			model.addAttribute("successMessage", "Planta registrada correctamente.");
		} catch (DataIntegrityViolationException e) {
			List<Planta> plantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", plantas);
			model.addAttribute("errorMessage", "El código de la planta ya existe.");
		}
		return "registrarPlantas";
	}

	@GetMapping("/modificaciones")
	public String modificaciones(Model model) {
		List<Planta> listaPlantas = serviciosPlanta.findAll();
		model.addAttribute("plantas", listaPlantas);
		return "modificaciones";
	}

	@PostMapping("/modificarNombreComun")
	public String modificarNombreComun(@RequestParam String codigo, @RequestParam String nuevoNombreComun, Model model) {
		if (nuevoNombreComun.isBlank()) {
			model.addAttribute("errorNombreComun", "El nombre común no puede estar vacío.");
			model.addAttribute("plantas", serviciosPlanta.findAll());
			return "modificaciones";
		}

		boolean encontrado = serviciosPlanta.codigoExistente(codigo);
		if (!encontrado) {
			model.addAttribute("errorNombreComun", "Código no encontrado.");
			model.addAttribute("plantas", serviciosPlanta.findAll());
			return "modificaciones";
		}

		try {
			boolean actualizado = serviciosPlanta.actualizarNombreComun(codigo, nuevoNombreComun);
			if (actualizado) {
				model.addAttribute("successNombreComun", "Nombre Común ACTUALIZADO.");
			} else {
				model.addAttribute("errorNombreComun", "Fallo en la actualización del nombre común.");
			}
		} catch (DataIntegrityViolationException e) {
			model.addAttribute("errorNombreComun", "Error al actualizar el nombre común.");
		}

		model.addAttribute("plantas", serviciosPlanta.findAll());
		return "modificaciones";
	}

	@PostMapping("/modificarNombreCientifico")
	public String modificarNombreCientifico(@RequestParam String codigo, @RequestParam String nuevoNombreCientifico,
			Model model) {
		if (nuevoNombreCientifico.isBlank()) {
			model.addAttribute("errorNombreCientifico", "El nombre científico no puede estar vacío.");
			model.addAttribute("plantas", serviciosPlanta.findAll());
			return "modificaciones";
		}

		boolean encontrado = serviciosPlanta.codigoExistente(codigo);
		if (!encontrado) {
			model.addAttribute("errorNombreCientifico", "Código no encontrado.");
			model.addAttribute("plantas", serviciosPlanta.findAll());
			return "modificaciones";
		}

		try {
			boolean actualizado = serviciosPlanta.actualizarNombreCientifico(codigo, nuevoNombreCientifico);
			if (actualizado) {
				model.addAttribute("successNombreCientifico", "Nombre Científico ACTUALIZADO.");
			} else {
				model.addAttribute("errorNombreCientifico", "Fallo en la actualización del nombre científico.");
			}
		} catch (DataIntegrityViolationException e) {
			model.addAttribute("errorNombreCientifico", "Error al actualizar el nombre científico.");
		}

		model.addAttribute("plantas", serviciosPlanta.findAll());
		return "modificaciones";
	}
}
