package com.hfm350.tarea3dweshfm350.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.repositorios.PersonaRepository;

@Service
public class ServiciosPersona {

	@Autowired
	private PersonaRepository personaRepo;

	public void insertar(Persona p) {
		personaRepo.saveAndFlush(p);
	}

	public boolean existeEmail(String email) {
		return personaRepo.existsByEmail(email);
	}

	public List<Persona> obtenerTodasLasPersonas() {
		return personaRepo.findAll();
	}
	
	public long IdUsuarioAutenticado(String usuario) {
		Long idPersona = personaRepo.idUsuarioAutenticado(usuario);
        return (idPersona != null) ? idPersona : -1;
	}

	public Optional<Persona> buscarPorId(Long usuarioId) {
		return personaRepo.findById(usuarioId);
	}
	
	

	
}
