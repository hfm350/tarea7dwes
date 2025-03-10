package com.hfm350.tarea3dweshfm350.seguridad;

import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.repositorios.CredencialRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CredencialUserDetailsService implements UserDetailsService {

    private final CredencialRepository credencialRepository;

    public CredencialUserDetailsService(CredencialRepository credencialRepository) {
        this.credencialRepository = credencialRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credencial credencial = credencialRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        System.out.println("🔍 Usuario autenticado: " + credencial.getUsuario());
        System.out.println("🔍 Rol en la BD: " + credencial.getRol());

        return new CredencialUserDetails(credencial);
    }


}
