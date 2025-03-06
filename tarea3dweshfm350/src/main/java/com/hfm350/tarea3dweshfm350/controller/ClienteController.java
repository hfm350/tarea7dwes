package com.hfm350.tarea3dweshfm350.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class ClienteController {

    @GetMapping("/menuCliente")
    public String mostrarMenuCliente(Model model, HttpSession session) {
        String perfil = (String) session.getAttribute("perfil");
        if (perfil == null || !perfil.equals("CLIENTE")) {
            return "redirect:/inicioSesion"; 
        }
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        return "menuCliente"; 
    }
}
