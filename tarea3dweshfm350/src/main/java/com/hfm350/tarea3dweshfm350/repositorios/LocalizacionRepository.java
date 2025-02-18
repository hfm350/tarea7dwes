package com.hfm350.tarea3dweshfm350.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hfm350.tarea3dweshfm350.modelo.Localizacion;
import com.hfm350.tarea3dweshfm350.modelo.Seccion;

public interface LocalizacionRepository extends JpaRepository<Localizacion, Long> {
	
	List<Localizacion> findBySeccion(Seccion seccion);
	
	@Query("SELECT l FROM Localizacion l WHERE l.seccion = :seccion AND l.numSec = :numSec")
	Optional<Localizacion> findBySeccionyNumSec(@Param("seccion") Seccion seccion, @Param("numSec") int numSec);


}
