package com.hfm350.tarea3dweshfm350.controller;

import java.rmi.server.LoaderHandler;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.hibernate.query.sqm.tree.SqmDeleteOrUpdateStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hfm350.tarea3dweshfm350.modelo.Controlador;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosEjemplar;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosMensaje;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPersona;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;

@Controller
public class EjemplarController {

	@Autowired
	private ServiciosEjemplar serviciosEjemplar;

	@Autowired
	private ServiciosMensaje serviciosMensaje;
	
	@Autowired
	private ServiciosPlanta serviciosPlanta;

	@GetMapping("/verMensajesEjemplar")
    public String mostrarFormulario(Model model) {
        List<Ejemplar> ejemplares = serviciosEjemplar.findAll();
        model.addAttribute("ejemplares", ejemplares);
        return "verMensajesEjemplar"; 
    }

    
    @PostMapping("/verMensajesEjemplar")
    public String procesarMensajes(@RequestParam Long codigoEjemplar, Model model) {
        List<Ejemplar> ejemplares = serviciosEjemplar.findAll();
        List<Mensaje> mensajes = serviciosMensaje.findAll();
        boolean ejemplarEncontrado = false;
        boolean mensajeEncontrado = false;
        List<Mensaje> mensajesEjemplar = new ArrayList<>();

      
        for (Ejemplar ejemplar : ejemplares) {
            if (ejemplar.getId().equals(codigoEjemplar)) {
                ejemplarEncontrado = true;

               
                for (Mensaje mensaje : mensajes) {
                    if (mensaje.getEjemplar().getId().equals(codigoEjemplar)) {
                        mensajesEjemplar.add(mensaje);
                        mensajeEncontrado = true;
                    }
                }
                break;
            }
        }

        model.addAttribute("ejemplares", ejemplares);
        model.addAttribute("codigoEjemplar", codigoEjemplar);

        if (!ejemplarEncontrado) {
            model.addAttribute("errorMensaje", "CODIGO no registrado en la base de datos");
        } else if (!mensajeEncontrado) {
            model.addAttribute("errorMensaje", "No existe ningun ejemplar para ese codigo");
        } else {
            model.addAttribute("mensajes", mensajesEjemplar);
        }

        return "verMensajesEjemplar"; 
    }
    
    
    
    @GetMapping("/ejemplarDePlanta")
    public String mostrarPlantas(Model model) {
        List<Planta> listaDePlantas = serviciosPlanta.findAll();
        model.addAttribute("plantas", listaDePlantas);
        return "ejemplarDePlanta";
    }

    @PostMapping("/ejemplarDePlanta")
    public String verEjemplares(@RequestParam("codigoPlanta") String codigoPlanta, Model model) {
        List<Planta> listaDePlantas = serviciosPlanta.findAll();
        List<Ejemplar> ejemplares = serviciosEjemplar.findAll();

        model.addAttribute("plantas", listaDePlantas);
        model.addAttribute("codigoBuscado", codigoPlanta);

        Planta plantaEncontrada = null;
        for (Planta p : listaDePlantas) {
            if (p.getCodigo().equalsIgnoreCase(codigoPlanta.trim())) {
                plantaEncontrada = p;
                break;
            }
        }

        if (plantaEncontrada != null) {
            List<Ejemplar> ejemplaresDePlanta = new ArrayList<>();
            for (Ejemplar ej : ejemplares) {
                if (ej.getPlanta().getId().equals(plantaEncontrada.getId())) {
                    ejemplaresDePlanta.add(ej);
                }
            }

            model.addAttribute("ejemplares", ejemplaresDePlanta);

            if (ejemplaresDePlanta.isEmpty()) {
                model.addAttribute("mensajeError", "No hay EJEMPLAR");
            }
        } else {
            model.addAttribute("mensajeError", "CODIGO de planta erroneo");
        }

        return "ejemplarDePlanta";
    }
    
    @PostMapping("/agregarEjemplar")
    public String agregarEjemplar(@RequestParam("codigoPlanta") String codigoPlanta, Model model) {
        Ejemplar ejemplar = serviciosEjemplar.insertar(codigoPlanta);

        if (ejemplar == null) {
            model.addAttribute("mensajeError", "No se pudo crear el ejemplar. Verifique el código de la planta.");
        } else {
            model.addAttribute("mensajeExito", "Ejemplar creado con éxito: " + ejemplar.getNombre());
        }

        return "redirect:/ejemplarDePlanta";
    }


}
