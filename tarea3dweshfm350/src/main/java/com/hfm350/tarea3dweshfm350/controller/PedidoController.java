package com.hfm350.tarea3dweshfm350.controller;

import com.hfm350.tarea3dweshfm350.modelo.Pedido;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.repositorios.EjemplarRepository;
import com.hfm350.tarea3dweshfm350.repositorios.MensajeRepository;
import com.hfm350.tarea3dweshfm350.modelo.Cliente;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.servicios.ServicioPedido;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosEjemplar;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class PedidoController {

    private final ServicioPedido servicioPedido;
    private final ServiciosEjemplar servicioEjemplar;
    private final ServiciosCredenciales serviciosCredenciales;
    private final ServiciosPlanta servicioPlanta;
    private final EjemplarRepository ejemplarRepo;
	private final MensajeRepository mensajeRepo;

    public PedidoController(ServicioPedido servicioPedido, ServiciosEjemplar servicioEjemplar,
                            ServiciosCredenciales serviciosCredenciales, ServiciosPlanta servicioPlanta,
                            EjemplarRepository ejemplarRepo, MensajeRepository mensajeRepo) {
        this.servicioPedido = servicioPedido;
        this.servicioEjemplar = servicioEjemplar;
        this.serviciosCredenciales = serviciosCredenciales;
        this.servicioPlanta = servicioPlanta;
        this.ejemplarRepo = ejemplarRepo;
        this.mensajeRepo = mensajeRepo;
    }

    @GetMapping("/carrito")
    public String verCarrito(HttpServletRequest request, Model model) {
        List<Long> ids = (List<Long>) request.getSession().getAttribute("carrito");
        List<Ejemplar> ejemplares = new ArrayList<>();

        if (ids != null && !ids.isEmpty()) {
            ejemplares = ejemplarRepo.findAllById(ids);
        }

        model.addAttribute("carrito", ejemplares);
        return "carrito";
    }

    @PostMapping("/carrito/eliminar")
    public String eliminarDelCarrito(@RequestParam Long ejemplarId, HttpServletRequest request) {
        List<Long> carrito = (List<Long>) request.getSession().getAttribute("carrito");

        if (carrito != null) {
            carrito.removeIf(id -> id.equals(ejemplarId));
            request.getSession().setAttribute("carrito", carrito);
        }

        return "redirect:/carrito";
    }

    @PostMapping("/carrito/confirmar")
    public String confirmarCarrito(HttpServletRequest request, Model model) {
        List<Long> carritoIds = (List<Long>) request.getSession().getAttribute("carrito");

        if (carritoIds == null || carritoIds.isEmpty()) {
            model.addAttribute("errorMensaje", "El carrito está vacío.");
            model.addAttribute("carrito", new ArrayList<>());
            return "carrito";
        }


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Long clienteId = serviciosCredenciales.obtenerIdClientePorUsuario(username);
        Cliente cliente = serviciosCredenciales.obtenerClientePorId(clienteId);

        List<Ejemplar> ejemplares = ejemplarRepo.findAllById(carritoIds);
        List<Ejemplar> ejemplaresAsignados = new ArrayList<>();

        for (Ejemplar ej : ejemplares) {
            if (ej.getPedido() == null) {
                ejemplaresAsignados.add(ej);
            }
        }

        if (ejemplaresAsignados.isEmpty()) {
            request.setAttribute("errorMensaje", "Algunos ejemplares ya no están disponibles.");
            request.setAttribute("carrito", ejemplares);
            return "carrito";
        }

        // Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(new Date());
        pedido = servicioPedido.guardarPedido2(pedido);

        for (Ejemplar ej : ejemplaresAsignados) {
            ej.setDisponible(false);
            ej.setPedido(pedido);
            ejemplarRepo.save(ej);
        }

        pedido.setEjemplares(ejemplaresAsignados);
        servicioPedido.guardarPedido2(pedido);

        // 🔽 Añadir mensaje por cada ejemplar comprado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime fechaMensaje = pedido.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        for (Ejemplar ej : ejemplaresAsignados) {
            String mensajeTexto = "El cliente " + cliente.getNombre() +
                    " compró el ejemplar " + ej.getId() +
                    " el día " + formatter.format(fechaMensaje) +
                    " en el pedido " + pedido.getId() + ".";

            Mensaje mensaje = new Mensaje();
            mensaje.setTiempo(LocalDateTime.now());
            mensaje.setMensaje(mensajeTexto);
            mensaje.setCliente(cliente);
            mensaje.setPedido(pedido);
            mensaje.setEjemplar(ej);

            mensajeRepo.save(mensaje);
        }

        request.getSession().removeAttribute("carrito");

        return "redirect:/carrito?exito";
    }



    @GetMapping("/realizarPedido")
    public String mostrarPlantasDisponibles(Model model) {
        List<Map<String, Object>> plantas = servicioEjemplar.obtenerPlantasConEjemplares();
        model.addAttribute("plantas", plantas);
        return "realizarPedido";
    }

    @PostMapping("/realizarPedido")
    public String realizarPedido(
            @RequestParam(value = "plantasSeleccionadas[]", required = false) List<Long> plantasIds,
            @RequestParam(value = "cantidadEjemplares[]", required = false) List<Integer> cantidades,
            HttpServletRequest request, Model model) {

        // Verifica si el usuario no ha seleccionado ninguna planta
        if (plantasIds == null || plantasIds.isEmpty()) {
        	model.addAttribute("errorMessage", "Debes seleccionar al menos una planta antes de continuar.");
        
        	List<Map<String, Object>> plantas = servicioEjemplar.obtenerPlantasConEjemplares();
            model.addAttribute("plantas", plantas);

            return "realizarPedido";
        
        }

        List<Long> carrito = (List<Long>) request.getSession().getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        for (int i = 0; i < plantasIds.size(); i++) {
            Long plantaId = plantasIds.get(i);
            Integer cantidad = cantidades.get(i);

            List<Ejemplar> ejemplares = servicioEjemplar.obtenerEjemplaresDisponiblesPorPlanta(plantaId, cantidad);
            for (Ejemplar ej : ejemplares) {
                carrito.add(ej.getId());
            }
        }

        request.getSession().setAttribute("carrito", carrito);
        return "redirect:/carrito";
    }


    @GetMapping("/misPedidos")
    public String verMisPedidos(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Verifica si el usuario está autenticado
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/inicioSesion"; // redirige si no está logueado
        }

        // Obtiene el nombre de usuario autenticado
        String usuarioAutenticado = authentication.getName();
        
        // Obtiene el ID del cliente asociado al usuario
        Long clienteId = serviciosCredenciales.obtenerIdClientePorUsuario(usuarioAutenticado);
        if (clienteId == null) {
            return "redirect:/menuCliente"; // si no tiene cliente asociado
        }

        // Consulta todos los pedidos confirmados del cliente
        List<Pedido> pedidosConfirmados = servicioPedido.obtenerPedidosPorCliente(clienteId);

        // Añade la lista de pedidos al modelo
        model.addAttribute("pedidos", pedidosConfirmados);

        return "misPedidos"; // debe existir el archivo misPedidos.html en /templates
    }
    
    @PostMapping("/eliminarPedido2")
    @Transactional
    public String eliminarPedido(@RequestParam Long idPedido, HttpServletRequest request) {
        // 🗑 Primero eliminar los mensajes asociados al pedido
        mensajeRepo.deleteByPedidoId(idPedido);

        // 🗑 Luego eliminar el pedido
        servicioPedido.eliminarPedido(idPedido);

        // ✅ Redirección
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("misPedidos")) {
            return "redirect:/misPedidos";
        }

        return "redirect:/carrito";
    }
    
    @GetMapping("/stock")
    public String verStock(Model model) {
        // Obtener todas las plantas
        List<Planta> plantas = servicioPlanta.obtenerTodasPlantas();

        // Para cada planta, obtén los ejemplares
        Map<Planta, List<Ejemplar>> stockPorPlanta = new LinkedHashMap<>();

        for (Planta planta : plantas) {
            List<Ejemplar> ejemplares = ejemplarRepo.findByPlanta(planta);
            stockPorPlanta.put(planta, ejemplares);
        }

        model.addAttribute("stockPorPlanta", stockPorPlanta);
        return "stock";
    }


    

   
}