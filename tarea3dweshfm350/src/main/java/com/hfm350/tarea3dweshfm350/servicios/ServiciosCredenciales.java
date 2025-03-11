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
    private PasswordEncoder passwordEncoder;
    
    public Optional<Credencial> buscarPorUsuario(String usuario) {
        return credencialRepo.findByUsuario(usuario);
    }

    public boolean autenticar(String nombreUsuario, String clave) {
        Optional<Credencial> credencialOpt = credencialRepo.findByUsuario(nombreUsuario);

        if (credencialOpt.isPresent()) {
            Credencial credencial = credencialOpt.get();
            
            System.out.println("🔑 Contraseña ingresada: " + clave);
            System.out.println("🔐 Contraseña en BD: " + credencial.getPassword());

            //Compara la contraseña ingresada (sin encriptar) con la almacenada (encriptada)
            boolean coincide = passwordEncoder.matches(clave, credencial.getPassword());

            System.out.println("¿Contraseñas coinciden?: " + coincide);

            return coincide;
        }

        System.out.println("Usuario no encontrado");
        return false;
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

        String encryptedPassword = passwordEncoder.encode(password); // Encripta la contraseña
        System.out.println("Contraseña encriptada: " + encryptedPassword); // Verificar en consola

        Credencial credenciales = new Credencial();
        credenciales.setUsuario(usuario);
        credenciales.setPassword(encryptedPassword); 
        credenciales.setPersona(persona);
        credenciales.setRol("ROLE_PERSONAL"); // Asignar el rol automáticamente

        try {
            credencialRepo.save(credenciales);
            System.out.println("Credenciales registradas exitosamente para el usuario: " + usuario);
        } catch (Exception e) {
            System.err.println("Error al registrar las credenciales: " + e.getMessage());
        }
    }
    
    public void insertarCliente(String username, String password, String rol) {
        // Crear una nueva instancia de Credencial
        Credencial credencial = new Credencial();
        credencial.setUsuario(username); // Asignar el username
        credencial.setPassword(password); // Asignar el password
        credencial.setRol(rol);           // Asignar el rol

        // Guardar la credencial en la base de datos
        credencialRepo.save(credencial);
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
