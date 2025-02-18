package com.hfm350.tarea3dweshfm350.modelo;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "seccion")
public class Seccion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String nombre;

	@Column
	private double area;

	@OneToMany(mappedBy = "seccion")
	private List<Localizacion> localizaciones;

	public Seccion() {

	}
	
	public Seccion(Long id, String nombre, double area, List<Localizacion> localizaciones) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.area = area;
		this.localizaciones = localizaciones;
	}

	public Seccion(String nombre, double area) {
		this.nombre = nombre;
		this.area = area;
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

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public List<Localizacion> getLocalizaciones() {
		return localizaciones;
	}

	public void setLocalizaciones(List<Localizacion> localizaciones) {
		this.localizaciones = localizaciones;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Seccion [id=" + id + ", nombre=" + nombre + ", area=" + area + ", localizaciones=" + localizaciones
				+ "]";
	}

	
}
