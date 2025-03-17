package com.hfm350.tarea3dweshfm350.repositorios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long>{

	@Query("SELECT m FROM Mensaje m JOIN m.ejemplar e WHERE e.planta = :planta")
	List<Mensaje> buscarPorPlanta(@Param("planta") Planta planta);

	@Query("SELECT m FROM Mensaje m WHERE m.tiempo BETWEEN :fechaInicio AND :fechaFin")
    List<Mensaje> buscarPorFechas(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                  @Param("fechaFin") LocalDateTime fechaFin);

	List<Mensaje> findByPersona(Persona persona);

	@Query("SELECT m FROM Mensaje m WHERE m.cliente.id = :clienteId")
    List<Mensaje> buscarMensajesPorCliente(@Param("clienteId") Long clienteId);

    @Query("SELECT m FROM Mensaje m WHERE m.pedido.id = :pedidoId")
    List<Mensaje> buscarMensajesPorPedido(@Param("pedidoId") Long pedidoId);
   
    @Modifying
    @Transactional
    @Query("DELETE FROM Mensaje m WHERE m.ejemplar.id = :ejemplarId")
    void deleteByEjemplarId(@Param("ejemplarId") Long ejemplarId);
	

}
