package com.hfm350.tarea3dweshfm350.fachada;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.hfm350.tarea3dweshfm350.modelo.Controlador;
import com.hfm350.tarea3dweshfm350.modelo.Credencial;
import com.hfm350.tarea3dweshfm350.modelo.Ejemplar;
import com.hfm350.tarea3dweshfm350.modelo.Localizacion;
import com.hfm350.tarea3dweshfm350.modelo.Mensaje;
import com.hfm350.tarea3dweshfm350.modelo.Persona;
import com.hfm350.tarea3dweshfm350.modelo.Planta;
import com.hfm350.tarea3dweshfm350.modelo.Seccion;
import com.hfm350.tarea3dweshfm350.modelo.Sesion;
import com.hfm350.tarea3dweshfm350.modelo.Sesion.Perfil;
import com.hfm350.tarea3dweshfm350.repositorios.EjemplarRepository;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosCredenciales;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosEjemplar;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosLocalizacion;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosMensaje;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPersona;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosPlanta;
import com.hfm350.tarea3dweshfm350.servicios.ServiciosSeccion;

@Controller
public class FachadaAdmin {

	@Autowired
	@Lazy
	Controlador controlador;

	@Autowired
	private ServiciosMensaje servMensaje;

	@Autowired
	private ServiciosEjemplar servEjemplar;

	@Autowired
	private ServiciosPlanta servPlanta;

	@Autowired
	private ServiciosPersona servPersona;

	@Autowired
	private ServiciosCredenciales servCredenciales;

	@Autowired
	private ServiciosLocalizacion servLocalizacion;

	@Autowired
	private ServiciosSeccion servSeccion;

	@Autowired
	@Lazy
	private FachadaInvitado vistaFachadaInvitado;

	@Autowired
	@Lazy
	private FachadaPersonal vistaFachadaPersonal;

	Scanner sc = new Scanner(System.in);

	public void menuAdmin() {
		Sesion s = new Sesion(Perfil.ADMIN, "");
		int opcion = 0;
		boolean sesion = true;
		Long usuarioId = controlador.getUsuarioAutenticado();

		do {
			System.out.println("\n\t\tMenú Administrador - Nº: " + usuarioId);
			System.out.println("\n\t\t1-  Gestión plantas");
			System.out.println("\t\t2-  Gestión ejemplares");
			System.out.println("\t\t3-  Gestión mensajes");
			System.out.println("\t\t4-  Gestión persona");
			// System.out.println("\t\t5- Gestión de Seccion y Localización");
			System.out.println("\t\t9-  Cierre de Sesión");

			try {
				opcion = sc.nextInt();
				sc.nextLine();
				if (opcion != 1 && opcion != 2 && opcion != 3 && opcion != 4 && opcion != 5 && opcion != 9) {
					System.out.println("Opción invalida. Prueba otra vez. \n");
					continue;
				}

				switch (opcion) {
				case 1:
					gestionPlanta();
					break;
				case 2:
					gestionEjemplar();
					break;
				case 3:
					gestionMensaje();
					break;
				case 4:
					gestionPersona();
					break;
				case 5:
					// gestionSeccionesyLocalizaciones();
				case 9:
					System.out.println("Cerrando sesión de ADMINISTRADOR");
					s.cerrarSesion();
					sesion = false;
					vistaFachadaInvitado.mostrarMenuInvitado();
					break;
				}
			} catch (InputMismatchException e) {
				System.err.println("ERROR, porfavor ingrese un numero ENTERO");
				sc.next();
			}
		} while (sesion);
	}

	@SuppressWarnings("unused")
	private void gestionSeccionesyLocalizaciones() {
		int opcion = 0;
		do {
			System.out.println("\n\t\tMenu-Gestión-Secciones\n");
			System.out.println("\t\t1-  Registrar sección");
			System.out.println("\t\t2-  Registrar localización");
			System.out.println("\t\t3-  Duplicar localización");
			System.out.println("\t\t9-  Volver al menú ADMIN");

			try {
				opcion = sc.nextInt();
				sc.nextLine();
				if (opcion != 1 && opcion != 2 && opcion != 3 && opcion != 9) {
					System.out.println("Opción invalida. Prueba otra vez. \n");
					continue;
				}

				switch (opcion) {
				case 1:
					// registrarSeccion();
					break;
				case 2:
					// registrarLocalizacion();
					break;
				case 3:
					// duplicarLocalizacion();
					break;
				case 9:
					menuAdmin();
					break;
				}
			} catch (InputMismatchException e) {
				System.out.println("ERROR, porfavor ingrese un numero ENTERO");
				sc.next();
			}
		} while (opcion != 9);
	}

	@SuppressWarnings("unused")
	private void registrarSeccion() {
		System.out.println("\n\t\tRegistrar nueva SECCIóN: ");
		System.out.print("Nombre de la SECCIóN: ");
		String nombreSeccion = sc.nextLine().trim();
		System.out.print("Área de la SECCIóN(Tiene que ser un numero con decimales: ");
		double area = 0.0;

		try {
			area = Double.parseDouble(sc.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("ERROR , debe ser un DOUBLE");
			return;
		}

		Seccion s = new Seccion(nombreSeccion, area);
		servSeccion.insertar(s);

		System.out.println("SECCIóN registrada.");
	}

	@SuppressWarnings("unused")
	private void registrarLocalizacion() {
		System.out.println("\n\t\tRegistrar nueva localización: ");
		List<Seccion> secciones = servSeccion.obtenerTodasLasSecciones();
		for (Seccion seccion : secciones) {
			System.out.println("ID: " + seccion.getId() + " - Nombre: " + seccion.getNombre());
		}
		System.out.print("Introduce el ID de la SECCIóN donde deseas agregar la localización: ");
		int idSeccion = sc.nextInt();

		Optional<Seccion> seccion = servSeccion.obtenerSeccionPorId((long) idSeccion);
		if (!seccion.isPresent()) {
			System.out.println("SECCIóN no encontrada.");
			return;
		}
		Seccion s = seccion.get();

		int numLocalizacion = servLocalizacion.numeroDeSeccion(s) + 1;

		System.out.print("Introduce la mesa (un solo digito): ");
		char mesa = sc.next().charAt(0);

		boolean esExterior = false;
		boolean respuesta = false;
		while (!respuesta) {
			System.out.print("¿Es una localización exterior? (si/no): ");
			String rs = sc.next().trim().toLowerCase();

			if (rs.equals("si")) {
				esExterior = true;
				respuesta = true;
			} else if (rs.equals("no")) {
				esExterior = false;
				respuesta = true;
			} else {
				System.out.println("ERRO. Pon 'si' o 'no'.");
			}
		}

		Localizacion l = new Localizacion();
		l.setNumSec(numLocalizacion);
		l.setExterior(esExterior);
		l.setMesa(mesa);
		l.setSeccion(s);
		l.setEjemplar(null);
		servLocalizacion.insertar(l);

		System.out.println("LOCALIZACIóN registrada");
	}

	@SuppressWarnings("unused")
	private void duplicarLocalizacion() {
		System.out.println("\n\t\tDuplicar localización: ");
		List<Seccion> secciones = servSeccion.obtenerTodasLasSecciones();
		for (Seccion seccion : secciones) {
			System.out.println("ID: " + seccion.getId() + " - Nombre: " + seccion.getNombre());
		}
		System.out.print("Introduce el ID de la sección: ");
		Long idSeccion = sc.nextLong();

		Optional<Seccion> seccion = servSeccion.obtenerSeccionPorId(idSeccion);
		if (!seccion.isPresent()) {
			System.out.println("SECCIóN no encontrada.");
			return;
		}
		Seccion s = seccion.get();

		List<Localizacion> localizaciones = servLocalizacion.obtenerTodasLasLocalizaciones();
		for (Localizacion localizacion : localizaciones) {
			System.out.println("Nº: " + localizacion.getId());
		}
		System.out.print("Introduce el nº de la LOCALIZACIóN: ");
		int numLocalizacion = sc.nextInt();

		Optional<Localizacion> localizacion = servLocalizacion.buscarPorNumeroySeccion(s, numLocalizacion);
		if (!localizacion.isPresent()) {
			System.out.println("LOCALIZACIóN no encontrada.");
			return;
		}

		Localizacion l = localizacion.get();

		int numeroNuevo = servLocalizacion.numeroDeSeccion(s) + 1;

		Localizacion localizacionDuplicada = new Localizacion();
		localizacionDuplicada.setNumSec(numeroNuevo);
		localizacionDuplicada.setExterior(l.isExterior());
		localizacionDuplicada.setMesa(l.getMesa());
		localizacionDuplicada.setSeccion(s);
		localizacionDuplicada.setEjemplar(null);

		servLocalizacion.insertar(localizacionDuplicada);

		System.out.println("Localización duplicada con éxito.");
	}

	private void gestionPlanta() {
		System.out.println("\n\t\tMenu-Gestión-Ejemplar\n");
		int opcion = 0;

		do {
			System.out.println("\t\t1-  Insertar PLANTA");
			System.out.println("\t\t2-  Modificar PLANTA-NombreComun");
			System.out.println("\t\t3-  Modificar PLANTA-NombreCientifico");
			System.out.println("\t\t9-  Volver al menú ADMIN");

			try {
				opcion = sc.nextInt();
				sc.nextLine();
				if (opcion != 1 && opcion != 2 && opcion != 3 && opcion != 9) {
					System.out.println("Opción invalida. Prueba otra vez. \n");
					continue;
				}
				switch (opcion) {
				case 1:
					insertarPlanta();
					break;
				case 2:
					modificarPlantaNombreComun();
					break;
				case 3:
					modificarPlantaNombreCientifico();
					break;
				case 9:
					menuAdmin();
					break;
				}
			} catch (InputMismatchException e) {
				System.out.println("ERROR, porfavor ingrese un numero ENTERO");
				sc.next();
			}
		} while (opcion != 9);
	}

	private void modificarPlantaNombreComun() {
		Planta p = null;
		boolean codigoCorrecto = false;
		boolean encontrado = false;
		String codigo;
		ArrayList<Planta> listaDePlantas = (ArrayList<Planta>) servPlanta.findAll();
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
		do {
			System.out.println("Dime el CODIGO para poder cambiar el nombre de esa PLANTA");
			codigo = sc.nextLine();
			codigoCorrecto = servPlanta.validarCodigo(codigo);
			if (!codigoCorrecto) {
				System.out.println("CODIGO no valido");
			}
		} while (!codigoCorrecto);

		encontrado = servPlanta.codigoExistente(codigo);
		if (!encontrado) {
			System.out.println("CODIGO no existente");
		}

		System.out.print("Nombre Comun nuevo: ");
		String nombreNuevo = sc.nextLine().trim();
		try {
			boolean nuevo = servPlanta.actualizarNombreComun(codigo, nombreNuevo);
			if (nuevo) {
				System.out.println("Planta de codigo: " + codigo + ", ahora el nombre comun es:" + nombreNuevo);
			} else {
				System.out.println("ERROR en la actualización");
			}
		} catch (Exception ex) {
			System.out.println("ERROR en la actualización" + ex.getMessage());
		}
	}

	public void modificarPlantaNombreCientifico() {
		Planta p = null;
		boolean codigoCorrecto = false;
		boolean encontrado = false;
		String codigo;
		ArrayList<Planta> listaDePlantas = (ArrayList<Planta>) servPlanta.findAll();
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
		do {
			System.out.println("Dime el CODIGO para poder cambiar el nombre de esa PLANTA");
			codigo = sc.nextLine();
			codigoCorrecto = servPlanta.validarCodigo(codigo);
			if (!codigoCorrecto) {
				System.out.println("CODIGO no valido");
			}
		} while (!codigoCorrecto);

		encontrado = servPlanta.codigoExistente(codigo);
		if (!encontrado) {
			System.out.println("CODIGO no existente");
		}

		System.out.print("Nombre Cientifico nuevo: ");
		String nombreNuevo = sc.nextLine().trim();
		try {
			boolean nuevo = servPlanta.actualizarNombreCientifico(codigo, nombreNuevo);
			if (nuevo) {
				System.out.println("Planta de codigo: " + codigo + ", ahora el nombre cientifico es:" + nombreNuevo);
			} else {
				System.out.println("ERROR en la actualización");
			}
		} catch (Exception ex) {
			System.out.println("ERROR en la actualización" + ex.getMessage());
		}
	}

	public void insertarPlanta() {
		Planta p = new Planta();

		while (true) {
			System.out.print("Dime el CODIGO de la planta: ");
			String codigoPlanta = sc.nextLine().toUpperCase();

			if (!servPlanta.validarCodigo(codigoPlanta)) {
				System.out.println("3-20 Caracteres");
				continue;
			}

			p.setCodigo(codigoPlanta);
			System.out.println("Código de planta insertado correctamente.");
			break;
		}

		System.out.print("Nombre común: ");
		String nombreComun = sc.nextLine().trim();
		p.setNombreComun(nombreComun);

		System.out.print("Nombre científico: ");
		String nombreCientifico = sc.nextLine().trim();
		p.setNombreCientifico(nombreCientifico);

		boolean valido = servPlanta.validarPlanta(p);
		if (!valido) {
			System.out.println("Los datos que has introducido no son correctos. Revisa los valores ingresados.");
			return;
		}

		try {
			servPlanta.insertar(p);
			System.out.println("Planta INSERTADA.");
		} catch (Exception e) {
			System.out.println("ERROR al insertar la planta: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void gestionEjemplar() {
		int opcion = 0;
		do {
			System.out.println("\n\t\tMenu-Gestión-Ejemplar\n");
			System.out.println("\t\t1-  Insertar ejemplar de una planta.");
			System.out.println("\t\t2-  Ver ejemplares de planta.");
			System.out.println("\t\t3-  Ver mensajes de un ejemplar.");
			System.out.println("\t\t9-  Volver Menú ADMIN");
			try {
				opcion = sc.nextInt();
				sc.nextLine();
				if (opcion != 1 && opcion != 2 && opcion != 3 && opcion != 9) {
					System.out.println("Opción invalida. Prueba otra vez. \n");
					continue;
				}
				switch (opcion) {
				case 1:
					insertarEjemplar();
					break;
				case 2:
					ejemplarPlantas();
					break;
				case 3:
					verMensajesdeUnEjemplar();
					break;
				case 9:
					menuAdmin();
					break;
				}
			} catch (InputMismatchException e) {
				System.out.println("ERROR, porfavor ingrese un numero ENTERO");
				sc.next();
			}
		} while (opcion != 9);
	}

	private void verMensajesdeUnEjemplar() {
		boolean correcto = false;
		int contador = 0;
		List<Ejemplar> ejemplars = servEjemplar.findAll();
		List<Mensaje> mensajes = servMensaje.findAll();

		for (Ejemplar ejemplar : ejemplars) {
			contador++;
			System.out.println(contador + " º");
			System.out.println(ejemplar.getId() + " ==>\t" + ejemplar.getNombre());
			System.out.println("-----------------");
		}

		do {
			System.out.print("Dime el codigo del Ejemplar para ver su/s mensaje/s: ");
			Long codigo = sc.nextLong();

			for (Ejemplar ejemplar : ejemplars) {
				if (ejemplar.getId().equals(codigo)) {
					correcto = true;
					boolean mensajeEncontrado = false;

					for (Mensaje mensaje : mensajes) {
						if (mensaje.getEjemplar().getId().equals(codigo)) {
							mensajeEncontrado = true;
							System.out.println("Mensaje/s del ejemplar: " + ejemplar.getId() + "-"
									+ ejemplar.getNombre() + "  Mensaje: " + mensaje.getMensaje() + " - Persona: "
									+ mensaje.getPersona().getNombre());
						}
					}

					if (!mensajeEncontrado) {
						System.out.println("No existen mensajes para el codigo del ejemplar " + codigo);
					}
					break;
				}
			}

			if (!correcto) {
				System.out.println("ERROR codigo inexistente");
			}

		} while (!correcto);
	}

	private void ejemplarPlantas() {
		boolean correcto = false;
		int contador = 0;
		List<Ejemplar> ejemplares = servEjemplar.findAll();
		List<Planta> listaDePlantas = servPlanta.findAll();

		for (Planta p : listaDePlantas) {
			contador++;
			System.out.println(contador + "º");
			System.out.println(p.getCodigo() + " ==>\t" + p.getNombreComun());
			System.out.println("-----------------");
		}

		do {
			System.out.print("Dime el codigo de PLANTA para ver su EJEMPLAR: ");
			String codigo = sc.nextLine().trim().toUpperCase();

			for (Planta p : listaDePlantas) {
				if (p.getCodigo().equals(codigo)) {
					correcto = true;
					boolean ejemplarEncontrado = false;

					for (Ejemplar ej : ejemplares) {
						if (ej.getPlanta().getId() == p.getId()) {
							ejemplarEncontrado = true;
							System.out.println("Ejemplar encontrado: " + ej);
						}
					}

					if (!ejemplarEncontrado) {
						System.out.println("No existen ejemplares para esta planta " + p);
					}
					break;
				}
			}

			if (!correcto) {
				System.out.println("CODIGO INVALIDO");
			}

		} while (!correcto);
	}

	private void insertarEjemplar() {
		Ejemplar e = new Ejemplar();
		Mensaje m = new Mensaje();
		String codigoPlanta;
		boolean existeCodigoPlanta;

		do {
			List<Planta> plantas = servPlanta.findAll();
			if (plantas.isEmpty()) {
				System.out.println("No hay plantas registradas. No se puede registrar un ejemplar.");
				return;
			}

			List<Planta> plantaSinEjemplar = new ArrayList<>();
			for (Planta p : plantas) {
				if (!servEjemplar.existsByPlanta(p)) {
					plantaSinEjemplar.add(p);
				}
			}

			if (plantaSinEjemplar.isEmpty()) {
				System.out.println("No hay plantas sin EJEMPLAR");
				return;
			}

			int cont = 0;
			for (Planta p : plantaSinEjemplar) {
				cont++;
				System.out.println(cont + "º \t\t" + p.getCodigo() + ", " + p.getNombreComun());
			}

			System.out.println("Dime el codigo de la Planta");
			codigoPlanta = sc.nextLine();
			existeCodigoPlanta = servPlanta.codigoExistente(codigoPlanta);

			if (!existeCodigoPlanta) {
				System.out.println("El codigo no coincide con ninguna Planta");
			}

		} while (!existeCodigoPlanta);

		Planta planta = servPlanta.buscarPorCodigo(codigoPlanta);
		if (planta != null) {
			e.setPlanta(planta);

			System.out.println("Dime el nombre del ejemplar");
			String nombreEjemplar = sc.nextLine().toUpperCase();

			e.setNombre(nombreEjemplar);

			servEjemplar.insertar(nombreEjemplar, codigoPlanta);

			LocalDateTime tiempo = LocalDateTime.now();
			System.out.println("Dime un mensaje para poner: ");
			String msg = sc.nextLine();

			Long usuarioId = controlador.getUsuarioAutenticado();
			Optional<Long> personaID = servCredenciales.obtenerIdPersonaPorIdCredencial(usuarioId);

			if (!personaID.isPresent()) {
				System.out.println("No se encuentra la persona");
				return;
			}

			Optional<Persona> personaOptional = servPersona.buscarPorId(personaID.get());
			if (!personaOptional.isPresent()) {
				System.out.println("No se encuentra la persona con el ID obtenido.");
				return;
			}

			Persona persona = personaOptional.get();
			m = new Mensaje(tiempo, msg, persona, e);
			servMensaje.insertar(m);

			System.out.println("Ejemplar registrado exitosamente.");
			System.out.println("\nLista de todos los ejemplares registrados:");
			List<Ejemplar> ejemplares = servEjemplar.findAll();
			for (Ejemplar ejemplar : ejemplares) {
				System.out.println("ID: " + ejemplar.getId() + ", Nombre: " + ejemplar.getNombre() + ", Planta: "
						+ ejemplar.getPlanta().getNombreComun());
			}
		} else {
			System.out.println("Hubo un error al buscar la planta.");
		}
	}

	private void gestionMensaje() {
		System.out.println("\n\t\tMenu-Gestión-Mensaje\n");
		int opcion = 0;
		do {
			System.out.println("\t\t1-  Añadir mensaje a un ejemplar");
			System.out.println("\t\t2-  Mostrar mensajes");
			System.out.println("\t\t3-  Filtrar mensajes por persona");
			System.out.println("\t\t4-  Filtrar mensajes por rango de fechas");
			System.out.println("\t\t5-  Filtrar mensajes por tipo de planta");
			System.out.println("\t\t9-  Volver al menú ADMIN");

			try {
				opcion = sc.nextInt();
				sc.nextLine();
				if (opcion != 1 && opcion != 2 && opcion != 3 && opcion != 4 && opcion != 5 && opcion != 9) {
					System.out.println("Opción invalida. Prueba otra vez. \n");
					continue;
				}

				switch (opcion) {
				case 1:
					añadirMensajeAEjemplar();
					break;
				case 2:
					mostrarMensajes();
					break;
				case 3:
					filtrarMensajesPorPersona();
					break;
				case 4:
					filtrarMensajesPorRangoFechas();
					break;
				case 5:
					filtrarMensajesPorTipoPlanta();
					break;
				case 9:
					menuAdmin();
					break;
				}
			} catch (InputMismatchException e) {
				System.out.println("ERROR, porfavor ingrese un numero ENTERO");
				sc.next();
			}
		} while (opcion != 9);
	}

	private void añadirMensajeAEjemplar() {
	    List<Ejemplar> ejemplares = servEjemplar.findAll();
	    if (ejemplares.isEmpty()) {
	        System.out.println("No hay ejemplares registrados.");
	        return;
	    }

	    System.out.println("\nLista de ejemplares:");
	    for (Ejemplar ejemplar : ejemplares) {
	        System.out.println("ID: " + ejemplar.getId() + " - Nombre: " + ejemplar.getNombre() + " - Planta: "
	                + ejemplar.getPlanta().getNombreComun());
	    }

	    System.out.print("Introduce el ID del ejemplar al que deseas añadir un mensaje: ");
	    Long idEjemplar = sc.nextLong();
	    sc.nextLine();

	    Optional<Ejemplar> ejemplarOptional = servEjemplar.buscarPorId(idEjemplar);
	    if (!ejemplarOptional.isPresent()) {
	        System.out.println("Ejemplar no encontrado.");
	        return;
	    }

	    Ejemplar ejemplar = ejemplarOptional.get();

	    System.out.print("Introduce el mensaje: ");
	    String mensajeTexto = sc.nextLine();

	    Long usuarioId = controlador.getUsuarioAutenticado();
	    Optional<Long> personaID = servCredenciales.obtenerIdPersonaPorIdCredencial(usuarioId);

	    if (!personaID.isPresent()) {
	        System.out.println("No se encuentra la persona.");
	        return;
	    }

	    Optional<Persona> personaOptional = servPersona.buscarPorId(personaID.get());
	    if (!personaOptional.isPresent()) {
	        System.out.println("No se encuentra la persona con el ID obtenido.");
	        return;
	    }

	    Persona persona = personaOptional.get();
	    Mensaje mensaje = new Mensaje(LocalDateTime.now(), mensajeTexto, persona, ejemplar);
	    servMensaje.insertar(mensaje);

	    System.out.println("Mensaje añadido correctamente.");
	}


	private void mostrarMensajes() {
		List<Mensaje> mensajes = servMensaje.findAll();
		if (mensajes.isEmpty()) {
			System.out.println("No hay mensajes registrados.");
			return;
		}

		System.out.println("\n\t\tLista de mensajes:");
		for (Mensaje mensaje : mensajes) {
			System.out
					.println("Fecha: " + mensaje.getFechahora() + " - Mensaje: " + mensaje.getMensaje() + " - Persona: "
							+ mensaje.getPersona().getNombre() + " - Ejemplar: " + mensaje.getEjemplar().getNombre());
		}
	}

	private void filtrarMensajesPorPersona() {
		List<Persona> personas = servPersona.obtenerTodasLasPersonas();
		if (personas.isEmpty()) {
			System.out.println("No hay personas registradas.");
			return;
		}

		System.out.println("\nLista de personas:");
		for (Persona persona : personas) {
			System.out.println("ID: " + persona.getId() + " - Nombre: " + persona.getNombre());
		}

		System.out.print("Introduce el ID de la persona: ");
		Long idPersona = sc.nextLong();
		sc.nextLine();

		Optional<Persona> personaOptional = servPersona.buscarPorId(idPersona);
		if (!personaOptional.isPresent()) {
			System.out.println("Persona no encontrada.");
			return;
		}

		Persona persona = personaOptional.get();
		List<Mensaje> mensajes = servMensaje.buscarPorPersona(persona);

		if (mensajes.isEmpty()) {
			System.out.println("No hay mensajes para esta persona.");
			return;
		}

		System.out.println("\nMensajes de " + persona.getNombre() + ":");
		for (Mensaje mensaje : mensajes) {
			System.out.println("Fecha: " + mensaje.getFechahora() + " - Mensaje: " + mensaje.getMensaje()
					+ " - Ejemplar: " + mensaje.getEjemplar().getNombre());
		}
	}

	private void filtrarMensajesPorRangoFechas() {
		System.out.print("Fecha de inicio (YYYY-MM-DD): ");
		String fechaInicioStr = sc.nextLine();
		System.out.print("Fecha de fin (YYYY-MM-DD): ");
		String fechaFinStr = sc.nextLine();

		try {
			LocalDateTime fechaInicio = LocalDateTime.parse(fechaInicioStr);
			LocalDateTime fechaFin = LocalDateTime.parse(fechaFinStr);

			List<Mensaje> mensajes = servMensaje.buscarPorFechas(fechaInicio, fechaFin);

			if (mensajes.isEmpty()) {
				System.out.println("No hay MENSAJES en estas fechas");
				return;
			}

			System.out.println("Mensajes entre " + fechaInicioStr + " y " + fechaFinStr + ":");
			for (Mensaje mensaje : mensajes) {
				System.out.println("Fecha: " + mensaje.getFechahora() + " - Mensaje: " + mensaje.getMensaje()
						+ " - Persona: " + mensaje.getPersona().getNombre() + " - Ejemplar: "
						+ mensaje.getEjemplar().getNombre());
			}
		} catch (Exception e) {
			System.out.println("FECHA erronéa. Usa YYYY-MM-DD.");
		}
	}

	private void filtrarMensajesPorTipoPlanta() {
		List<Planta> plantas = servPlanta.findAll();
		if (plantas.isEmpty()) {
			System.out.println("No hay plantas registradas.");
			return;
		}

		System.out.println("\nLista de plantas:");
		for (Planta planta : plantas) {
			System.out.println("Código: " + planta.getCodigo() + " - Nombre: " + planta.getNombreComun());
		}

		System.out.print("Introduce el código de la planta: ");
		String codigoPlanta = sc.nextLine().toUpperCase();

		Planta planta = servPlanta.buscarPorCodigo(codigoPlanta);
		if (planta != null) {
			List<Mensaje> mensajes = servMensaje.buscarPorPlanta(planta);

			if (mensajes.isEmpty()) {
				System.out.println("No hay mensajes para esta planta.");
				return;
			}

			System.out.println("\nMensajes para " + planta.getNombreComun() + ":");
			for (Mensaje mensaje : mensajes) {
				System.out.println("Fecha: " + mensaje.getFechahora() + " - Mensaje: " + mensaje.getMensaje()
						+ " - Persona: " + mensaje.getPersona().getNombre() + " - Ejemplar: "
						+ mensaje.getEjemplar().getNombre());
			}
		} else {
			System.out.println("Planta no encontrada.");
		}
	}

	public void gestionPersona() {
		System.out.println("\n\t\tMenú-Gestión-Persona\n");
		Persona p = new Persona();
		Credencial c = new Credencial();

		String nombre = solicitarNombre();
		p.setNombre(nombre);

		String email;
		boolean emailValido = false;
		while (!emailValido) {
			email = solicitarEmail();
			if (servPersona.existeEmail(email)) {
				System.out.println("EMAIL ya registrado. Intente con otro.");
			} else {
				p.setEmail(email);
				emailValido = true;
			}
		}

		boolean usuarioValido = false;
		String usuario;
		String contrasena;
		do {
			System.out.print("Usuario: ");
			usuario = sc.nextLine();
			if (servCredenciales.existeUsuario(usuario)) {
				System.out.println("El usuario '" + usuario + "' ya está registrado. Elige otro nombre.");
			} else {
				usuarioValido = true;
				c.setUsuario(usuario);
			}
		} while (!usuarioValido);

		contrasena = solicitarContraseña();
		c.setPassword(contrasena);

		try {
			servPersona.insertar(p);
			Long idPersona = p.getId();

			if (idPersona == null || idPersona == 0) {
				System.out.println("ERROR en el registro de la persona. No hay ID asignado.");
				return;
			}

			servCredenciales.insertar(usuario, contrasena, idPersona);
			System.out.println("Registro exitoso. Usuario y credenciales creados correctamente.");

			System.out.println("\nListado de todas las personas registradas:");

			List<Persona> todasLasPersonas = servPersona.obtenerTodasLasPersonas();
			if (todasLasPersonas.isEmpty()) {
				System.out.println("No hay personas registradas.");
			} else {
				for (Persona persona : todasLasPersonas) {
					System.out.println(persona);
				}
			}

		} catch (Exception e) {
			System.err.println("Ocurrió un error durante el registro de las credenciales: " + e.getMessage());
		}
	}

	private String solicitarEmail() {
		while (true) {
			System.out.print("Dime el EMAIL de la persona a quien quieres REGISTRAR: ");
			String email = sc.nextLine().trim();
			if (emailValido(email)) {
				return email;
			} else {
				System.out.println("El email tiene que ser de este formato xxxxx@xxxx.com");
			}
		}
	}

	private String solicitarNombre() {
		while (true) {
			System.out.print("Dime el NOMBRE de la persona a quien quieres REGISTRAR: ");
			String nombre = sc.nextLine().trim();
			if (nombreValido(nombre)) {
				return nombre;
			} else {
				System.out.println("El NOMBRE tiene que tener la primera letra mayúscula");
			}
		}
	}

	private String solicitarUsuario() {
		while (true) {
			System.out.print("Usuario: ");
			String usuario = sc.nextLine();
			if (servCredenciales.existeUsuario(usuario)) {
				System.out.println("El usuario '" + usuario + "' ya está registrado. Elige otro nombre.");
			} else {
				return usuario;
			}
		}
	}

	private String solicitarContraseña() {
		while (true) {
			System.out.println("Dime la CONTRASEÑA que le quieres poner a tu USUARIO (4 digitos EJ: 0000): ");
			String contrasena = sc.nextLine();
			if (contraseñaValida(contrasena)) {
				return contrasena;
			} else {
				System.out.println("La CONTRASEÑA tiene que tener 4 digitos");
			}
		}
	}

	private boolean emailValido(String email) {
		return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.com$");
	}

	private boolean contraseñaValida(String contraseña) {
		return contraseña.matches("\\d{4}");
	}

	private boolean nombreValido(String nombre) {
		return nombre.matches("[A-Z][a-z]*");
	}
}
