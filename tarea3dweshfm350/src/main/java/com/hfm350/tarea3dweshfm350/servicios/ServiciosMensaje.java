package com.hfm350.tarea3dweshfm350.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.repositorios.EjemplarRepository;
import com.hfm350.tarea3dweshfm350.repositorios.MensajeRepository;
import com.hfm350.tarea3dweshfm350.repositorios.PlantaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ServiciosMensaje {

	@Autowired
	private EjemplarRepository ejemplarRepository;
	
	@Autowired
	private MensajeRepository mensajeRepo;

	 @Transactional
	    public void insertar(Mensaje mensaje) {
	        if (mensaje.getEjemplar() == null || mensaje.getPersona() == null) {
	            throw new IllegalArgumentException("El mensaje debe tener un ejemplar y una persona asociada.");
	        }

	        // Verificar si el ejemplar existe antes de guardar el mensaje
	        if (mensaje.getEjemplar().getId() == null) {
	            Ejemplar ejemplarGuardado = ejemplarRepository.save(mensaje.getEjemplar());
	            mensaje.setEjemplar(ejemplarGuardado);
	        }

	        mensajeRepo.save(mensaje);
	    }
	
	public List<Mensaje> findAll() {
		return mensajeRepo.findAll();
	}

	public List<Mensaje> buscarPorPlanta(Planta planta) {
		return mensajeRepo.buscarPorPlanta(planta);
	}

	public List<Mensaje> buscarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
		return mensajeRepo.buscarPorFechas(fechaInicio, fechaFin);
	}

	
	public List<Mensaje> findAllWithEjemplar() {
        return mensajeRepo.findAllWithEjemplar(); 
	}
	
	
	public Mensaje obtenerMensajeConEjemplar(Long id) {
	    return mensajeRepo.findByIdWithEjemplar(id)
	        .orElseThrow(() -> new EntityNotFoundException("Mensaje no encontrado"));
	}

	public List<Mensaje> buscarPorPersona(Persona persona) {
	    return mensajeRepo.buscarPorPersonaConEjemplar(persona);
	}


}
