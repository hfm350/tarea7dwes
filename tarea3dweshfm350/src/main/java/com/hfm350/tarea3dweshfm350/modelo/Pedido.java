package com.hfm350.tarea3dweshfm350.modelo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime fechaPedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany
    @JoinColumn(name = "pedido_id")
    private List<Ejemplar> ejemplares;

    public Pedido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Ejemplar> getEjemplares() {
        return ejemplares;
    }

    public void setEjemplares(List<Ejemplar> ejemplares) {
        this.ejemplares = ejemplares;
    }

    @Override
    public String toString() {
        return "Pedido [id=" + id + ", fechaPedido=" + fechaPedido + ", cliente=" + cliente + ", ejemplares="
                + ejemplares + "]";
    }


}