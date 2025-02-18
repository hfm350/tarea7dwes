package com.hfm350.tarea3dweshfm350.modelo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosEjemplar;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosMensaje;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPersona;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;

@Service
public class Controlador {

	@Autowired
	private ServiciosPlanta servPlanta;

	@Autowired
	private ServiciosCredenciales servCredencial;

	@Autowired
	private ServiciosPersona servPersona;

	@Autowired
	private ServiciosMensaje serviciosMensaje;

	@Autowired
	private ServiciosEjemplar servEjemplar;

	

	public ServiciosPlanta getServiciosPlanta() {
		return servPlanta;
	}

	public ServiciosCredenciales getServiciosCredenciales() {
		return servCredencial;
	}

	public ServiciosPersona getServiciosPersona() {
		return servPersona;
	}

	public ServiciosMensaje getServiciosMensaje() {
		return serviciosMensaje;
	}

	public ServiciosEjemplar getServiciosEjemplar() {
		return servEjemplar;
	}

	private Long usuarioAutenticado;

	public void setUsuarioAutenticado(Long idUsuario) {
	    this.usuarioAutenticado = idUsuario;
	}

	public Long getUsuarioAutenticado() {
	    return usuarioAutenticado;
	}

}
