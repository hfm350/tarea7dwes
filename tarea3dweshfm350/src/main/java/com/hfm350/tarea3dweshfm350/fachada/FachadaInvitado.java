package com.hfm350.tarea3dweshfm350.fachada;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Optional;
import java.util.Scanner;

import com.hfm350.tarea3dweshfm350.fachada.*;
import com.hfm350.tarea3dweshfm350.modelo.Controlador;
import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.modelo.Pedido;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.modelo.Sesion;
import com.hfm350.tarea3dweshfm350.modelo.Sesion.Perfil;
import com.hfm350.tarea3dweshfm350.servicios.ServicioPedido;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPersona;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;

@Component
public class FachadaInvitado {

	@Autowired
	@Lazy
	private Controlador controlador;

	@Autowired
	private ServiciosCredenciales servCredenciales;

	@Autowired
	private ServiciosPlanta plantasServicio;

	@Autowired
	private ServicioPedido servicioPedido;

	@Autowired
	private ServiciosPersona servPersona;

	@Autowired
	@Lazy
	private FachadaAdmin adminVista;

	@Autowired
	@Lazy
	private FachadaPersonal personalVista;

	private Scanner sc = new Scanner(System.in);

	Sesion s = new Sesion(Perfil.INVITADO, "");

	public void mostrarMenuInvitado() {
	    int opcionSeleccionada = 0;
	    boolean sesionActiva = true;
	    do {
	        System.out.println("\n\n\t\t\tMENU INVITADO");
	        System.out.println("\tSeleccione una opción:");
	        System.out.println("\t1. Mostrar todas las plantas");
	        System.out.println("\t2. Iniciar sesión");
	        //System.out.println("\t3. Hacer pedido");
	        System.out.println("\t9. Cerrar Sesion");

	        try {
	            opcionSeleccionada = sc.nextInt();
	            switch (opcionSeleccionada) {
	            case 1:
	                mostrarTodasLasPlantas();
	                mostrarMenuInvitado();
	                break;
	            case 2:
	                iniciarSesion();
	                sesionActiva = true;
	                break;
	            case 3:
	                //hacerPedido();
	                break;
	            case 9:
	                System.out.println("Cierre Sesion HECHO");
	                sesionActiva = false;
	                break;
	            default:
	                System.out.println("OPCION NO VALIDA");
	            }
	        } catch (Exception e) {
	            System.out.println("ENTRADA NO VALIDA");
	            sc.nextLine();
	        }
	    } while (sesionActiva);
	}


	public void hacerPedido() {
		sc.nextLine();
		System.out.print("Ingrese su nombre de usuario: ");
		String nombreUsuario = sc.nextLine().toUpperCase();
		System.out.print("Ingrese su contraseña: ");
		String clave = sc.nextLine().toLowerCase();

		if (servCredenciales.autenticar(nombreUsuario, clave)) {
			System.out.print("\n");

			if (nombreUsuario.equalsIgnoreCase("ADMIN") && clave.equals("admin")) {
				s.setPerfil(Perfil.ADMIN);
				System.out.println("\t\tHola ADMIN");
			} else {
				s.setPerfil(Perfil.PERSONAL);
				System.out.println("\t\tHola " + nombreUsuario);
			}

			if (s.getPerfil() == Perfil.ADMIN || s.getPerfil() == Perfil.PERSONAL) {
				ArrayList<Planta> listaDePlantas = (ArrayList<Planta>) plantasServicio.findAll();
				for (Planta p : listaDePlantas) {
					System.out.println(p);
				}
				System.out.print("Ingrese el ID de la planta que quieres pedir: ");
				Long idPlanta = sc.nextLong();
				Planta planta = plantasServicio.buscarPorID(idPlanta);
				if (planta != null) {
					Pedido pedido = servicioPedido.registrarPedido(nombreUsuario, idPlanta);
					System.out.println("¡Pedido realizado con ÉXITO!");
					System.out.println(pedido);
				} else {
					System.out.println("Planta NO ENCONTRADA");
				}
			} else {
				System.out.println("DISPONIBLE SOLO PARA USUARIOS REGISTRADOS");
			}

		} else {
			System.out.println("Usuario o contraseña incorrectos.");
			mostrarMenuInvitado();
		}
	}

	public void iniciarSesion() {
		sc.nextLine();
		System.out.print("Ingrese su nombre de usuario: ");
		String nombreUsuario = sc.nextLine().toUpperCase().trim();
		System.out.print("Ingrese su contraseña: ");
		String clave = sc.nextLine().toLowerCase().trim();

		if (servCredenciales.autenticar(nombreUsuario, clave)) {

			Optional<Credencial> usuarioId = servCredenciales.buscarPersonaPorId(nombreUsuario);

			Credencial c = usuarioId.get();

			controlador.setUsuarioAutenticado(c.getId());

			System.out.print("\n");
			if (nombreUsuario.equalsIgnoreCase("ADMIN") && clave.equals("admin")) {
				s.setPerfil(Perfil.ADMIN);
				adminVista.menuAdmin();
			} else {
				s.setPerfil(Perfil.PERSONAL);
				personalVista.menuPersonal();
			}

		} else {
			System.out.println("Usuario o contraseña incorrectos.");
			mostrarMenuInvitado();
		}
	}

	public void mostrarTodasLasPlantas() {
		ArrayList<Planta> listaDePlantas = (ArrayList<Planta>) plantasServicio.findAll();
		if (listaDePlantas == null || listaDePlantas.isEmpty()) {
			System.out.println("NO HAY PLANTAS");
			return;
		}
		int contador = 0;
		System.out.println("\t\t\tLISTA DE PLANTAS DISPONIBLES");
		for (Planta planta : listaDePlantas) {
			contador++;
			System.out.println(contador + "º");
			System.out.println(planta);
			System.out.println("-----------------");

		}
		mostrarMenuInvitado();

	}
}
