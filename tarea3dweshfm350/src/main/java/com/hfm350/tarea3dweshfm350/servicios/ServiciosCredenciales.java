package com.hfm350.tarea3dweshfm350.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.repositorios.CredencialRepository;
import com.hfm350.tarea3dweshfm350.repositorios.PersonaRepository;

@Service
public class ServiciosCredenciales {

    @Autowired
    private PersonaRepository personaRepo;

    @Autowired
    private CredencialRepository credencialRepo;

    public boolean autenticar(String nombreUsuario, String clave) {
        Optional<Credencial> credencialOpt = credencialRepo.findByUsuario(nombreUsuario);
        if (credencialOpt.isPresent()) {
            Credencial credenciales = credencialOpt.get();
            return credenciales.getPassword().equals(clave); 
        }
        return false; 
    }

    public boolean verificarUsuario(String usuario) {
        return credencialRepo.existeUsuario(usuario);
    }

    public void insertar(String usuario, String password, Long idPersona) {
        // Verifica si idPersona es nulo o no existe en la base de datos
        if (idPersona == null) {
            System.out.println("El ID de la persona no puede ser nulo.");
            return;
        }

        Persona persona = personaRepo.findById(idPersona).orElse(null);

        if (persona == null) {
            System.out.println("La persona con ID " + idPersona + " no existe.");
            return;
        }

        // Crear la entidad Credencial
        Credencial credenciales = new Credencial();
        credenciales.setUsuario(usuario);
        credenciales.setPassword(password);
        credenciales.setPersona(persona);

        try {
            // Guarda la credencial en la base de datos
            credencialRepo.save(credenciales);
            System.out.println("Credenciales registradas exitosamente para el usuario: " + usuario);
        } catch (Exception e) {
            System.err.println("Error al registrar las credenciales: " + e.getMessage());
        }
    }

    public boolean existeUsuario(String usuario) {
        return credencialRepo.existeUsuario(usuario);
    }

    public Long obtenerIdUsuario(String usuario) {
        return credencialRepo.obtenerIdUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuario));
    }

    public Persona buscarPersonaPorUsuario(String usuario) {
        return credencialRepo.findPersonaByUsuario(usuario);
    }

    public Optional<Credencial> buscarPersonaPorId(String nombreUsuario) {
        return credencialRepo.findByUsuario(nombreUsuario);
    }

    public Optional<Long> obtenerIdPersonaPorIdCredencial(Long idCredencial) {
        return credencialRepo.findPersonaIdByCredencialId(idCredencial);
    }

    public List<Credencial> findAll() {
        return credencialRepo.findAll();
    }
}
