package com.hfm350.tarea3dweshfm350.servicios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	public ServiciosEjemplar(EjemplarRepository ejemplarRepositorio) {
        this.ejemplarRepo = ejemplarRepositorio;
    }
	
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

	public Ejemplar insertar2(Ejemplar ej) {
	    return ejemplarRepo.saveAndFlush(ej);  // Devuelve el ejemplar guardado
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
	
	/**
     * Obtiene la lista de ejemplares disponibles (suponiendo que el atributo `disponible` es un boolean en la entidad `Ejemplar`)
     */
    public List<Ejemplar> obtenerEjemplaresDisponibles() {
        return ejemplarRepo.findByDisponibleTrue();  // Método que busca ejemplares disponibles
    }
    
    public List<Ejemplar> obtenerEjemplaresPorIds(List<Long> ejemplaresIds) {
        return ejemplarRepo.findAllById(ejemplaresIds);
    }
    
    public List<Ejemplar> obtenerEjemplaresDisponiblesPorPlanta(Long plantaId, Integer cantidad) {
        // Consultar la base de datos para obtener los ejemplares disponibles de la planta
        return ejemplarRepo.findTopNByPlantaIdAndDisponibleTrue(plantaId, cantidad);
    }
    
    public List<Map<String, Object>> obtenerPlantasConEjemplares() {
        List<Object[]> resultados = plantaRepo.obtenerPlantasConEjemplares();
        List<Map<String, Object>> plantas = new ArrayList<>();

        for (Object[] fila : resultados) {
            Map<String, Object> planta = new HashMap<>();
            planta.put("id", fila[0]);
            planta.put("nombreComun", fila[1]); // Aquí se usa nombreComun
            planta.put("cantidadEjemplares", fila[2]);
            plantas.add(planta);
        }

        return plantas;
    }

    

    public List<Ejemplar> obtenerEjemplaresDisponiblesPorPlanta(Long plantaId, int cantidad) {
        List<Ejemplar> ejemplaresDisponibles = ejemplarRepo.findEjemplaresDisponiblesPorPlanta(plantaId);

        if (ejemplaresDisponibles.size() < cantidad) {
            throw new RuntimeException("No hay suficientes ejemplares disponibles.");
        }

        return ejemplaresDisponibles.subList(0, cantidad);
    }



	
    
	

	

}
