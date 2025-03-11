package com.hfm350.tarea3dweshfm350.repositorios;

import com.hfm350.tarea3dweshfm350.modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
}