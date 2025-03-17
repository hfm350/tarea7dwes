package com.hfm350.tarea3dweshfm350.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hfm350.tarea3dweshfm350.modelo.Controlador;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosEjemplar;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosMensaje;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPersona;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;

@Controller
public class MensajeController {

	@Autowired
	private ServiciosMensaje servMensaje;
	@Autowired
	private ServiciosPlanta servPlanta;
	@Autowired
	private ServiciosEjemplar serviciosEjemplar;
	
	@Autowired
	private ServiciosMensaje serviciosMensaje;
	@Autowired
	private ServiciosCredenciales serviciosCredenciales;
	@Autowired
	private ServiciosPersona serviciosPersona;
	
	@Autowired
	private Controlador controlador;
	
	@GetMapping("/gestionMensajes")
	public String gestionMensajes(Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
	        return "redirect:/inicioSesion"; // Redirigir al login si no está autenticado
	    }

	    String usuarioAutenticado = authentication.getName();
	    boolean esAdmin = authentication.getAuthorities().stream()
	        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

	    // Obtener los mensajes y ejemplares
	    List<Mensaje> mensajes = serviciosMensaje.findAll();
	    List<Ejemplar> ejemplares = serviciosEjemplar.findAll();

	    model.addAttribute("mensajes", mensajes);
	    model.addAttribute("ejemplares", ejemplares);
	    model.addAttribute("rol", esAdmin ? "ROLE_ADMIN" : "ROLE_PERSONAL"); // Se pasa correctamente el rol

	    return "gestionMensajes";
	}



	@PostMapping("/gestionMensajes")
	public String añadirMensaje(@RequestParam Long idEjemplar, 
	                             @RequestParam String mensajeTexto, 
	                             Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    // Verificar si el usuario está autenticado
	    if (authentication == null || !authentication.isAuthenticated() || 
	        "anonymousUser".equals(authentication.getPrincipal())) {
	        model.addAttribute("mensajeError", "Usuario no autenticado. Inicie sesión nuevamente.");
	        return "redirect:/inicioSesion"; // Redirigir al login si la sesión expiró
	    }

	    String usuarioAutenticado = authentication.getName();
	    System.out.println("✅ Usuario autenticado: " + usuarioAutenticado);

	    // Buscar la persona asociada al usuario autenticado
	    Optional<Persona> personaOptional = serviciosCredenciales.buscarPersonaPorUsuario2(usuarioAutenticado);
	    if (personaOptional.isEmpty()) {
	        model.addAttribute("mensajeError", "No se encuentra la persona asociada.");
	        return "gestionMensajes";
	    }

	    Persona persona = personaOptional.get();

	    // Buscar el ejemplar
	    Optional<Ejemplar> ejemplarOptional = serviciosEjemplar.buscarPorId(idEjemplar);
	    if (ejemplarOptional.isEmpty()) {
	        model.addAttribute("mensajeError", "Ejemplar no encontrado.");
	        return "gestionMensajes";
	    }

	    Ejemplar ejemplar = ejemplarOptional.get();

	    // Crear y guardar el mensaje
	    Mensaje mensaje = new Mensaje(LocalDateTime.now(), mensajeTexto, persona, ejemplar);
	    serviciosMensaje.insertar(mensaje);

	    // Agregar mensaje de éxito
	    model.addAttribute("mensajeExito", "Mensaje añadido correctamente.");
	    model.addAttribute("ejemplares", serviciosEjemplar.findAll());
	    model.addAttribute("mensajes", serviciosMensaje.findAll());

	    return "gestionMensajes";
	}


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

	    // Validamos que cada mensaje tenga persona o cliente
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