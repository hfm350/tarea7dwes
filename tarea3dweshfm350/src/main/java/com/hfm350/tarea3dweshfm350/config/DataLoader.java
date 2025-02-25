package com.hfm350.tarea3dweshfm350.config;

import com.hfm350.tarea3dweshfm350.modelo.*;
import com.hfm350.tarea3dweshfm350.repositorios.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataLoader implements CommandLineRunner {

    private final CredencialRepository credencialesRepository;
    private final EjemplarRepository ejemplaresRepository;
    private final MensajeRepository mensajesRepository;
    private final PersonaRepository personasRepository;
    private final PlantaRepository plantasRepository;

    public DataLoader(CredencialRepository credencialesRepository,
                      EjemplarRepository ejemplaresRepository,
                      MensajeRepository mensajesRepository,
                      PersonaRepository personasRepository,
                      PlantaRepository plantasRepository) {
        this.credencialesRepository = credencialesRepository;
        this.ejemplaresRepository = ejemplaresRepository;
        this.mensajesRepository = mensajesRepository;
        this.personasRepository = personasRepository;
        this.plantasRepository = plantasRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // Borrar datos en el orden correcto
        mensajesRepository.deleteAll();
        credencialesRepository.deleteAll();
        ejemplaresRepository.deleteAll();
        plantasRepository.deleteAll();
        personasRepository.deleteAll();

        System.out.println("Base de datos vaciada.");

        // Insertar datos en 'personas'
        Persona p1 = new Persona("Juan Pérez", "juan@juan.com");
        Persona p2 = new Persona("María López", "maria@maria.lopez");
        Persona p3 = new Persona("Carlos González", "carlos@carlos.gonzalez");
        personasRepository.saveAll(List.of(p1, p2, p3));

        // Insertar datos en 'credenciales'
        credencialesRepository.saveAll(List.of(
            new Credencial("juanp", "juanp", p1),
            new Credencial("marial", "marial", p2),
            new Credencial("carlosg", "carlosg", p3)
        ));

        // Insertar datos en 'plantas'
        Planta planta1 = new Planta("P001", "Rosa rubiginosa", "Rosa");
        Planta planta2 = new Planta("P002", "Helianthus annuus", "Girasol");
        Planta planta3 = new Planta("P003", "Echinopsis chamaecereus", "Cactus de maní");
        plantasRepository.saveAll(List.of(planta1, planta2, planta3));

        // Insertar datos en 'ejemplares' individualmente para obtener ID
        Ejemplar e1 = ejemplaresRepository.save(new Ejemplar(null, "Rosa Roja", planta1, new ArrayList<>(), null));
        Ejemplar e2 = ejemplaresRepository.save(new Ejemplar(null, "Girasol Grande", planta2, new ArrayList<>(), null));
        Ejemplar e3 = ejemplaresRepository.save(new Ejemplar(null, "Cactus Pequeño", planta3, new ArrayList<>(), null));

        // Ahora que los ejemplares tienen ID, se pueden usar en los mensajes
        mensajesRepository.saveAll(List.of(
            new Mensaje(LocalDateTime.now(), "Mensaje para la Rosa.", p1, e1),
            new Mensaje(LocalDateTime.now(), "Mensaje para el Girasol.", p2, e2),
            new Mensaje(LocalDateTime.now(), "Mensaje para el Cactus.", p3, e3)
        ));

        System.out.println("Datos cargados en la base de datos");
    }


}
