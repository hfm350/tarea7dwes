package com.hfm350.tarea3dweshfm350.controller;

import java.rmi.server.LoaderHandler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.query.sqm.tree.SqmDeleteOrUpdateStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hfm350.tarea3dweshfm350.modelo.Controlador;
import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosEjemplar;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosMensaje;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPersona;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;

import jakarta.servlet.http.HttpSession;

@Controller
public class EjemplarController {

	@Autowired
	private ServiciosEjemplar serviciosEjemplar;

	@Autowired
	private ServiciosMensaje serviciosMensaje;
	
	@Autowired
	private ServiciosPlanta serviciosPlanta;
	
	@Autowired
	private ServiciosCredenciales serviciosCredenciales;
	
	@Autowired
	private ServiciosPersona serviciosPersona;
	
	@GetMapping("/gestionEjemplares")
	public String gestionEjemplar(Model model, HttpSession session) {
		String rol = (String) session.getAttribute("rol");
		model.addAttribute("rol", rol);
		return "/gestionEjemplares";
	}

	@GetMapping("/verMensajesEjemplar")
    public String mostrarFormulario(Model model) {
        List<Ejemplar> ejemplares = serviciosEjemplar.findAll();
        model.addAttribute("ejemplares", ejemplares);
        return "verMensajesEjemplar"; 
    }

    
    @PostMapping("/verMensajesEjemplar")
    public String procesarMensajes(@RequestParam Long codigoEjemplar, Model model) {
        List<Ejemplar> ejemplares = serviciosEjemplar.findAll();
        List<Mensaje> mensajes = serviciosMensaje.findAll();
        boolean ejemplarEncontrado = false;
        boolean mensajeEncontrado = false;
        List<Mensaje> mensajesEjemplar = new ArrayList<>();

      
        for (Ejemplar ejemplar : ejemplares) {
            if (ejemplar.getId().equals(codigoEjemplar)) {
                ejemplarEncontrado = true;

               
                for (Mensaje mensaje : mensajes) {
                    if (mensaje.getEjemplar().getId().equals(codigoEjemplar)) {
                        mensajesEjemplar.add(mensaje);
                        mensajeEncontrado = true;
                    }
                }
                break;
            }
        }

        model.addAttribute("ejemplares", ejemplares);
        model.addAttribute("codigoEjemplar", codigoEjemplar);

        if (!ejemplarEncontrado) {
            model.addAttribute("errorMensaje", "CODIGO no registrado en la base de datos");
        } else if (!mensajeEncontrado) {
            model.addAttribute("errorMensaje", "No existe ningun ejemplar para ese codigo");
        } else {
            model.addAttribute("mensajes", mensajesEjemplar);
        }

        return "verMensajesEjemplar"; 
    }
    
    
    
    @GetMapping("/ejemplarDePlanta")
    public String mostrarPlantas(Model model) {
        List<Planta> listaDePlantas = serviciosPlanta.findAll();
        model.addAttribute("plantas", listaDePlantas);
        return "ejemplarDePlanta";
    }

    @PostMapping("/ejemplarDePlanta")
    public String verEjemplares(@RequestParam(value = "codigoPlantas", required = false) List<String> codigosSeleccionados, Model model) {
        List<Planta> listaDePlantas = serviciosPlanta.findAll();
        model.addAttribute("plantas", listaDePlantas);

        if (codigosSeleccionados == null || codigosSeleccionados.isEmpty()) {
            model.addAttribute("mensajeError", "Debes seleccionar al menos una planta.");
            return "ejemplarDePlanta";
        }

        model.addAttribute("codigoBuscado", String.join(", ", codigosSeleccionados));
        List<Ejemplar> ejemplaresTotales = new ArrayList<>();

        for (String codigo : codigosSeleccionados) {
            Planta planta = listaDePlantas.stream()
                    .filter(p -> p.getCodigo().equalsIgnoreCase(codigo.trim()))
                    .findFirst()
                    .orElse(null);

            if (planta != null) {
                List<Ejemplar> ejemplaresDisponibles = serviciosEjemplar.findAll()
                        .stream()
                        .filter(ej -> ej.getPlanta().getId().equals(planta.getId()))
                        .filter(Ejemplar::isDisponible)
                        .collect(Collectors.toList());

                ejemplaresTotales.addAll(ejemplaresDisponibles);
            }
        }

        
        if (ejemplaresTotales.isEmpty()) {
            model.addAttribute("mensajeError", "No hay EJEMPLARES DISPONIBLES para las plantas seleccionadas.");
        } else {
            model.addAttribute("ejemplares", ejemplaresTotales);
        }

        return "ejemplarDePlanta";
    }



    
    @PostMapping("/agregarEjemplar")
    public String agregarEjemplar(@RequestParam("codigoPlanta") String codigoPlanta, Model model) {
        Ejemplar ejemplar = serviciosEjemplar.insertar(codigoPlanta);

        if (ejemplar == null) {
            model.addAttribute("mensajeError", "No se pudo crear el ejemplar. Verifique el código de la planta.");
        } else {
            model.addAttribute("mensajeExito", "Ejemplar creado con éxito: " + ejemplar.getNombre());
        }

        return "redirect:/ejemplarDePlanta";
    }

    @GetMapping("/insertarEjemplar")
	public String insertarEjemplar(Model model, HttpSession session) {
    	List<Ejemplar> ejemplaresDisponibles = serviciosEjemplar.findAll()
    	        .stream()
    	        .filter(Ejemplar::isDisponible) // Filtrar solo los disponibles
    	        .collect(Collectors.toList());
		
		List<Planta> plantas = serviciosPlanta.findAll();
		List<Planta> plantasSinEjemplar = new ArrayList<>();

		for (Planta planta : plantas) {
		    boolean tieneEjemplar = serviciosEjemplar.existsByPlanta(planta);
		    if (!tieneEjemplar) {
		        plantasSinEjemplar.add(planta); // Agregar a la nueva lista
		    }
		}


		if (plantas.isEmpty()) {
			model.addAttribute("errorMessagelistavacia", "No hay plantas disponibles para registrar ejemplares.");
		} else {
			model.addAttribute("plantasSinEjemplar", plantas);
		}

		model.addAttribute("ejemplares", ejemplaresDisponibles);
		model.addAttribute("plantasSinEjemplar", serviciosPlanta.findAll());
		return "insertarEjemplar"; // Devuelve la vista sin redirigir

	}

	

	@PostMapping("/insertarEjemplar")
	public String insertarEjemplar(@RequestParam String codigoPlanta,

			RedirectAttributes redirectAttributes, HttpSession session) {

		// Verificar si el usuario está autenticado en Spring Security
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication.getPrincipal().equals("anonymousUser")) {
			redirectAttributes.addFlashAttribute("errorMessagePersona", "Usuario no autenticado.");
			System.out.println("⚠️ Usuario no autenticado. Redirigiendo...");
			return "redirect:/insertarEjemplar";
		}

		String usuarioAutenticado = authentication.getName(); // Nombre de usuario autenticado
		System.out.println("✅ Usuario autenticado: " + usuarioAutenticado);

		// Buscar la planta por su código
		Planta planta = serviciosPlanta.buscarPorCodigo(codigoPlanta);
		if (planta == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "El código de la planta no es válido.");
			return "redirect:/insertarEjemplar";
		}

		// Insertar el ejemplar automáticamente generando su nombre
		Ejemplar ejemplar = serviciosEjemplar.insertar(codigoPlanta);
		if (ejemplar == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al insertar el ejemplar.");
			return "redirect:/insertarEjemplar";
		}

		// Obtener ID del usuario autenticado desde la credencial
		Optional<Credencial> credencialOpt = serviciosCredenciales.buscarPorUsuario(usuarioAutenticado);
		if (credencialOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessagePersona", "No se encontró la credencial del usuario.");
			return "redirect:/insertarEjemplar";
		}

		Long personaID = serviciosCredenciales.obtenerIdPersonaPorIdCredencial(credencialOpt.get().getId())
				.orElseThrow(() -> new RuntimeException("No se encuentra la persona asociada."));

		Persona persona = serviciosPersona.buscarPorId(personaID)
				.orElseThrow(() -> new RuntimeException("No se encuentra la persona con el ID obtenido."));

		// Registrar el mensaje asociado al ejemplar y la persona
		LocalDateTime tiempo = LocalDateTime.now();
		String fechaFormateada = tiempo.format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
		String mensajeGenerado = "Ejemplar añadido por " + persona.getNombre() + " a las " + fechaFormateada + ".";
		Mensaje nuevoMensaje = new Mensaje(tiempo, mensajeGenerado, persona, ejemplar);
		serviciosMensaje.insertar(nuevoMensaje);

		// Mensaje de éxito
		redirectAttributes.addFlashAttribute("successMessage", "Ejemplar y mensaje registrados exitosamente.");

		System.out.println("✅ Ejemplar y mensaje guardados con éxito para el usuario: " + usuarioAutenticado);

		return "redirect:/insertarEjemplar";
	}

}
