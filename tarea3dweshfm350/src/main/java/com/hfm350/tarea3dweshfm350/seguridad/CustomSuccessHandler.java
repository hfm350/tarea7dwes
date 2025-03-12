package com.hfm350.tarea3dweshfm350.seguridad;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String role = authentication.getAuthorities().iterator().next().getAuthority();

		// 🔹 Debugging: Verificar autenticación
		System.out.println("✅ Usuario autenticado con éxito: " + authentication.getName());
		System.out.println("✅ Rol asignado: " + role);

		// Guarda el rol en la sesión
		request.getSession().setAttribute("rol", role);

		// Redirige a la página correspondiente
		if ("ROLE_ADMIN".equals(role)) {
			response.sendRedirect("/menuAdmin");
		} else if ("ROLE_PERSONAL".equals(role)) {
			response.sendRedirect("/menuPersonal");
		} else if ("ROLE_CLIENTE".equals(role)) {
			response.sendRedirect("/menuCliente");
		} else {
			response.sendRedirect("/inicioSesion?error=true");
		}
	}
}
