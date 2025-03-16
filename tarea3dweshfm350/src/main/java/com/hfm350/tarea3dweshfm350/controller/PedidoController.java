package com.hfm350.tarea3dweshfm350.controller;

import com.hfm350.tarea3dweshfm350.modelo.Pedido;
import com.hfm350.tarea3dweshfm350.modelo.Cliente;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.servicios.ServicioPedido;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class PedidoController {

    private final ServicioPedido servicioPedido;
    private final ServiciosCredenciales serviciosCredenciales;

    public PedidoController(ServicioPedido servicioPedido, ServiciosCredenciales serviciosCredenciales) {
        this.servicioPedido = servicioPedido;
        this.serviciosCredenciales = serviciosCredenciales;
    }

    @GetMapping("/carrito")
    public String verCarrito(HttpSession session, Model model) {
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

        // Obtener los pedidos en la sesión
        List<Pedido> pedidos = (List<Pedido>) session.getAttribute("pedidos");

        if (pedidos == null) {
            pedidos = servicioPedido.obtenerPedidosPorCliente(clienteId);
            session.setAttribute("pedidos", pedidos);
        }

        model.addAttribute("pedidos", pedidos);
        return "carrito";
    }

    /**
     * Permite al cliente realizar un pedido de ejemplares disponibles.
     */
    @PostMapping("/realizarPedido")
    public String realizarPedido(HttpSession session, @RequestParam int cantidad) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            return "redirect:/inicioSesion";
        }

        servicioPedido.realizarPedido(cliente, cantidad);
        return "redirect:/carrito";
    }

    /**
     * Confirma un pedido en el carrito del cliente.
     */
    @PostMapping("/confirmarPedido")
    public String confirmarPedido(@RequestParam Long idPedido) {
        servicioPedido.confirmarPedido(idPedido);
        return "redirect:/menuCliente";
    }

    /**
     * Elimina un pedido del carrito antes de ser confirmado.
     */
    @PostMapping("/eliminarPedido")
    public String eliminarPedido(@RequestParam Long idPedido) {
        servicioPedido.eliminarPedido(idPedido);
        return "redirect:/carrito";
    }
}
