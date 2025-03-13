package com.hfm350.tarea3dweshfm350.controller;

import com.hfm350.tarea3dweshfm350.modelo.Pedido;
import com.hfm350.tarea3dweshfm350.modelo.Cliente;
import com.hfm350.tarea3dweshfm350.servicios.ServicioPedido;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PedidoController {

    private final ServicioPedido servicioPedido;

    public PedidoController(ServicioPedido servicioPedido) {
        this.servicioPedido = servicioPedido;
    }

    /**
     * Muestra la vista del carrito con los pedidos en sesión.
     */
    @GetMapping("/carrito")
    public String verCarrito(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            return "redirect:/inicioSesion";
        }

        model.addAttribute("pedidos", servicioPedido.obtenerPedidosPorCliente(cliente.getId()));
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
