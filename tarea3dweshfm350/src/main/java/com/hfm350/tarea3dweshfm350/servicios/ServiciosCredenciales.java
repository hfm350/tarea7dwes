package com.hfm350.tarea3dweshfm350.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.repositorios.CredencialRepository;
import com.hfm350.tarea3dweshfm350.repositorios.PersonaRepository;

@Service
public class ServiciosCredenciales {

	@Autowired
	private PersonaRepository personaRepo;

	@Autowired
	private CredencialRepository crendecialRepo;

	public boolean autenticar(String nombreUsuario, String clave) {
	    Optional<Credencial> credencialOpt = crendecialRepo.findByUsuario(nombreUsuario);
	    if (credencialOpt.isPresent()) {
	        Credencial credenciales = credencialOpt.get();
	        return credenciales.getPassword().equals(clave); 
	    }
	    return false; 
	}

	public boolean verificarUsuario(String usuario) {
		return crendecialRepo.existeUsuario(usuario);
	}

	public void insertar(String usuario, String password, Long idPersona) {
		Persona persona = personaRepo.findById(idPersona).orElse(null);

		if (persona == null) {
			System.out.println("La persona con ID " + idPersona + " no existe.");
			return;
		}

		Credencial credenciales = new Credencial();
		credenciales.setUsuario(usuario);
		credenciales.setPassword(password);
		credenciales.setPersona(persona);

		try {
			crendecialRepo.save(credenciales);
			System.out.println("Credenciales registradas exitosamente para el usuario: " + usuario);
		} catch (Exception e) {
			System.err.println("Error al registrar las credenciales: " + e.getMessage());
		}
	}

	public boolean existeUsuario(String usuario) {
		return crendecialRepo.existeUsuario(usuario);
	}

	public Long obtenerIdUsuario(String usuario) {
		return crendecialRepo.obtenerIdUsuario(usuario)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuario));
	}
	
	public Persona buscarPersonaPorUsuario(String usuario) {
		return crendecialRepo.findPersonaByUsuario(usuario);
	}

	public Optional<Credencial> buscarPersonaPorId(String nombreUsuario) {
		return crendecialRepo.findByUsuario(nombreUsuario);
	}
	
	public Optional<Long> obtenerIdPersonaPorIdCredencial(Long idCredencial) {
        return crendecialRepo.findPersonaIdByCredencialId(idCredencial);
    }
	
	public List<Credencial> findAll() {
        return crendecialRepo.findAll();
    }

	

}
