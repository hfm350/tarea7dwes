package com.hfm350.tarea3dweshfm350.servicios;

import com.hfm350.tarea3dweshfm350.modelo.Localizacion;
import com.hfm350.tarea3dweshfm350.modelo.Seccion;
import com.hfm350.tarea3dweshfm350.repositorios.LocalizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiciosLocalizacion {

    

    @Autowired
    private LocalizacionRepository localizacionRepo;

    public List<Localizacion> obtenerTodasLasLocalizaciones() {
        return localizacionRepo.findAll();
    }

    public Optional<Localizacion> obtenerLocalizacionPorId(Long id) {
        return localizacionRepo.findById(id);
    }

    public void insertar(Localizacion l) {
        localizacionRepo.save(l);
    }
    
    public int numeroDeSeccion(Seccion seccion) {
        List<Localizacion> localizaciones = localizacionRepo.findBySeccion(seccion);
        return localizaciones.size();
    }
    
    public Optional<Localizacion> buscarPorNumeroySeccion(Seccion seccion, int numLocalizacion) {
        return localizacionRepo.findBySeccionyNumSec(seccion, numLocalizacion);
    }

}

