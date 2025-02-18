package com.hfm350.tarea3dweshfm350.modelo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "personas")
public class Persona implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String nombre;

	@Column(unique=true)
	private String email;

	@OneToOne(mappedBy = "persona", cascade = CascadeType.ALL)
	private Credencial credenciales;
	
	@OneToMany(mappedBy = "persona", cascade = CascadeType.ALL)
	private List<Mensaje> mensajes = new LinkedList<Mensaje>();

	public Persona() {
	}

	public Persona(String nombre, String email) {
		this.nombre = nombre;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Credencial getCredenciales() {
		return credenciales;
	}

	public void setCredenciales(Credencial credenciales) {
		this.credenciales = credenciales;
	}

	public List<Mensaje> getMensajes() {
		return mensajes;
	}

	public void setMensajes(List<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}

	@Override
	public String toString() {
	    String credencialesStr = (credenciales != null) ? "Credencial [usuario=" + credenciales.getUsuario() + ", id=" + credenciales.getId() + "]" : "Sin credenciales asociadas";
	    return "Persona [id=" + id + ", nombre=" + nombre + ", email=" + email + ", credenciales=" + credencialesStr + "]";
	}


}