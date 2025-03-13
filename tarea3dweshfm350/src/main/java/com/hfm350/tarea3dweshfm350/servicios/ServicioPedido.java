package com.hfm350.tarea3dweshfm350.servicios;

import com.hfm350.tarea3dweshfm350.modelo.Pedido;
import com.hfm350.tarea3dweshfm350.modelo.Cliente;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.repositorios.PedidoRepository;
import com.hfm350.tarea3dweshfm350.repositorios.EjemplarRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioPedido {
    private final PedidoRepository pedidoRepository;
    private final EjemplarRepository ejemplarRepository;

    public ServicioPedido(PedidoRepository pedidoRepository, EjemplarRepository ejemplarRepository) {
        this.pedidoRepository = pedidoRepository;
        this.ejemplarRepository = ejemplarRepository;
    }

    /**
     * Obtiene todos los pedidos del cliente.
     */
    public List<Pedido> obtenerPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    /**
     * Crea un nuevo pedido y asigna ejemplares disponibles.
     */
    public Pedido realizarPedido(Cliente cliente, int cantidad) {
        List<Ejemplar> ejemplaresDisponibles = ejemplarRepository.findEjemplaresDisponibles();

        if (ejemplaresDisponibles.size() < cantidad) {
            throw new RuntimeException("No hay suficientes ejemplares disponibles.");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEjemplares(ejemplaresDisponibles.subList(0, cantidad));

        // Marcar los ejemplares como no disponibles
        for (Ejemplar ejemplar : pedido.getEjemplares()) {
            ejemplar.setDisponible(false);
            ejemplarRepository.save(ejemplar);
        }

        return pedidoRepository.save(pedido);
    }

    /**
     * Confirma un pedido.
     */
    public void confirmarPedido(Long idPedido) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        if (pedidoOpt.isPresent()) {
            pedidoRepository.save(pedidoOpt.get());
        }
    }

    /**
     * Elimina un pedido antes de ser confirmado.
     */
    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }
}
