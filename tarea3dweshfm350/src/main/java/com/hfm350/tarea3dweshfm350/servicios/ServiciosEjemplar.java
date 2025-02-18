package com.hfm350.tarea3dweshfm350.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.repositorios.EjemplarRepository;
import com.hfm350.tarea3dweshfm350.repositorios.PlantaRepository;


@Service
public class ServiciosEjemplar {

	@Autowired
    private EjemplarRepository ejemplarRepo;
	
	@Autowired
	private PlantaRepository plantaRepo;
	
	public void insertar(Ejemplar ej) {
        ejemplarRepo.saveAndFlush(ej);
    }
	
	public Ejemplar insertar(String nombre, String codigo) {
	    if (nombre == null || nombre.trim().isEmpty()) {
	        System.out.println("El nombre del ejemplar no puede estar vacío.");
	        return null;
	    }

	    Optional<Planta> p = plantaRepo.findByCodigo(codigo);

	    if (p.isEmpty()) {
	        System.out.println("La planta con código " + codigo + " no existe.");
	        return null;
	    }

	    Planta planta = p.get();

	    Ejemplar ejemplar = new Ejemplar();
	    ejemplar.setNombre(nombre.trim());
	    ejemplar.setPlanta(planta); 

	    try {
	        ejemplarRepo.save(ejemplar);
	        System.out.println("El ejemplar ha sido registrado exitosamente para la planta " + planta.getNombreComun());
	    } catch (Exception e) {
	        System.err.println("Error al registrar el ejemplar: " + e.getMessage());
	    }
		return ejemplar;
	}

	public List<Ejemplar> findAll() {
		return ejemplarRepo.findAll();
	}

	public boolean existsByPlanta(Planta planta) {
		return ejemplarRepo.existePlanta(planta);
	}

	public Optional<Ejemplar> buscarPorId(Long idEjemplar) {
		return ejemplarRepo.findById(idEjemplar);
	}
	
	

	

}
