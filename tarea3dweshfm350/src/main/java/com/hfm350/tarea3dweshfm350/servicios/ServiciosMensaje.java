package com.hfm350.tarea3dweshfm350.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.repositorios.EjemplarRepository;
import com.hfm350.tarea3dweshfm350.repositorios.MensajeRepository;
import com.hfm350.tarea3dweshfm350.repositorios.PlantaRepository;

@Service
public class ServiciosMensaje {

	@Autowired
	private EjemplarRepository ejemplarRepository;
	@Autowired
	private MensajeRepository mensajeRepo;

	public void insertar(Mensaje mensaje) {

		if (mensaje.getEjemplar().getId() == null) {
			ejemplarRepository.save(mensaje.getEjemplar());
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

	public List<Mensaje> buscarPorPersona(Persona persona) {
		return mensajeRepo.findByPersona(persona);
	}
}
