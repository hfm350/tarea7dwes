package com.hfm350.tarea3dweshfm350.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

import com.hfm350.tarea3dweshfm350.modelo.Cliente;
import com.hfm350.tarea3dweshfm350.modelo.Controlador;
import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.modelo.Sesion;
import com.hfm350.tarea3dweshfm350.modelo.Sesion.Perfil;
import com.hfm350.tarea3dweshfm350.repositorios.PlantaRepository;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCliente;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosEjemplar;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosMensaje;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPersona;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;

@Controller
public class HomeController {

	@Autowired
	private ServiciosEjemplar serviciosEjemplar;

	@Autowired
	private ServiciosPlanta serviciosPlanta;

	@Autowired
	private ServiciosPersona serviciosPersona;

	@Autowired
	private ServiciosCliente serviciosCliente;

	@Autowired
	private ServiciosMensaje serviciosMensaje;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PlantaRepository plantaRepo;

	@Autowired
	private ServiciosCredenciales serviciosCredenciales;

	@Autowired
	private Controlador controlador;

	@GetMapping("/inicioSesion")
	public String mostrarFormularioDeInicioDeSesion() {
		return "inicioSesion";
	}

	@GetMapping("/menuAdmin")
	public String mostrarAdminDashboard(Model model, HttpSession session) {
		String rol = (String) session.getAttribute("rol");
		String nombreUsuario = (String) session.getAttribute("nombreUsuario");

		if (rol == null || !rol.equals("ROLE_ADMIN")) {
			return "redirect:/inicioSesion";
		}

		if (nombreUsuario == null) {
			nombreUsuario = "ADMIN"; // Valor por defecto si está vacío
		}

		model.addAttribute("nombreUsuario", nombreUsuario);
		return "menuAdmin";
	}

	@GetMapping("/menuPersonal")
	public String mostrarPersonalDashboard(Model model, HttpSession session, Principal principal) {
		String rol = (String) session.getAttribute("rol");
		String nombreUsuario = (String) session.getAttribute("nombreUsuario");

		if (rol == null || !rol.equals("ROLE_PERSONAL")) { // Asegurar que coincida con Spring Security
			return "redirect:/inicioSesion";
		}

		if (nombreUsuario == null && principal != null) {
			nombreUsuario = principal.getName(); // Obtener el nombre del usuario autenticado
			session.setAttribute("nombreUsuario", nombreUsuario); // Guardarlo en la sesión
		}

		model.addAttribute("nombreUsuario", nombreUsuario);
		return "menuPersonal";
	}

	@PostMapping("/login")
	public String procesarLogin(@RequestParam("username") String usuario, 
	                            @RequestParam("password") String password,
	                            HttpSession session, RedirectAttributes redirectAttributes) {
	    if (serviciosCredenciales.autenticar(usuario, password)) {
	        session.setAttribute("nombreUsuario", usuario);
	        Optional<Credencial> credencialOpt = serviciosCredenciales.buscarPorUsuario(usuario);

	        if (credencialOpt.isPresent()) {
	            Credencial credencial = credencialOpt.get();
	            String rol = credencial.getRol();
	            session.setAttribute("rol", rol);

	            if ("ROLE_CLIENTE".equals(rol)) {
	                return "redirect:/menuCliente";
	            } else if ("ROLE_ADMIN".equals(rol)) {
	                return "redirect:/menuAdmin";
	            } else if ("ROLE_PERSONAL".equals(rol)) {
	                return "redirect:/menuPersonal";
	            }
	        }
	    }

	    redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos.");
	    return "redirect:/inicioSesion";
	}




	@GetMapping("/logout")
	public String cerrarSesion(HttpSession session) {
		session.invalidate(); // Elimina la sesión y todos sus atributos
		return "redirect:/inicioSesion";
	}

	@GetMapping("/")
	public String index(Model model, HttpSession session) {
		List<Planta> listaPlantas = plantaRepo.findAll();
		model.addAttribute("plantas", listaPlantas);
		model.addAttribute("perfil", session.getAttribute("perfil"));
		return "index";
	}

	@GetMapping("/gestionEjemplares")
	public String gestionEjemplar(Model model, HttpSession session) {
		String rol = (String) session.getAttribute("rol");
		model.addAttribute("rol", rol);
		return "/gestionEjemplares";
	}

	@GetMapping("/verPlantas")
	public String verPlantas(Model model, HttpSession session) {
		List<Planta> listaPlantas = plantaRepo.findAll();
		model.addAttribute("plantas", listaPlantas);
		model.addAttribute("perfil", session.getAttribute("perfil"));
		return "verPlantas";

	}

	@GetMapping("/insertarEjemplar")
	public String insertarEjemplar(Model model, HttpSession session) {
		List<Ejemplar> ejemplares = serviciosEjemplar.findAll();
		model.addAttribute("ejemplares", ejemplares);

		List<Planta> plantas = serviciosPlanta.findAll();

		for (Planta planta : plantas) {
			boolean tieneEjemplar = serviciosEjemplar.existsByPlanta(planta);
			if (!tieneEjemplar) {
				plantas.add(planta);
			}
		}

		if (plantas.isEmpty()) {
			model.addAttribute("errorMessagelistavacia", "No hay plantas disponibles para registrar ejemplares.");
		} else {
			model.addAttribute("plantasSinEjemplar", plantas);
		}

		model.addAttribute("ejemplares", serviciosEjemplar.findAll());
		model.addAttribute("plantasSinEjemplar", serviciosPlanta.findAll());
		return "insertarEjemplar"; // Devuelve la vista sin redirigir

	}

	@GetMapping("/gestionPlantas")
	public String gestionPlantas(Model model, HttpSession session) {
		model.addAttribute("perfil", session.getAttribute("perfil"));
		return "gestionPlantas";
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

	@GetMapping("/gestionMensajes")
	public String gestionMensajes(Model model, HttpSession session) {
		String rol = (String) session.getAttribute("rol");
		model.addAttribute("rol", rol); // Pasamos el rol a la vista

		List<Mensaje> mensajes = serviciosMensaje.findAll();
		List<Ejemplar> ejemplares = serviciosEjemplar.findAll();

		if (ejemplares.isEmpty()) {
			model.addAttribute("mensajeError", "No hay ejemplares registrados.");
			return "error";
		}

		model.addAttribute("mensajes", mensajes);
		model.addAttribute("ejemplares", ejemplares);
		return "gestionMensajes";
	}

	@PostMapping("/gestionMensajes")
	public String añadirMensaje(@RequestParam Long idEjemplar, @RequestParam String mensajeTexto, Model model) {
		Optional<Ejemplar> ejemplarOptional = serviciosEjemplar.buscarPorId(idEjemplar);
		if (!ejemplarOptional.isPresent()) {
			model.addAttribute("mensajeError", "Ejemplar no encontrado.");
			return "gestionMensajes";
		}

		Ejemplar ejemplar = ejemplarOptional.get();

		Long usuarioIdLong = controlador.getUsuarioAutenticado();
		if (usuarioIdLong == null) {
			model.addAttribute("errorMessagePersona", "Usuario no autenticado.");
			return "gestionMensajes";
		}

		Optional<Long> personaID = serviciosCredenciales.obtenerIdPersonaPorIdCredencial(usuarioIdLong);
		if (!personaID.isPresent()) {
			model.addAttribute("errorMessagePersona", "No se encuentra la persona asociada.");
			return "gestionMensajes";
		}

		Optional<Persona> personaOptional = serviciosPersona.buscarPorId(personaID.get());
		if (!personaOptional.isPresent()) {
			model.addAttribute("errorUsuario", "No se encuentra la persona con el ID obtenido.");
			return "gestionMensajes";
		}

		Persona persona = personaOptional.get();

		if (persona == null) {
			model.addAttribute("errorUsuario", "La persona no es válida.");
			return "gestionMensajes";
		}

		Mensaje mensaje = new Mensaje(LocalDateTime.now(), mensajeTexto, persona, ejemplar);
		serviciosMensaje.insertar(mensaje);

		model.addAttribute("mensajeExito", "Mensaje añadido correctamente.");
		List<Mensaje> mensajes = serviciosMensaje.findAll();
		model.addAttribute("mensajes", mensajes);

		return "gestionMensajes";
	}

	@GetMapping("/filtrarPorPersona")
	public String filtrarMensajesPorPersona(Model model) {
		List<Persona> personas = serviciosPersona.obtenerTodasLasPersonas();
		if (personas.isEmpty()) {
			model.addAttribute("mensajeError", "No hay personas registradas.");
			return "filtrarPorPersona";
		}

		model.addAttribute("personas", personas);
		return "filtrarPorPersona";
	}

	@PostMapping("/filtrarPorPersona")
	public String filtrarMensajesPorPersona(@RequestParam(required = false) Long idPersona, Model model) {

		if (idPersona == null) {
			model.addAttribute("mensajeError", "Por favor, seleccione una persona.");
			List<Persona> personas = serviciosPersona.obtenerTodasLasPersonas();
			model.addAttribute("personas", personas);
			return "filtrarPorPersona";
		}

		Optional<Persona> personaOptional = serviciosPersona.buscarPorId(idPersona);
		if (!personaOptional.isPresent()) {
			model.addAttribute("mensajeError", "Persona no encontrada.");
			List<Persona> personas = serviciosPersona.obtenerTodasLasPersonas();
			model.addAttribute("personas", personas);
			return "filtrarPorPersona";
		}

		Persona persona = personaOptional.get();
		List<Mensaje> mensajes = serviciosMensaje.buscarPorPersona(persona);

		if (mensajes.isEmpty()) {
			model.addAttribute("mensajeError", "No hay mensajes para esta persona.");
		}

		model.addAttribute("persona", persona);
		model.addAttribute("mensajes", mensajes);

		model.addAttribute("idPersonaSeleccionada", idPersona);
		model.addAttribute("personas", serviciosPersona.obtenerTodasLasPersonas());

		return "filtrarPorPersona";
	}

	@GetMapping("/menuCliente")
	public String mostrarClienteDashboard(Model model, HttpSession session) {
		String rol = (String) session.getAttribute("rol");
		String nombreUsuario = (String) session.getAttribute("nombreUsuario");

		System.out.println("⚡ Entrando a /menuCliente con rol: " + rol);

		if (rol == null || !rol.equals("ROLE_CLIENTE")) {
			return "redirect:/inicioSesion?error=true";
		}

		if (nombreUsuario == null) {
			nombreUsuario = "Cliente";
		}

		model.addAttribute("nombreUsuario", nombreUsuario);
		return "menuCliente";
	}


	@PostMapping("/registroCliente")
	public String registrarCliente(@ModelAttribute Cliente cliente,
	                               @RequestParam String username,
	                               @RequestParam String password,
	                               Model model,
	                               RedirectAttributes redirectAttributes) {

	    boolean valido = true;

	    // 🔍 **Validaciones**
	    if (cliente.getNombre().isEmpty() || !cliente.getNombre().matches("[A-Z][a-z]+(\\s[A-Z][a-z]+)*")) {
	        model.addAttribute("errorMessageNombre", "El nombre debe comenzar con mayúscula y solo contener letras.");
	        valido = false;
	    }


	    if (cliente.getEmail().isEmpty() || !cliente.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.com$")) {
	        model.addAttribute("errorMessageEmail", "El email debe tener el formato correcto.");
	        valido = false;
	    } else if (serviciosCliente.existeEmail(cliente.getEmail())) {
	        model.addAttribute("errorMessageEmail", "El email ya está registrado.");
	        valido = false;
	    }

	    if (username.isEmpty() || username.contains(" ")) {
	        model.addAttribute("errorMessageUsuario", "El usuario no puede contener espacios en blanco.");
	        valido = false;
	    } else if (serviciosCredenciales.existeUsuario(username)) {
	        model.addAttribute("errorMessageUsuario", "El usuario ya está en uso.");
	        valido = false;
	    }

	    if (password.isEmpty() || !password.matches("\\d{4}")) {
	        model.addAttribute("errorMessagePassword", "La contraseña debe tener exactamente 4 dígitos numéricos.");
	        valido = false;
	    }

	    if (cliente.getNifNie().isEmpty() || !cliente.getNifNie().matches("[0-9]{8}[A-Z]")) {
	        model.addAttribute("errorMessageNifNie", "El NIF/NIE debe tener 8 números seguidos de una letra mayúscula.");
	        valido = false;
	    } else if (serviciosCliente.existeNifNie(cliente.getNifNie())) {
	        model.addAttribute("errorMessageNifNie", "El NIF/NIE ya está registrado.");
	        valido = false;
	    }

	    if (cliente.getDireccion().isEmpty()) {
	        model.addAttribute("errorMessageDireccion", "La dirección no puede estar vacía.");
	        valido = false;
	    }

	    if (cliente.getTelefono().isEmpty() || !cliente.getTelefono().matches("\\d{9}")) {
	        model.addAttribute("errorMessageTelefono", "El teléfono debe tener exactamente 9 dígitos numéricos.");
	        valido = false;
	    }

	    // Si hay errores, volver a mostrar el formulario con los mensajes
	    if (!valido) {
	        return "registroCliente";
	    }

	    // Encriptar la contraseña
	    String encryptedPassword = passwordEncoder.encode(password);

	    // Crear Credencial y asociarla al Cliente
	    Credencial credencial = new Credencial();
	    credencial.setUsuario(username);
	    credencial.setPassword(encryptedPassword);
	    credencial.setRol("ROLE_CLIENTE");

	    cliente.setCredenciales(credencial);
	    credencial.setCliente(cliente);

	    // Guardar Cliente (cascada guarda Credencial)
	    serviciosCliente.guardar2(cliente);

	    // Redirigir con mensaje de éxito
	    redirectAttributes.addFlashAttribute("successMessage", "Cliente registrado exitosamente.");
	    return "redirect:/inicioSesion";
	}



	@GetMapping("/registroCliente")
	public String mostrarFormularioRegistro(Model model) {
		model.addAttribute("cliente", new Cliente()); // Asegurar que el modelo tiene un objeto Cliente
		return "registroCliente"; // Nombre de la vista HTML
	}

}
