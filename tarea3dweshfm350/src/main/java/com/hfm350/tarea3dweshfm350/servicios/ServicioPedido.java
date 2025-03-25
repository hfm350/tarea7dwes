package com.hfm350.tarea3dweshfm350.servicios;

import com.hfm350.tarea3dweshfm350.modelo.Pedido;
import com.hfm350.tarea3dweshfm350.modelo.Cliente;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.repositorios.PedidoRepository;

import jakarta.transaction.Transactional;

import com.hfm350.tarea3dweshfm350.repositorios.ClienteRepository;
import com.hfm350.tarea3dweshfm350.repositorios.EjemplarRepository;
import com.hfm350.tarea3dweshfm350.repositorios.MensajeRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioPedido {
	private final PedidoRepository pedidoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final MensajeRepository mensajeRepository;
    private final ClienteRepository clienteRepository;

    public ServicioPedido(PedidoRepository pedidoRepository, EjemplarRepository ejemplarRepository, 
                          MensajeRepository mensajeRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.mensajeRepository = mensajeRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Obtiene todos los pedidos en la sesión.
     */
    public List<Pedido> obtenerPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    /**
     * Crea un nuevo pedido y asigna ejemplares disponibles.
     */
    @Transactional
    public Pedido realizarPedido(Cliente cliente, String codigoPlanta, int cantidad) {
        List<Ejemplar> ejemplaresDisponibles = ejemplarRepository.findEjemplaresDisponiblesPorPlanta(codigoPlanta);

        if (ejemplaresDisponibles.size() < cantidad) {
            throw new RuntimeException("No hay suficientes ejemplares disponibles.");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEjemplares(ejemplaresDisponibles.subList(0, cantidad));

        // 🔴 IMPORTANTE: Marcar los ejemplares como NO disponibles y guardarlos en la BD
        for (Ejemplar ejemplar : pedido.getEjemplares()) {
        	ejemplar.setDisponible(false); //Se marca como NO disponible
            ejemplar.setPedido(pedido);     // Se asocia al pedido
            ejemplarRepository.save(ejemplar); // Se guarda en la BD
        }

        return pedidoRepository.save(pedido);  // Se guarda el pedido en la BD
    }




    /**
     * Confirma un pedido en la BD. /carrito
     */
    


    
    public void guardarPedido(Pedido pedido) {
        pedidoRepository.save(pedido);
    }
    

    


   
    
    public Long obtenerIdClientePorUsuario(String usuario) {
        Long id = pedidoRepository.obtenerIdClientePorUsuario(usuario);
        System.out.println("📌 ID del cliente obtenido: " + id);
        return id;
    }
    
   
    
    public Pedido guardarPedido2(Pedido pedido) {
        return pedidoRepository.save(pedido);  // ✅ Ahora devuelve el pedido guardado
    }

    @Transactional
    public void eliminarPedido(Long idPedido) {
        // 1. Buscar todos los ejemplares relacionados con ese pedido
        List<Ejemplar> ejemplares = ejemplarRepository.findByPedidoId(idPedido);

        // 2. Desasociar los ejemplares del pedido (sin borrarlos, opcional)
        for (Ejemplar e : ejemplares) {
            e.setPedido(null);
            e.setDisponible(true); // opcional, si quieres que se puedan volver a pedir
            ejemplarRepository.save(e);
        }

        // 3. Ahora que no tienen restricción, se puede eliminar el pedido
        pedidoRepository.deleteById(idPedido);
    }







}
