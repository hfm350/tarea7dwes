package com.hfm350.tarea3dweshfm350.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.modelo.Persona;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, Long> {
	
	
	Optional<Credencial> findByUsuario(String nombreUsuario);

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Credencial c WHERE c.usuario = :usuario")
	boolean existeUsuario(String usuario);

	@Query("SELECT c.id FROM Credencial c WHERE c.usuario = :usuario")
	Optional<Long> obtenerIdUsuario(String usuario);

	@Query("SELECT c.persona FROM Credencial c WHERE c.usuario = :usuario")
	Persona findPersonaByUsuario(@Param("usuario") String usuario);
	
	@Query("SELECT c.persona.id FROM Credencial c WHERE c.id = :idCredencial")
	Optional<Long> findPersonaIdByCredencialId(@Param("idCredencial") Long idCredencial);

	boolean existsByUsuarioAndPersonaId(String usuario, Long personaId);



}
