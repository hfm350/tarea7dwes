package com.hfm350.tarea3dweshfm350.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Planta;

@Repository
public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {
	@Query("SELECT COUNT(e) > 0 FROM Ejemplar e WHERE e.planta = :planta")
	boolean existePlanta(@Param("planta") Planta planta);

	@Query("SELECT e FROM Ejemplar e LEFT JOIN FETCH e.pedido WHERE e.id = :id")
	Optional<Ejemplar> findById(@Param("id") Long id);

	@Query("SELECT e FROM Ejemplar e WHERE e.disponible = true")
	List<Ejemplar> findEjemplaresDisponibles();

	@Query("SELECT e FROM Ejemplar e WHERE e.planta.codigo = :codigoPlanta AND e.disponible = true")
	List<Ejemplar> findEjemplaresDisponiblesPorPlanta(@Param("codigoPlanta") String codigoPlanta);

	List<Ejemplar> findByDisponibleTrue();

	@Query("SELECT e FROM Ejemplar e WHERE e.planta.id = :plantaId AND e.disponible = true ORDER BY e.id ASC LIMIT :cantidad")
	List<Ejemplar> findTopNByPlantaIdAndDisponibleTrue(@Param("plantaId") Long plantaId,
			@Param("cantidad") int cantidad);

	@Query("SELECT e FROM Ejemplar e WHERE e.planta.id = :plantaId AND e.disponible = true ORDER BY e.id ASC")
	List<Ejemplar> findEjemplaresDisponiblesPorPlanta(@Param("plantaId") Long plantaId);

	@Modifying
	@Transactional
	@Query("UPDATE Ejemplar e SET e.disponible = true, e.pedido = NULL WHERE e.pedido.id = :pedidoId")
	void restaurarEjemplaresDePedido(@Param("pedidoId") Long pedidoId);

	@Modifying
	@Transactional
	@Query("UPDATE Ejemplar e SET e.disponible = false WHERE e.id IN :ids")
	void marcarEjemplaresNoDisponibles(@Param("ids") List<Long> ids);

	void deleteByPedidoId(Long pedidoId);

	List<Ejemplar> findByPedidoId(Long pedidoId);
	
	List<Ejemplar> findByPlanta(Planta planta);


}
