package com.hfm350.tarea3dweshfm350.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
            nombreUsuario = "ADMIN";  // Valor por defecto si está vacío
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
            nombreUsuario = principal.getName();  // Obtener el nombre del usuario autenticado
            session.setAttribute("nombreUsuario", nombreUsuario); // Guardarlo en la sesión
        }

        model.addAttribute("nombreUsuario", nombreUsuario);
        return "menuPersonal";
    }

    

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("username") String usuario,
            @RequestParam("password") String password,
            Model model,
            HttpSession session) {

        if (serviciosCredenciales.autenticar(usuario, password)) {
            session.setAttribute("nombreUsuario", usuario);

            Optional<Credencial> idUsuario = serviciosCredenciales.buscarPersonaPorId(usuario);
            if (idUsuario.isPresent()) {
                Credencial credencial = idUsuario.get();
                Perfil perfil = "admin".equalsIgnoreCase(usuario) ? Perfil.ADMIN : Perfil.PERSONAL;
                session.setAttribute("perfil", perfil);

                return perfil == Perfil.ADMIN ? "redirect:/menuAdmin" : "redirect:/menuPersonal";
            }
        }
        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "inicioSesion";
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
        model.addAttribute("perfil", session.getAttribute("perfil"));
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
        List<Planta> plantasSinEjemplar = new ArrayList<>();

        for (Planta planta : plantas) {
            boolean tieneEjemplar = serviciosEjemplar.existsByPlanta(planta);
            if (!tieneEjemplar) { 
                plantasSinEjemplar.add(planta);
            }
        }

        if (plantasSinEjemplar.isEmpty()) {
            model.addAttribute("errorMessagelistavacia", "No hay plantas disponibles para registrar ejemplares.");
        } else {
            model.addAttribute("plantasSinEjemplar", plantasSinEjemplar);
        }

        return "insertarEjemplar";
    }

    @GetMapping("/gestionPlantas")
    public String gestionPlantas(Model model, HttpSession session) {
        model.addAttribute("perfil", session.getAttribute("perfil"));
        return "gestionPlantas";
    }

    @PostMapping("/insertarEjemplar")
    public String insertarEjemplar(@RequestParam String codigoPlanta, 
                                   @RequestParam String nombreEjemplar,
                                   @RequestParam String mensaje, 
                                   Model model,
                                   HttpSession session) {

        if (nombreEjemplar.trim().isEmpty()) {
            model.addAttribute("errorMessage", "El nombre del ejemplar no puede estar vacío.");
            return "insertarEjemplar";
        }

        if (mensaje.trim().isEmpty()) {
            model.addAttribute("errorMessage", "El mensaje no puede estar vacío.");
            return "insertarEjemplar";
        }

        Planta planta = serviciosPlanta.buscarPorCodigo(codigoPlanta);
        if (planta == null) {
            model.addAttribute("errorMessage", "El código de la planta no es válido.");
            return "insertarEjemplar";
        }

        Ejemplar ejemplar = serviciosEjemplar.insertar(nombreEjemplar, codigoPlanta);
        if (ejemplar == null) {
            model.addAttribute("errorMessage", "Error al insertar el ejemplar.");
            return "insertarEjemplar";
        }

        Long usuarioIdLong = controlador.getUsuarioAutenticado();
        if (usuarioIdLong == null) {
            model.addAttribute("errorMessagePersona", "Usuario no autenticado.");
            return "insertarEjemplar";
        }

        Optional<Long> personaID = serviciosCredenciales.obtenerIdPersonaPorIdCredencial(usuarioIdLong);
        if (!personaID.isPresent()) {
            model.addAttribute("errorMessagePersona", "No se encuentra la persona asociada.");
            return "insertarEjemplar";
        }

        Optional<Persona> personaOptional = serviciosPersona.buscarPorId(personaID.get());
        if (!personaOptional.isPresent()) {
            model.addAttribute("errorUsuario", "No se encuentra la persona con el ID obtenido");
            return "insertarEjemplar";
        }

        Persona persona = personaOptional.get();
        LocalDateTime tiempo = LocalDateTime.now();
        Mensaje nuevoMensaje = new Mensaje(tiempo, mensaje, persona, ejemplar);
        serviciosMensaje.insertar(nuevoMensaje);

        model.addAttribute("successMessage", "Ejemplar y mensaje registrados exitosamente");

        return "redirect:/insertarEjemplar";
    }
    
    @GetMapping("/gestionMensajes")
	public String gestionMensajes(Model model) {
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

	@GetMapping("/registroCliente")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new Cliente()); // Crear un objeto Cliente vacío para el formulario
        return "registroCliente"; // Este es el formulario HTML donde se va a ingresar la información del cliente
    }

    @PostMapping("/registroCliente")
    public String registrarCliente(@ModelAttribute Cliente cliente, 
                                   @RequestParam String username,
                                   @RequestParam String password, 
                                   Model model, 
                                   RedirectAttributes redirectAttributes) {

        // Validación básica de los campos
        if (cliente.getNombre().trim().isEmpty()) {
            model.addAttribute("errorMessage", "El nombre completo es obligatorio.");
            return "registroCliente";
        }

        if (cliente.getEmail().trim().isEmpty()) {
            model.addAttribute("errorMessage", "El correo electrónico es obligatorio.");
            return "registroCliente";
        }

        if (!cliente.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("errorMessage", "El correo electrónico no tiene un formato válido.");
            return "registroCliente";
        }

        if (cliente.getFechaNac() == null) {
            model.addAttribute("errorMessage", "La fecha de nacimiento es obligatoria.");
            return "registroCliente";
        }

        // Verificación si el username ya existe
        if (serviciosCredenciales.existeUsuario(username)) {
            model.addAttribute("errorMessage", "El nombre de usuario ya está en uso.");
            return "registroCliente";
        }

        // Verificación si el NIF/NIE ya existe
        if (serviciosCliente.existeNifNie(cliente.getNifNie())) {
            model.addAttribute("errorMessage", "El NIF/NIE ya está registrado.");
            return "registroCliente";
        }

        // Crear las credenciales y registrar el cliente
        Credencial credencial = new Credencial(username, password);
        serviciosCredenciales.insertar(username, password, null);

        // Crear la persona asociada al cliente
        Persona persona = new Persona(cliente.getNombre(), cliente.getEmail());
        serviciosPersona.insertar(persona);

        // Asociar credenciales al cliente
        cliente.setCredenciales(credencial);

        // Registrar el cliente en la base de datos
        serviciosCliente.guardar(cliente);

        // Mensaje de éxito y redirección
        redirectAttributes.addFlashAttribute("successMessage", "Cliente registrado exitosamente.");
        return "redirect:/inicioSesion"; 
    }
    
    @GetMapping("/menuCliente")
    public String mostrarClienteDashboard(Model model, HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        String nombreUsuario = (String) session.getAttribute("nombreUsuario");

        if (rol == null || !rol.equals("ROLE_CLIENTE")) { 
            return "redirect:/inicioSesion"; 
        }

        if (nombreUsuario == null) {
            nombreUsuario = "Cliente";  // Valor por defecto si está vacío
        }

        model.addAttribute("nombreUsuario", nombreUsuario);
        return "menuCliente";
    }
	
}
