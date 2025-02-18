package com.hfm350.tarea3dweshfm350.modelo;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "localizacion")
public class Localizacion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private int numSec;

	@Column
	private boolean exterior = false;


	@Column
	private char mesa;

	@ManyToOne
	@JoinColumn(name = "id_seccion")
	private Seccion seccion;

	@OneToOne
	@JoinColumn(name = "id_ejemplar")
	private Ejemplar ejemplar;

	public Localizacion() {

	}

	public Localizacion(Long id, int numSec, boolean exterior, char mesa, Seccion seccion,
			Ejemplar ejemplar) {
		super();
		this.id = id;
		this.numSec = numSec;
		this.exterior = exterior;
		this.mesa = mesa;
		this.seccion = seccion;
		this.ejemplar = ejemplar;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getNumSec() {
		return numSec;
	}

	public void setNumSec(int numSec) {
		this.numSec = numSec;
	}

	public boolean isExterior() {
		return exterior;
	}

	public void setExterior(boolean exterior) {
		this.exterior = exterior;
	}

	public char getMesa() {
		return mesa;
	}

	public void setMesa(char mesa) {
		this.mesa = mesa;
	}

	public Seccion getSeccion() {
		return seccion;
	}

	public void setSeccion(Seccion seccion) {
		this.seccion = seccion;
	}

	public Ejemplar getEjemplar() {
		return ejemplar;
	}

	public void setEjemplar(Ejemplar ejemplar) {
		this.ejemplar = ejemplar;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Localizacion [id=" + id + ", numSec=" + numSec + ", exterior=" + exterior + ", mesa=" + mesa + ", seccion=" + seccion + ", ejemplar=" + ejemplar + "]";
	}
	
	

}
