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
    @Transactional
    public void confirmarPedido(Long idPedido) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);

        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            Cliente cliente = pedido.getCliente();
            LocalDateTime fechaHoraActual = LocalDateTime.now();

            for (Ejemplar ejemplar : pedido.getEjemplares()) {
                // Crear el mensaje
                String mensajeTexto = cliente.getNombre() + " compró el ejemplar " + 
                                      ejemplar.getId() + " el día " + 
                                      fechaHoraActual.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + 
                                      " en el pedido " + pedido.getId() + ".";

                // Guardar la anotación
                Mensaje mensaje = new Mensaje(fechaHoraActual, mensajeTexto, cliente, pedido, ejemplar);
                mensajeRepository.save(mensaje);
            }

            // Marcar el pedido como confirmado
            pedido.setConfirmado(true);
            pedidoRepository.save(pedido);
            System.out.println("Pedido confirmado y mensajes registrados: " + pedido.getId());
        } else {
            System.err.println("Error: Pedido no encontrado con ID " + idPedido);
        }
    }


    @Transactional
    public void eliminarPedido(Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);

        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();

            System.out.println("Eliminando pedido ID: " + pedido.getId() + " (Confirmado: " + pedido.isConfirmado() + ")");

            for (Ejemplar ejemplar : pedido.getEjemplares()) {
                mensajeRepository.deleteByEjemplarId(ejemplar.getId());
            }
            
            // Restaurar ejemplares antes de eliminar el pedido
            List<Ejemplar> ejemplares = pedido.getEjemplares();
            for (Ejemplar ejemplar : ejemplares) {
                ejemplar.setDisponible(true);
                ejemplar.setPedido(null);
                ejemplarRepository.save(ejemplar);
            }
            
            // Eliminar el pedido
            pedidoRepository.delete(pedido);
            System.out.println("Pedido eliminado correctamente.");
        } else {
            System.err.println("Error: No se encontró el pedido con ID " + id);
        }
    }
    
    public void guardarPedido(Pedido pedido) {
        pedidoRepository.save(pedido);
    }
    

    @Transactional
    public void eliminarPedidosNoConfirmados(Long clienteId) {
        List<Pedido> pedidosNoConfirmados = pedidoRepository.findByClienteIdAndConfirmadoFalse(clienteId);
        

        if (!pedidosNoConfirmados.isEmpty()) {
            pedidosNoConfirmados.forEach(pedido -> {
                // Restaurar los ejemplares antes de eliminar el pedido
                pedido.getEjemplares().forEach(ejemplar -> {
                    ejemplar.setDisponible(true);
                    ejemplar.setPedido(null);
                });
                ejemplarRepository.saveAll(pedido.getEjemplares());
            });

            ejemplarRepository.flush(); // Asegura que los cambios se reflejan en la BD antes de eliminar pedidos

            // Eliminar los pedidos después de restaurar los ejemplares
            pedidoRepository.deleteAll(pedidosNoConfirmados);
            pedidoRepository.flush();

            System.out.println("Pedidos no confirmados eliminados exitosamente.");
        } else {
            System.out.println("No había pedidos no confirmados para eliminar.");
        }
    }


   
    
    public Long obtenerIdClientePorUsuario(String usuario) {
        Long id = pedidoRepository.obtenerIdClientePorUsuario(usuario);
        System.out.println("📌 ID del cliente obtenido: " + id);
        return id;
    }
    
    public List<Pedido> obtenerPedidosNoConfirmadosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdAndConfirmadoFalse(clienteId);
    }
    
    public List<Pedido> obtenerPedidosConfirmadosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdAndConfirmadoTrue(clienteId);
    }	

    
    public Pedido guardarPedido2(Pedido pedido) {
        return pedidoRepository.save(pedido);  // ✅ Ahora devuelve el pedido guardado
    }




}
