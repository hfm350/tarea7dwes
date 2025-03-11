package com.hfm350.tarea3dweshfm350.modelo;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaNac;


	@Column(unique = true, nullable = false)
	private String nifNie;

	@Column(unique = true, nullable = false)
	private String email;

	@Column
	private String direccion;

	@Column
	private String telefono;

	@OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
	private Credencial credenciales;

	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
	private List<Pedido> pedidos;

	public Cliente() {
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

	public LocalDate getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }
	
	public String getNifNie() {
		return nifNie;
	}

	public void setNifNie(String nifNie) {
		this.nifNie = nifNie;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Credencial getCredenciales() {
		return credenciales;
	}
	
	

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public void setCredenciales(Credencial credenciales) {
		this.credenciales = credenciales;
		if (credenciales != null) {
			credenciales.setCliente(this);
		}
	}

	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	public void setPersona(Persona personaGuardada) {
		
		
	}
	
	
}