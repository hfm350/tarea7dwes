package com.hfm350.tarea3dweshfm350.modelo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "ejemplares")
public class Ejemplar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "idplanta", nullable = false)
    private Planta planta;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @OneToMany(mappedBy = "ejemplar", cascade = CascadeType.ALL)
    private List<Mensaje> mensajes = new LinkedList<>();
    
    @Column(nullable = false)
    private boolean disponible = true;


    public Ejemplar() {}

    // Constructor sin ID para nuevos ejemplares
    public Ejemplar(Planta planta) {
        this.planta = planta;
    }

    public Ejemplar(Long id, String nombre, Planta planta, Pedido pedido, List<Mensaje> mensajes) {
        this.id = id;
        this.nombre = nombre;
        this.planta = planta;
        this.pedido = pedido;
        this.mensajes = mensajes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        setNombreAutomatico();
    }

    public String getNombre() {
        return nombre;
    }

    private void setNombreAutomatico() {
        if (planta != null && planta.getCodigo() != null && id != null) {
            this.nombre = planta.getCodigo() + "_" + id;
        }
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }
    

    public boolean isDisponible() {
		return disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	@Override
    public String toString() {
        return "Ejemplar{" +
               "id=" + id +
               ", nombre='" + (nombre != null ? nombre : "null") + '\'' +
               ", planta=" + (planta != null ? planta.getId() : "null") +
               ", pedido=" + (pedido != null ? pedido.getId() : "null") +
               '}';
    }
}
