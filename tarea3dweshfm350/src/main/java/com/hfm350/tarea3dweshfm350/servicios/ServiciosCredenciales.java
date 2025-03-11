package com.hfm350.tarea3dweshfm350.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder; // **Importante para encriptar la contraseña**

    // Método para autenticar usuarios (compara contraseñas encriptadas)
    public boolean autenticar(String nombreUsuario, String clave) {
        Optional<Credencial> credencialOpt = credencialRepo.findByUsuario(nombreUsuario);
        return credencialOpt.isPresent() && passwordEncoder.matches(clave, credencialOpt.get().getPassword());
    }

    public boolean verificarUsuario(String usuario) {
        return credencialRepo.existeUsuario(usuario);
    }

    public void insertar(String usuario, String password, Long idPersona) {
        Persona persona = personaRepo.findById(idPersona).orElse(null);

        if (persona == null) {
            System.out.println("La persona con ID " + idPersona + " no existe.");
            return;
        }

        Credencial credenciales = new Credencial();
        credenciales.setUsuario(usuario);
        credenciales.setPassword(passwordEncoder.encode(password)); // Encriptar la contraseña
        credenciales.setPersona(persona);
        credenciales.setRol("ROLE_PERSONAL"); // Asignar rol por defecto

        try {
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
