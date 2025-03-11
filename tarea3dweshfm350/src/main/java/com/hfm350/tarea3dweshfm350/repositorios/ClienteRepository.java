package com.hfm350.tarea3dweshfm350.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hfm350.tarea3dweshfm350.modelo.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{
	boolean existsByNifNie(String nifNie);
	boolean existsByEmail(String email);
	
}
