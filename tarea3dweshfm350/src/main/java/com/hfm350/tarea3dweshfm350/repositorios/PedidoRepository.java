package com.hfm350.tarea3dweshfm350.repositorios;

import com.hfm350.tarea3dweshfm350.modelo.Pedido;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	List<Pedido> findByClienteId(Long clienteId);

	@Modifying
	@Transactional
	@Query("UPDATE Pedido p SET p.confirmado = true WHERE p.id = :id")
	void confirmarPedido(@Param("id") Long id);

	@Modifying
	@Query("DELETE FROM Pedido p WHERE p.cliente.id = :clienteId AND p.confirmado = false")
	void eliminarPedidosNoConfirmados(@Param("clienteId") Long clienteId);

	@Query("SELECT c.id FROM Cliente c WHERE c.credenciales.usuario = :usuario")
	Long obtenerIdClientePorUsuario(@Param("usuario") String usuario);

	//@Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.confirmado = false")
	//List<Pedido> findByClienteIdAndConfirmadoFalse(@Param("clienteId") Long clienteId);
	
	List<Pedido> findByClienteIdAndConfirmadoFalse(Long clienteId);
	
	List<Pedido> findByClienteIdAndConfirmadoTrue(Long clienteId);

}