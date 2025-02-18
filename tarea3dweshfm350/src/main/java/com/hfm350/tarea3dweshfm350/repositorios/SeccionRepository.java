package com.hfm350.tarea3dweshfm350.repositorios;

import com.hfm350.tarea3dweshfm350.modelo.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Long> {
   
}
