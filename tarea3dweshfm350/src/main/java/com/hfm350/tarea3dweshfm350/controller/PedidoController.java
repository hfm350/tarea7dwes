package com.hfm350.tarea3dweshfm350.controller;

import com.hfm350.tarea3dweshfm350.modelo.Pedido;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.repositorios.EjemplarRepository;
import com.hfm350.tarea3dweshfm350.modelo.Cliente;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.servicios.ServicioPedido;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosEjemplar;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosEjemplar;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class PedidoController {

    private final ServicioPedido servicioPedido;
    private final ServiciosEjemplar servicioEjemplar;
    private final ServiciosCredenciales serviciosCredenciales;
    private final ServiciosPlanta servicioPlanta;
    private final EjemplarRepository ejemplarRepo;

    public PedidoController(ServicioPedido servicioPedido, ServiciosEjemplar servicioEjemplar, ServiciosCredenciales serviciosCredenciales, ServiciosPlanta servicioPlanta, EjemplarRepository ejemplarRepo) {
        this.servicioPedido = servicioPedido;
        this.servicioEjemplar = servicioEjemplar;
        this.serviciosCredenciales = serviciosCredenciales;
        this.servicioPlanta = servicioPlanta;
        this.ejemplarRepo = ejemplarRepo;
    }

    /**
     * Vista del carrito: Lista los pedidos actuales del cliente
     */
    @GetMapping("/carrito")
    public String verCarrito(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/inicioSesion";
        }

        // Obtener el usuario autenticado
        String usuarioAutenticado = authentication.getName();
        Long clienteId = serviciosCredenciales.obtenerIdClientePorUsuario(usuarioAutenticado);

        if (clienteId == null) {
            return "redirect:/menuCliente";
        }

        // Obtener los pedidos actualizados desde la BD
        List<Pedido> pedidos = servicioPedido.obtenerPedidosPorCliente(clienteId);

        // Filtrar solo pedidos NO confirmados
        List<Pedido> pedidosNoConfirmados = pedidos.stream()
            .filter(pedido -> !pedido.isConfirmado()) // Excluir los confirmados
            .toList();

        model.addAttribute("pedidos", pedidosNoConfirmados);

        return "carrito";
    }


    /**
     * Vista para realizar un nuevo pedido con selección de ejemplares
     */
    @GetMapping("/realizarPedido")
    public String mostrarPlantasDisponibles(Model model) {
        List<Map<String, Object>> plantas = servicioEjemplar.obtenerPlantasConEjemplares();
        model.addAttribute("plantas", plantas); // Asegurarse de que "plantas" se pasa correctamente
        return "realizarPedido";
    }

    

    /**
     * Método que crea un nuevo pedido con los ejemplares seleccionados
     */
    @PostMapping("/realizarPedido")
    public String realizarPedido(
            @RequestParam("plantasSeleccionadas[]") List<Long> plantasIds,
            @RequestParam("cantidadEjemplares[]") List<Integer> cantidades) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/inicioSesion";
        }

        String usuarioAutenticado = authentication.getName();
        Long clienteId = serviciosCredenciales.obtenerIdClientePorUsuario(usuarioAutenticado);

        if (clienteId == null) {
            return "redirect:/menuCliente";
        }

        Cliente cliente = serviciosCredenciales.obtenerClientePorId(clienteId);
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setCliente(cliente);
        nuevoPedido.setFecha(java.sql.Date.valueOf(LocalDate.now()));

        List<Ejemplar> ejemplaresSeleccionados = new ArrayList<>();

        for (int i = 0; i < plantasIds.size(); i++) {
            Long plantaId = plantasIds.get(i);
            Integer cantidad = cantidades.get(i);

            List<Ejemplar> ejemplaresDisponibles = servicioEjemplar.obtenerEjemplaresDisponiblesPorPlanta(plantaId, cantidad);

            // Aquí marcamos los ejemplares como NO disponibles antes de añadirlos al pedido
            for (Ejemplar ejemplar : ejemplaresDisponibles) {
                ejemplar.setDisponible(false);  // Se marca como NO disponible
                ejemplarRepo.save(ejemplar);  // Se guarda en la BD
            }

            ejemplaresSeleccionados.addAll(ejemplaresDisponibles);
        }

        if (ejemplaresSeleccionados.isEmpty()) {
            return "redirect:/realizarPedido?error=noEjemplares";
        }

        nuevoPedido.setEjemplares(ejemplaresSeleccionados);
        
        // Guardamos el pedido en la BD
        servicioPedido.guardarPedido(nuevoPedido);

        return "redirect:/realizarPedido"; 
    }


    /**
     * Finaliza un pedido (antes llamado confirmarPedido)
     */
    @PostMapping("/finalizarPedido")
    public String finalizarPedido(@RequestParam Long idPedido) {
        servicioPedido.confirmarPedido(idPedido);  
        return "redirect:/carrito";
    }


    /**
     * Elimina un pedido del carrito
     */
    @PostMapping("/eliminarPedido")
    public String eliminarPedido(@RequestParam Long idPedido) {
        servicioPedido.eliminarPedido(idPedido); 
        return "redirect:/carrito"; 
    }

    

    
}
