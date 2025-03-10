package com.hfm350.tarea3dweshfm350.seguridad;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hfm350.tarea3dweshfm350.modelo.Credencial;

import java.util.Collection;
import java.util.Collections;

public class CredencialUserDetails implements UserDetails {

    private final Credencial credencial;

    public CredencialUserDetails(Credencial credencial) {
        this.credencial = credencial;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(credencial.getRol()));
    }


    @Override
    public String getPassword() {
        return credencial.getPassword();
    }

    @Override
    public String getUsername() {
        return credencial.getUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
