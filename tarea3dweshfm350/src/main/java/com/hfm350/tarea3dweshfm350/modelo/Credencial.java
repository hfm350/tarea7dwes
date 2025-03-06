package com.hfm350.tarea3dweshfm350.modelo;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "credenciales")
public class Credencial implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String usuario;

	@Column
	private String password;

	@OneToOne
	@JoinColumn(name = "idPersona", unique = true)
	private Persona persona;
	
    @OneToOne(optional = true)
    @JoinColumn(name = "cliente_id", unique = true)
    private Cliente cliente;


	public Credencial() {
	}

	public Credencial(String usuario, String password) {
		this.usuario = usuario;
		this.password = password;
	}

	public Credencial(String usuario, String password, Persona persona) {
		this.usuario = usuario;
		this.password = password;
		this.persona = persona;
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

	public String getPassword() {
		return password;
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

	@Override
	public String toString() {
	    String personaStr = (persona != null) ? "Persona [id=" + persona.getId() + ", nombre=" + persona.getNombre() + "]" : "Sin persona asociada";
	    return "Credencial [id=" + id + ", usuario=" + usuario + ", password=" + password + ", persona=" + personaStr + "]";
	}


}