package com.hfm350.tarea3dweshfm350.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosMensaje;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;

@Controller
public class MensajeController {

	@Autowired
	private ServiciosMensaje servMensaje;
	@Autowired
	private ServiciosPlanta servPlanta;

	@GetMapping("/filtrarPorFecha")
	public String mostrarFormularioFiltrarFecha() {
		return "filtrarPorFecha";
	}

	@PostMapping("/filtrarPorFecha")
	public String filtrarMensajesPorRangoFechas(@RequestParam("fechaInicio") String fechaInicioStr,
			@RequestParam("fechaFin") String fechaFinStr, Model model) {
		try {
			LocalDateTime fechaInicio = LocalDateTime.parse(fechaInicioStr + "T00:00:00");
			LocalDateTime fechaFin = LocalDateTime.parse(fechaFinStr + "T23:59:59");

			List<Mensaje> mensajes = servMensaje.buscarPorFechas(fechaInicio, fechaFin);

			if (mensajes.isEmpty()) {
				model.addAttribute("mensajeError", "No hay mensajes en estas fechas.");
			} else {
				model.addAttribute("mensajes", mensajes);
				model.addAttribute("fechaInicio", fechaInicioStr);
				model.addAttribute("fechaFin", fechaFinStr);
			}

		} catch (Exception e) {
			model.addAttribute("mensajeError", "Error en las fechas. Usa el formato YYYY-MM-DD.");
		}

		return "filtrarPorFecha";
	}

	@GetMapping("/filtrarPorPlanta")
	public String mostrarFormularioFiltrarPorPlanta(Model model) {
		List<Planta> plantas = servPlanta.findAll();
		if (plantas.isEmpty()) {
			model.addAttribute("mensajeError", "No hay plantas registradas.");
		} else {
			model.addAttribute("plantas", plantas);
		}
		return "filtrarPorPlanta";
	}

	@PostMapping("/filtrarPorPlanta")
	public String filtrarMensajesPorPlanta(@RequestParam(required = false) String codigoPlanta, Model model) {
	    if (codigoPlanta == null || codigoPlanta.trim().isEmpty()) {
	        model.addAttribute("mensajeError", "Debe seleccionar una planta.");
	        model.addAttribute("plantas", servPlanta.findAll());
	        return "filtrarPorPlanta";
	    }

	    Planta planta = servPlanta.buscarPorCodigo(codigoPlanta.toUpperCase());
	    if (planta == null) {
	        model.addAttribute("mensajeError", "Planta no encontrada.");
	        model.addAttribute("plantas", servPlanta.findAll());
	        return "filtrarPorPlanta";
	    }

	    List<Mensaje> mensajes = servMensaje.buscarPorPlanta(planta);

	    // 🔹 Validamos que cada mensaje tenga persona o cliente
	    for (Mensaje mensaje : mensajes) {
	        if (mensaje.getPersona() == null && mensaje.getCliente() == null) {
	            //mensaje.setPersona(new Persona("Desconocido")); // Crear un objeto persona temporal
	        }
	    }

	    if (mensajes.isEmpty()) {
	        model.addAttribute("mensajeError", "No hay mensajes para esta planta.");
	    } else {
	        model.addAttribute("mensajes", mensajes);
	    }

	    model.addAttribute("planta", planta);
	    model.addAttribute("plantas", servPlanta.findAll());
	    model.addAttribute("codigoPlantaSeleccionada", codigoPlanta);

	    return "filtrarPorPlanta";
	}



}