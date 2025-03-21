package com.hfm350.tarea3dweshfm350.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "mensajes")
public class Mensaje implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column()
	private LocalDateTime tiempo;

	@Column
	private String mensaje;

	@ManyToOne
	@JoinColumn(name = "persona_id", nullable = true)
	private Persona persona;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "ejemplar_id", nullable = true)
	private Ejemplar ejemplar;

	@ManyToOne
	@JoinColumn(name = "cliente_id", nullable = true)
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "pedido_id", nullable = true)
	private Pedido pedido;

	public Mensaje() {
	}

	public Mensaje(LocalDateTime tiempo, String mensaje, Persona persona, Ejemplar ejemplar) {
		this.tiempo = tiempo;
		this.mensaje = mensaje;
		this.persona = persona;
		this.ejemplar = ejemplar;
	}

	public Mensaje(Long id, LocalDateTime tiempo, String mensaje, Persona persona, Ejemplar ejemplar) {
		super();
		this.id = id;
		this.tiempo = tiempo;
		this.mensaje = mensaje;
		this.persona = persona;
		this.ejemplar = ejemplar;
	}

	public Mensaje(LocalDateTime tiempo, String mensaje, Cliente cliente, Pedido pedido, Ejemplar ejemplar) {
		this.tiempo = tiempo;
		this.mensaje = mensaje;
		this.cliente = cliente;
		this.pedido = pedido;
		this.ejemplar = ejemplar;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getFechahora() {
		return tiempo;
	}

	public void setFechahora(LocalDateTime fechahora) {
		this.tiempo = fechahora;
	}

	public LocalDateTime getTiempo() {
		return tiempo;
	}

	public void setTiempo(LocalDateTime tiempo) {
		this.tiempo = tiempo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Pedido getPedido() {
		return pedido;
	}


	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Ejemplar getEjemplar() {
		return ejemplar;
	}

	public void setEjemplar(Ejemplar ejemplar) {
		this.ejemplar = ejemplar;
	}

	@Override
	public String toString() {
		return "Mensaje [id=" + id + ", fechahora=" + tiempo + ", mensaje=" + mensaje + ", persona=" + persona
				+ ", ejemplar=" + ejemplar + "]";
	}

}