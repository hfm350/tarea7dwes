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

@Controller
public class PlantasController {

	@Autowired
	private ServiciosPlanta serviciosPlanta;

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
		

		Planta planta = new Planta(codigo, nombreComun, nombreCientifico);

		boolean valido = serviciosPlanta.validarPlanta(planta);
		if (!valido) {
			List<Planta> plantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", plantas);
			model.addAttribute("errorMessage", "Los datos que has introducido no son correctos");
			return "registrarPlantas";
		}

		try {
			serviciosPlanta.insertar(planta);
			List<Planta> plantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", plantas);
			model.addAttribute("successMessage", "Planta registrada correctamente");
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
	public String modificarNombreComun(@RequestParam String codigo, @RequestParam String nuevoNombreComun,
			Model model) {
		boolean encontrado = serviciosPlanta.codigoExistente(codigo);
		if (!encontrado) {
			model.addAttribute("errorNombreComun", "Codigo no encontrado");
			List<Planta> listaPlantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", listaPlantas);
			return "modificaciones";
		}
		boolean nuevo = serviciosPlanta.actualizarNombreComun(codigo, nuevoNombreComun);
		if (nuevo) {
			model.addAttribute("successNombreComun", "Nombre Común ACTUALIZADO");
			List<Planta> listaPlantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", listaPlantas);
		} else {
			model.addAttribute("errorNombreComun", "Fallo en la ACTUALIZACION del nombre Comun");
			List<Planta> listaPlantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", listaPlantas);
		}
		return "modificaciones";
	}

	@PostMapping("/modificarNombreCientifico")
	public String modificarNombreCientificio(@RequestParam String codigo, @RequestParam String nuevoNombreCientifico,
			Model model) {
		boolean encontrado = serviciosPlanta.codigoExistente(codigo);
		if (!encontrado) {
			model.addAttribute("errorNombreCientifico", "Codigo no encontrado");
			List<Planta> listaPlantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", listaPlantas);
			return "modificaciones";
		}
		boolean nuevo = serviciosPlanta.actualizarNombreCientifico(codigo, nuevoNombreCientifico);
		if (nuevo) {
			model.addAttribute("successNombreCientifico", "Nombre Cientifico ACTUALIZADO");
			List<Planta> listaPlantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", listaPlantas);
		} else {
			model.addAttribute("errorNombreCientifico", "Fallo en la ACTUALIZACION del nombre Cientifico");
			List<Planta> listaPlantas = serviciosPlanta.findAll();
			model.addAttribute("plantas", listaPlantas);
		}
		return "modificaciones";
	}

	
	
}
