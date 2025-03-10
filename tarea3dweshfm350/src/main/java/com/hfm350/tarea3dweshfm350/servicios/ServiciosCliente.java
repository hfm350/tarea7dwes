package com.hfm350.tarea3dweshfm350.servicios;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfm350.tarea3dweshfm350.modelo.Cliente;
import com.hfm350.tarea3dweshfm350.repositorios.ClienteRepository;

@Service
public class ServiciosCliente {

    @Autowired
    private ClienteRepository clienteRepo;

    public void guardar(Cliente cliente) {
        clienteRepo.save(cliente);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepo.findById(id);
    }
    
    public boolean existeNifNie(String nifNie) {
        return clienteRepo.existsByNifNie(nifNie);
    }
}

