package com.hfm350.tarea3dweshfm350.servicios;

import com.hfm350.tarea3dweshfm350.modelo.Seccion;
import com.hfm350.tarea3dweshfm350.repositorios.SeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiciosSeccion {

	@Autowired
	private SeccionRepository seccionRepo;

	public List<Seccion> obtenerTodasLasSecciones() {
		return seccionRepo.findAll();
	}

	public Optional<Seccion> obtenerSeccionPorId(Long id) {
		return seccionRepo.findById(id);
	}

	public void insertar(Seccion s) {
		seccionRepo.saveAndFlush(s);
	}

}
