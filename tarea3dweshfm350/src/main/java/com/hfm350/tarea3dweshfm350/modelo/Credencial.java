package com.hfm350.tarea3dweshfm350.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "credenciales")
public class Credencial implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String usuario;

    @Column(nullable = false)
    private String password;

    @OneToOne(optional = true)
    @JoinColumn(name = "cliente_id", unique = true)
    private Cliente cliente;

    @OneToOne
    @JoinColumn(name = "persona_id", unique = true, nullable = false)
    private Persona persona;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sesion.Perfil perfil;  

    public Credencial() {}

    public Credencial(String usuario, String password, Sesion.Perfil perfil, Persona persona) {
        this.usuario = usuario;
        this.password = password;
        this.perfil = perfil;
        this.persona = persona;
    }
    
    public Credencial(String usuario, String password, String rol, Persona persona) {
        this.usuario = usuario;
        this.password = password;
        this.perfil = perfil;
        this.persona = persona;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return usuario;
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Sesion.Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Sesion.Perfil perfil) {
        this.perfil = perfil;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }
}
