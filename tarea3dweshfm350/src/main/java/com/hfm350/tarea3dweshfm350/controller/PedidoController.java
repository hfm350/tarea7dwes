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

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/carrito")
    public String verCarrito(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/inicioSesion";
        }

        String usuarioAutenticado = authentication.getName();
        Long clienteId = serviciosCredenciales.obtenerIdClientePorUsuario(usuarioAutenticado);

        if (clienteId == null) {
            return "redirect:/menuCliente";
        }

        List<Pedido> pedidosNoConfirmados = servicioPedido.obtenerPedidosNoConfirmadosPorCliente(clienteId);
        model.addAttribute("pedidos", pedidosNoConfirmados);

        return "carrito";
    }

    @GetMapping("/realizarPedido")
    public String mostrarPlantasDisponibles(Model model) {
        List<Map<String, Object>> plantas = servicioEjemplar.obtenerPlantasConEjemplares();
        model.addAttribute("plantas", plantas);
        return "realizarPedido";
    }

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

     // 🔹 GUARDAR PRIMERO EL PEDIDO
        nuevoPedido = servicioPedido.guardarPedido2(nuevoPedido);  // ✅ Ahora el método devuelve el Pedido


        List<Ejemplar> ejemplaresSeleccionados = new ArrayList<>();

        for (int i = 0; i < plantasIds.size(); i++) {
            Long plantaId = plantasIds.get(i);
            Integer cantidad = cantidades.get(i);

            List<Ejemplar> ejemplaresDisponibles = servicioEjemplar.obtenerEjemplaresDisponiblesPorPlanta(plantaId, cantidad);

            for (Ejemplar ejemplar : ejemplaresDisponibles) {
                ejemplar.setDisponible(false);
                ejemplar.setPedido(nuevoPedido);  // ✅ Ahora el pedido ya está en la base de datos
                ejemplarRepo.save(ejemplar);  // ✅ Guardamos el ejemplar con el pedido asignado
            }

            ejemplaresSeleccionados.addAll(ejemplaresDisponibles);
        }

        if (ejemplaresSeleccionados.isEmpty()) {
            return "redirect:/realizarPedido?error=noEjemplares";
        }

        return "redirect:/realizarPedido";
    }


    @PostMapping("/finalizarPedido")
    public String finalizarPedido(@RequestParam Long idPedido) {
        servicioPedido.confirmarPedido(idPedido);
        return "redirect:/carrito";
    }

    @PostMapping("/eliminarPedido")
    public String eliminarPedido(@RequestParam Long idPedido) {
        servicioPedido.eliminarPedido(idPedido);
        return "redirect:/carrito";
    }

    @GetMapping("/misPedidos")
    public String verMisPedidos(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/inicioSesion";
        }

        String usuarioAutenticado = authentication.getName();
        Long clienteId = serviciosCredenciales.obtenerIdClientePorUsuario(usuarioAutenticado);
        if (clienteId == null) {
            return "redirect:/menuCliente";
        }
        
        List<Pedido> pedidosConfirmados = servicioPedido.obtenerPedidosConfirmadosPorCliente(clienteId);
        model.addAttribute("pedidos", pedidosConfirmados);
        return "misPedidos";
    }

    @PostMapping("/eliminarPedido2")
    public String eliminarPedido(@RequestParam Long idPedido, HttpServletRequest request) {
        servicioPedido.eliminarPedido(idPedido);
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("misPedidos")) {
            return "redirect:/misPedidos";
        }
        return "redirect:/carrito";
    }
}