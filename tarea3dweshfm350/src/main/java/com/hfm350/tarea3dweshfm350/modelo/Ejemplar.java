package com.hfm350.tarea3dweshfm350.modelo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ejemplares")
public class Ejemplar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "idplanta")
    private Planta planta;

    @ManyToOne
    @JoinColumn(name = "pedido_id")  
    private Pedido pedido;

    @OneToMany(mappedBy = "ejemplar", cascade = CascadeType.ALL)
    private List<Mensaje> mensajes = new LinkedList<Mensaje>();

    public Ejemplar() {}

    public Ejemplar(Long id, String nombre, Planta planta, Pedido pedido, List<Mensaje> mensajes) {
        super();
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
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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