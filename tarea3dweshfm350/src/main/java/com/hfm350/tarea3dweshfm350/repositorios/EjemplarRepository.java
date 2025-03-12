package com.hfm350.tarea3dweshfm350.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Planta;



@Repository
public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {
    @Query("SELECT COUNT(e) > 0 FROM Ejemplar e WHERE e.planta = :planta")
    boolean existePlanta(@Param("planta") Planta planta);

    Optional<Ejemplar> findById(Long id);
}
