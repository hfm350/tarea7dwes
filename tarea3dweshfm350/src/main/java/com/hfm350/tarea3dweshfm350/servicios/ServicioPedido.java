package com.hfm350.tarea3dweshfm350.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfm350.tarea3dweshfm350.modelo.Pedido;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.repositorios.PedidoRepository;
import com.hfm350.tarea3dweshfm350.repositorios.PersonaRepository;
import com.hfm350.tarea3dweshfm350.repositorios.PlantaRepository;

import jakarta.transaction.Transactional;

@Service
public class ServicioPedido {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private PersonaRepository personaRepository;

	@Autowired
	private PlantaRepository plantaRepository;

	
	 
}
