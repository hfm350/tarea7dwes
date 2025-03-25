package com.hfm350.tarea3dweshfm350.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.repositorios.PlantaRepository;

import jakarta.transaction.Transactional;

@Service
public class ServiciosPlanta {

    @Autowired
    private PlantaRepository plantaRepo;

    public void insertar(Planta p) {
        if (p == null) {
            System.out.println("La planta no puede ser nula.");
            return;
        }
        plantaRepo.saveAndFlush(p);
    }

    public Planta buscarPorID(long id) {
        return plantaRepo.findById(id).orElse(null);
    }
    
    @Transactional
    public boolean actualizarNombreComun(String codigo, String nombreComun) {
        int updated = plantaRepo.actualizarNombreComun(codigo, nombreComun);
        return updated > 0;
    }

    @Transactional
    public boolean actualizarNombreCientifico(String codigo, String nombreCientifico) {
        int updated = plantaRepo.actualizarNombreCientifico(codigo, nombreCientifico);
        return updated > 0;
    }

    public boolean codigoExistente(String codigo) {
        return plantaRepo.existsByCodigo(codigo);
    }

    public boolean validarPlanta(Planta p) {
        if (p == null) {
            return false;
        }
        return validarCodigo(p.getCodigo()) &&
               validarTexto(p.getNombreCientifico(), 3, 100) &&
               validarTexto(p.getNombreComun(), 3, 100);
    }

    public boolean validarCodigo(String codigo) {
        return validarTexto(codigo, 3, 50) && codigo.matches("^[A-ZÁÉÍÓÚÑ]+$");
    }

    private boolean validarTexto(String texto, int min, int max) {
        if (texto == null || texto.isEmpty()) {
            return false;
        }
        if (texto.length() < min || texto.length() > max) {
            return false;
        }
        return texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    }

    public Planta buscarPorCodigo(String codigo) {
        return plantaRepo.findByCodigo(codigo).orElse(null);
    }

    public boolean esunCodigo(String codigo) {
        return validarCodigo(codigo);
    }

    public List<Planta> findAll() {
        return plantaRepo.findAll();
    }
    
    public List<Object[]> obtenerPlantasConEjemplares() {
        return plantaRepo.obtenerPlantasConEjemplares();
    }

}
