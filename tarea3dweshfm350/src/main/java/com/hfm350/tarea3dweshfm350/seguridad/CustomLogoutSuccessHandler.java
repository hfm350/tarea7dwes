package com.hfm350.tarea3dweshfm350.seguridad;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.hfm350.tarea3dweshfm350.servicios.ServicioPedido;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ServicioPedido servicioPedido;

    public CustomLogoutSuccessHandler(ServicioPedido servicioPedido) {
        this.servicioPedido = servicioPedido;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication != null) {
            String usuario = authentication.getName();
            Long clienteId = servicioPedido.obtenerIdClientePorUsuario(usuario);

            if (clienteId != null) {
                // Eliminar pedidos NO confirmados de la BD
                servicioPedido.eliminarPedidosNoConfirmados(clienteId);
            }
        }

        // Eliminar pedidos en sesión
        request.getSession().invalidate();

        response.sendRedirect("/inicioSesion");
    }
}
