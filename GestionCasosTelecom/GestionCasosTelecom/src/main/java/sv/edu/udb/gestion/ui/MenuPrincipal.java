package sv.edu.udb.gestion.ui;

import sv.edu.udb.gestion.dao.*;
import sv.edu.udb.gestion.entity.*;
import sv.edu.udb.gestion.service.CasoService;
import sv.edu.udb.gestion.util.DatabaseConnection;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {
    private static Scanner scanner = new Scanner(System.in);
    private static Usuario usuarioActual = null;
    private static CasoService casoService = new CasoService();
    private static UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static DepartamentoDAO departamentoDAO = new DepartamentoDAO();
    private static CasoDAO casoDAO = new CasoDAO();

    public static void main(String[] args) {
        DatabaseConnection.testConnection();

        // Cada vez que abres el programa, revisamos si algún caso ya se venció (fecha límite o 7 días en devuelto)
        try {
            casoService.verificarVencimientos();
        } catch (SQLException e) {
            System.err.println("No se pudieron revisar vencimientos: " + e.getMessage());
        }

        // Cargar o crear un usuario administrador automáticamente
        try {
            cargarOcrearAdmin();
        } catch (SQLException e) {
            System.err.println("Error al cargar/crear el administrador: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Mostrar menú principal (basado en el rol del usuario)
        mostrarMenuPorRol();
    }


    //Busca un administrador en la BD; si no existe, lo crea.
    private static void cargarOcrearAdmin() throws SQLException {
        // Intentar buscar cualquier usuario con rol ADMIN
        List<Usuario> admins = usuarioDAO.listarPorRol(Rol.ADMIN);
        if (!admins.isEmpty()) {
            usuarioActual = admins.get(0);
            System.out.println("Sesión iniciada como: " + usuarioActual.getNombre() + " (ADMIN)");
            return;
        }

        // No hay admin, creamos uno por defecto
        System.out.println("No se encontró administrador. Creando uno por defecto...");
        Usuario admin = new Usuario("Administrador", "admin@empresa.com", "admin123", Rol.ADMIN);
        usuarioDAO.insertar(admin);
        usuarioActual = admin;
        System.out.println("Administrador creado con email: admin@empresa.com, contraseña: admin123");
    }

    private static void mostrarMenuPorRol() {
        // Como hemos cargado un admin, mostramos el menú de administrador directamente.
        menuAdmin();
    }

    // --- Menú ADMIN (sin login) ---
    private static void menuAdmin() {
        while (true) {
            System.out.println("\n--- ADMINISTRADOR ---");
            System.out.println("1. Registrar departamento");
            System.out.println("2. Registrar usuario (jefe área, jefe desarrollo, empleado, programador)");
            System.out.println("3. Listar departamentos");
            System.out.println("4. Listar usuarios");
            System.out.println("5. Ver solicitudes pendientes (EN_ESPERA)");
            System.out.println("6. Aceptar y asignar caso");
            System.out.println("7. Rechazar solicitud");
            System.out.println("8. Ver casos a mi cargo");
            System.out.println("9. Ver bitácora de un caso");
            System.out.println("10. Solicitar nuevo caso (como jefe de área)");
            System.out.println("11. Registrar bitácora (como programador)");
            System.out.println("12. Finalizar caso / enviar a probador (como programador)");
            System.out.println("13. Aprobar caso (como probador) — pide fecha de puesta en producción");
            System.out.println("14. Rechazar caso con observaciones (como probador)");
            System.out.println("15. Salir");
            System.out.print("Opción: ");
            int op = leerEntero();

            try {
                switch (op) {
                    case 1:
                        registrarDepartamento();
                        break;
                    case 2:
                        registrarUsuario();
                        break;
                    case 3:
                        listarDepartamentos();
                        break;
                    case 4:
                        listarUsuarios();
                        break;
                    case 5:
                        listarCasosEnEspera();
                        break;
                    case 6:
                        aceptarAsignarCaso();
                        break;
                    case 7:
                        rechazarSolicitud();
                        break;
                    case 8:
                        verCasosPorJefe();
                        break;
                    case 9:
                        verBitacora();
                        break;
                    case 10:
                        solicitarCaso();
                        break;
                    case 11:
                        actualizarBitacora();
                        break;
                    case 12:
                        finalizarCasoProgramador();
                        break;
                    case 13:
                        aprobarCaso();
                        break;
                    case 14:
                        rechazarCaso();
                        break;
                    case 15:
                        System.out.println("Saliendo...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Métodos auxiliares para entrada de datos
    private static int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static long leerLong() {
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Métodos de administración
    private static void registrarDepartamento() throws SQLException {
        System.out.print("Código (3 letras): ");
        String cod = scanner.nextLine();
        System.out.print("Nombre: ");
        String nom = scanner.nextLine();
        Departamento d = new Departamento(cod, nom);
        departamentoDAO.insertar(d);
        System.out.println("Departamento registrado con ID " + d.getId());
    }

    private static void registrarUsuario() throws SQLException {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Contraseña: ");
        String pass = scanner.nextLine();
        System.out.println("Rol: 1. JEFE_AREA  2. JEFE_DESARROLLO  3. EMPLEADO  4. PROGRAMADOR  5. ADMIN");
        int rolOpt = leerEntero();
        Rol rol;
        switch (rolOpt) {
            case 1: rol = Rol.JEFE_AREA; break;
            case 2: rol = Rol.JEFE_DESARROLLO; break;
            case 3: rol = Rol.EMPLEADO; break;
            case 4: rol = Rol.PROGRAMADOR; break;
            case 5: rol = Rol.ADMIN; break;
            default: throw new IllegalArgumentException("Rol inválido");
        }
        Usuario u = new Usuario(nombre, email, pass, rol);
        if (rol == Rol.JEFE_AREA || rol == Rol.EMPLEADO) {
            listarDepartamentos();
            System.out.print("ID del departamento al que pertenece: ");
            Long deptoId = leerLong();
            Departamento d = departamentoDAO.buscarPorId(deptoId);
            u.setDepartamento(d);
        }
        if (rol == Rol.PROGRAMADOR) {
            List<Usuario> jefes = usuarioDAO.listarPorRol(Rol.JEFE_DESARROLLO);
            System.out.println("Jefes de desarrollo disponibles:");
            for (Usuario j : jefes) {
                System.out.println(j.getId() + " - " + j.getNombre());
            }
            System.out.print("ID del jefe de desarrollo asignado: ");
            Long jefeId = leerLong();
            Usuario jefe = usuarioDAO.buscarPorId(jefeId);
            u.setJefeDesarrollo(jefe);
        }
        usuarioDAO.insertar(u);
        System.out.println("Usuario registrado con ID " + u.getId());
    }

    private static void listarDepartamentos() throws SQLException {
        List<Departamento> list = departamentoDAO.listarTodos();
        System.out.println("--- DEPARTAMENTOS ---");
        for (Departamento d : list) {
            System.out.println(d.getId() + " - " + d.getCodigo() + " - " + d.getNombre());
        }
    }

    private static void listarUsuarios() throws SQLException {
        List<Usuario> list = usuarioDAO.listarTodos();
        System.out.println("--- USUARIOS ---");
        for (Usuario u : list) {
            System.out.println(u.getId() + " - " + u.getNombre() + " (" + u.getRol() + ")");
        }
    }

    // Métodos de casos
    private static void listarCasosEnEspera() throws SQLException {
        List<Caso> casos = casoDAO.listarPorEstado(EstadoCaso.EN_ESPERA);
        System.out.println("--- SOLICITUDES PENDIENTES ---");
        for (Caso c : casos) {
            String cod = c.getCodigo() != null ? c.getCodigo() : "(aún sin código)";
            System.out.println(c.getId() + " - " + cod + " - " + c.getDescripcionSolicitud());
        }
    }

    private static void aceptarAsignarCaso() throws SQLException {
        System.out.print("ID del caso: ");
        Long casoId = leerLong();
        // Obtener programadores a cargo de este jefe (si tenemos jefe actual)
        // Aquí usamos el usuarioActual (que es admin) pero deberíamos seleccionar un jefe de desarrollo
        // Para simplificar, listamos todos los programadores y pedimos jefe de desarrollo
        List<Usuario> jefes = usuarioDAO.listarPorRol(Rol.JEFE_DESARROLLO);
        if (jefes.isEmpty()) {
            System.out.println("No hay jefes de desarrollo registrados.");
            return;
        }
        System.out.println("Jefes de desarrollo:");
        for (Usuario j : jefes) {
            System.out.println(j.getId() + " - " + j.getNombre());
        }
        System.out.print("ID del jefe de desarrollo que asignará el caso: ");
        Long jefeId = leerLong();
        Usuario jefe = usuarioDAO.buscarPorId(jefeId);
        List<Usuario> programadores = usuarioDAO.listarProgramadoresPorJefe(jefeId);
        if (programadores.isEmpty()) {
            System.out.println("No hay programadores bajo este jefe.");
            return;
        }
        System.out.println("Programadores a cargo de " + jefe.getNombre() + ":");
        for (Usuario p : programadores) {
            System.out.println(p.getId() + " - " + p.getNombre());
        }
        System.out.print("ID del programador asignado: ");
        Long progId = leerLong();
        System.out.print("Fecha límite (yyyy-MM-dd): ");
        String fechaStr = scanner.nextLine();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaLimite = null;
        try {
            fechaLimite = sdf.parse(fechaStr);
        } catch (ParseException e) {
            System.out.println("Formato inválido.");
            return;
        }
        System.out.print("Descripción del análisis: ");
        String analisis = scanner.nextLine();
        List<Usuario> empleados = usuarioDAO.listarPorRol(Rol.EMPLEADO);
        System.out.println("Posibles probadores:");
        for (Usuario e : empleados) {
            System.out.println(e.getId() + " - " + e.getNombre());
        }
        System.out.print("ID del probador: ");
        Long probId = leerLong();

        casoService.asignarProgramador(casoId, jefeId, progId, fechaLimite, analisis, probId);
    }

    private static void rechazarSolicitud() throws SQLException {
        System.out.print("ID del caso: ");
        Long casoId = leerLong();
        System.out.print("Argumento de rechazo: ");
        String arg = scanner.nextLine();
        casoService.rechazarSolicitud(casoId, arg);
    }

    private static void verCasosPorJefe() throws SQLException {
        System.out.print("ID del jefe de desarrollo: ");
        Long jefeId = leerLong();
        List<Caso> casos = casoDAO.listarPorJefeDesarrollo(jefeId);
        System.out.println("--- CASOS ASIGNADOS AL JEFE ---");
        for (Caso c : casos) {
            System.out.println(c.getId() + " - " + c.getCodigo() + " - Estado: " + c.getEstado() + " - Avance: " + c.getPorcentajeAvance() + "%");
        }
    }

    private static void verBitacora() throws SQLException {
        System.out.print("ID del caso: ");
        Long casoId = leerLong();
        List<Bitacora> bitacoras = casoService.obtenerBitacoras(casoId);
        System.out.println("--- BITÁCORA ---");
        for (Bitacora b : bitacoras) {
            System.out.println(b.getFecha() + " - Avance: " + b.getPorcentajeAvance() + "% - " + b.getDescripcion());
        }
    }

    private static void solicitarCaso() throws SQLException {
        System.out.print("Descripción del requerimiento: ");
        String desc = scanner.nextLine();
        // Pedir jefe de área que solicita
        List<Usuario> jefesArea = usuarioDAO.listarPorRol(Rol.JEFE_AREA);
        if (jefesArea.isEmpty()) {
            System.out.println("No hay jefes de área registrados.");
            return;
        }
        System.out.println("Jefes de área disponibles:");
        for (Usuario j : jefesArea) {
            System.out.println(j.getId() + " - " + j.getNombre());
        }
        System.out.print("ID del jefe de área solicitante: ");
        Long jefeAreaId = leerLong();
        Usuario solicitante = usuarioDAO.buscarPorId(jefeAreaId);
        if (solicitante == null || solicitante.getDepartamento() == null) {
            System.out.println("El jefe de área no tiene departamento asignado.");
            return;
        }
        casoService.solicitarCaso(desc, solicitante.getDepartamento().getId(), solicitante.getId());
    }

    private static void actualizarBitacora() throws SQLException {
        System.out.print("ID del caso: ");
        Long casoId = leerLong();
        System.out.print("Descripción del trabajo realizado: ");
        String desc = scanner.nextLine();
        System.out.print("Nuevo porcentaje de avance (0-100): ");
        int porc = leerEntero();
        casoService.registrarBitacora(casoId, desc, porc);
    }

    private static void finalizarCasoProgramador() throws SQLException {
        System.out.print("ID del caso: ");
        Long casoId = leerLong();
        casoService.finalizarCaso(casoId);
    }

    private static void aprobarCaso() throws SQLException {
        System.out.print("ID del caso: ");
        Long casoId = leerLong();
        System.out.print("Fecha de puesta en producción (yyyy-MM-dd): ");
        String fechaStr = scanner.nextLine();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaProd;
        try {
            fechaProd = sdf.parse(fechaStr);
        } catch (ParseException e) {
            System.out.println("Fecha inválida.");
            return;
        }
        casoService.aprobarCaso(casoId, fechaProd);
    }

    private static void rechazarCaso() throws SQLException {
        System.out.print("ID del caso: ");
        Long casoId = leerLong();
        System.out.print("Observaciones del rechazo: ");
        String obs = scanner.nextLine();
        casoService.rechazarCasoConObservaciones(casoId, obs);
    }
}
