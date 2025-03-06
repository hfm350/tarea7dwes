package com.hfm350.tarea3dweshfm350.modelo;

public class Sesion {

    private Perfil perfil;
    private String nombreUsuario; 

    public Sesion() {
    }

    public enum Perfil {
        INVITADO, PERSONAL, ADMIN, CLIENTE
    }

    
    public Sesion(Perfil perfil, String nombreUsuario) {
        super();
        this.perfil = perfil;
        this.nombreUsuario = nombreUsuario;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void cerrarSesion() {
        this.perfil = Perfil.INVITADO;
        this.nombreUsuario = null; 
    }
}
