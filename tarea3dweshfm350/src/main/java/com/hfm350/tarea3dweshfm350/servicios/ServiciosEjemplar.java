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
	
	public Ejemplar insertar(String codigoPlanta) {
	    Optional<Planta> optionalPlanta = plantaRepo.findByCodigo(codigoPlanta);

	    if (optionalPlanta.isEmpty()) {
	        System.out.println("La planta con código " + codigoPlanta + " no existe.");
	        return null;
	    }

	    Planta planta = optionalPlanta.get();
	    Ejemplar ejemplar = new Ejemplar(planta);
	    ejemplar.setDisponible(true); // Asegurar que se asigna un valor antes de guardar

	    try {
	        ejemplar = ejemplarRepo.save(ejemplar);
	        ejemplar.setId(ejemplar.getId()); // Se actualiza el nombre
	        ejemplar = ejemplarRepo.save(ejemplar); // Guarda con nombre asignado
	        System.out.println("Ejemplar creado: " + ejemplar.getNombre());
	    } catch (Exception e) {
	        System.err.println("Error al registrar el ejemplar: " + e.getMessage());
	        return null;
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
