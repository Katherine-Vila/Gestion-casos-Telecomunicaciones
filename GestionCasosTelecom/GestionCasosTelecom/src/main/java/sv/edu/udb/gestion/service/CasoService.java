package sv.edu.udb.gestion.service;

import sv.edu.udb.gestion.dao.*;
import sv.edu.udb.gestion.entity.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Servicio de casos: concentra la lógica de negocio (estados, transiciones, código generado,
 * bitácora y reglas de vencimiento). Los DAO hacen el acceso a datos; aquí se valida el flujo.
 */
public class CasoService {
    private final CasoDAO casoDAO = new CasoDAO();
    private final BitacoraDAO bitacoraDAO = new BitacoraDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final DepartamentoDAO departamentoDAO = new DepartamentoDAO();


    // Arma el código tipo PRS240223001: 3 letras del depto + fecha de la solicitud (año/mes/día con 2 dígitos el año) + 3 números al azar
    private String generarCodigoIntento(Departamento depto, Date fechaSolicitud) {
        String codDepto = depto.getCodigo() != null ? depto.getCodigo().toUpperCase().replaceAll("[^A-Z]", "") : "";
        if (codDepto.length() < 3) {
            StringBuilder sb = new StringBuilder(codDepto);
            while (sb.length() < 3) {
                sb.append('X');
            }
            codDepto = sb.toString();
        } else if (codDepto.length() > 3) {
            codDepto = codDepto.substring(0, 3);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String fechaStr = sdf.format(fechaSolicitud);
        int num = new Random().nextInt(1000);
        String numStr = String.format("%03d", num);
        return codDepto + fechaStr + numStr;
    }


    //Genera un código que no esté repetido en la base (por si el azar choca con otro caso).
    private String generarCodigoUnico(Departamento depto, Date fechaSolicitud) throws SQLException {
        String codigo;
        int intentos = 0;
        do {
            codigo = generarCodigoIntento(depto, fechaSolicitud);
            intentos++;
            if (intentos > 50) {
                throw new IllegalStateException("No se pudo generar un código único; intente de nuevo.");
            }
        } while (casoDAO.buscarPorCodigo(codigo) != null);
        return codigo;
    }

    private static LocalDate aFechaLocal(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // ---------- 1) Pedir caso (solo queda “en espera”; todavía no hay código oficial) ----------

    /**
     * El jefe de área pide abrir un caso: se guarda la descripción y queda {@code EN_ESPERA}.
     * El código tipo PRS… se genera después, cuando desarrollo acepta y asigna ({@link #asignarProgramador}).
     *
     * @return id del caso insertado (útil para demos y pantallas que siguen el flujo con ese mismo registro)
     */
    public Long solicitarCaso(String descripcion, Long departamentoId, Long solicitanteId) throws SQLException {
        Departamento depto = departamentoDAO.buscarPorId(departamentoId);
        Usuario solicitante = usuarioDAO.buscarPorId(solicitanteId);
        if (depto == null || solicitante == null) {
            throw new IllegalArgumentException("Departamento o solicitante no válido.");
        }
        Caso caso = new Caso(descripcion, new Date(), depto, solicitante);
        caso.setCodigo(null);
        caso.setEstado(EstadoCaso.EN_ESPERA);
        caso.setPorcentajeAvance(0);
        casoDAO.insertar(caso);
        System.out.println("Solicitud guardada (en espera). Aún no tiene código; lo tendrá cuando el jefe de desarrollo la acepte.");
        return caso.getId();
    }

    // ---------- 2) El jefe de desarrollo “mira” el caso antes de decidir ----------



    // Sirve para que en pantalla se cargue el caso y se vea si está en espera.
    // No cambia nada solo: después se llama a rechazar la solicitud o a asignar programador.
    public Caso analizarCaso(Long casoId) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) {
            throw new IllegalArgumentException("Caso no encontrado.");
        }
        if (caso.getEstado() != EstadoCaso.EN_ESPERA) {
            throw new IllegalStateException("Este caso ya no está esperando respuesta del jefe de desarrollo.");
        }
        return caso;
    }


    // El jefe de desarrollo dice “no” al pedido inicial: queda RECHAZADO y se guarda el porqué.

    public void rechazarSolicitud(Long casoId, String argumento) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) {
            throw new IllegalArgumentException("Caso no encontrado.");
        }
        if (caso.getEstado() != EstadoCaso.EN_ESPERA) {
            throw new IllegalStateException("El caso no está en espera de respuesta.");
        }
        caso.setEstado(EstadoCaso.RECHAZADO);
        caso.setArgumentoRechazo(argumento);
        casoDAO.actualizar(caso);
        System.out.println("Solicitud rechazada.");
    }

    // ---------- 3) Aceptar: se genera el código, se asigna gente y pasa a EN_DESARROLLO ----------



    // El jefe de desarrollo acepta: aquí nace el código PRS…, se elige programador, fecha límite y probador.
    // El programador debe ser de los que dependen de ese jefe; el probador debe ser del mismo departamento
    // que pidió el caso.
    public void asignarProgramador(Long casoId, Long jefeDesarrolloId, Long programadorId, Date fechaLimite,
                                   String analisisDescripcion, Long probadorId) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) {
            throw new IllegalArgumentException("Caso no encontrado.");
        }
        if (caso.getEstado() != EstadoCaso.EN_ESPERA) {
            throw new IllegalStateException("El caso no está en espera de respuesta.");
        }

        Usuario jefe = usuarioDAO.buscarPorId(jefeDesarrolloId);
        if (jefe == null || jefe.getRol() != Rol.JEFE_DESARROLLO) {
            throw new IllegalArgumentException("Jefe de desarrollo no válido.");
        }

        Usuario programador = usuarioDAO.buscarPorId(programadorId);
        if (programador == null || programador.getRol() != Rol.PROGRAMADOR) {
            throw new IllegalArgumentException("Programador no válido.");
        }
        if (programador.getJefeDesarrollo() == null || !programador.getJefeDesarrollo().getId().equals(jefeDesarrolloId)) {
            throw new IllegalArgumentException("Ese programador no está a cargo de ese jefe de desarrollo.");
        }

        Usuario probador = usuarioDAO.buscarPorId(probadorId);
        if (probador == null || probador.getRol() != Rol.EMPLEADO) {
            throw new IllegalArgumentException("El probador debe ser un empleado del área que pidió el trabajo.");
        }
        if (probador.getDepartamento() == null
                || caso.getDepartamento() == null
                || !probador.getDepartamento().getId().equals(caso.getDepartamento().getId())) {
            throw new IllegalArgumentException("El probador debe ser del mismo departamento solicitante.");
        }

        caso.setCodigo(generarCodigoUnico(caso.getDepartamento(), caso.getFechaSolicitud()));
        caso.setJefeDesarrollo(jefe);
        caso.setProgramador(programador);
        caso.setProbador(probador);
        caso.setFechaLimite(fechaLimite);
        caso.setAnalisisDescripcion(analisisDescripcion);
        caso.setEstado(EstadoCaso.EN_DESARROLLO);
        casoDAO.actualizar(caso);
        System.out.println("Caso aceptado. Código asignado: " + caso.getCodigo() + " — Estado: en desarrollo.");
    }


    public void aceptarYAsignar(Long casoId, Long jefeDesarrolloId, Long programadorId, Date fechaLimite,
                                String analisisDescripcion, Long probadorId) throws SQLException {
        asignarProgramador(casoId, jefeDesarrolloId, programadorId, fechaLimite, analisisDescripcion, probadorId);
    }

    // ---------- 4) Bitácora: solo anota trabajo y porcentaje; no “cierra” el caso sola ----------


    // El programador escribe qué hizo y qué tanto lleva (0 a 100). No pasa a “esperando aprobación” solo por llegar al 100%.
    public void registrarBitacora(Long casoId, String descripcion, int nuevoPorcentaje) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) {
            throw new IllegalArgumentException("Caso no encontrado.");
        }
        if (caso.getEstado() != EstadoCaso.EN_DESARROLLO && caso.getEstado() != EstadoCaso.DEVUELTO) {
            throw new IllegalStateException("Solo se puede registrar bitácora mientras está en desarrollo o devuelto con observaciones.");
        }
        if (nuevoPorcentaje < 0 || nuevoPorcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        }

        Bitacora bit = new Bitacora(caso, new Date(), descripcion, nuevoPorcentaje);
        bitacoraDAO.insertar(bit);

        caso.setPorcentajeAvance(nuevoPorcentaje);
        casoDAO.actualizar(caso);
        System.out.println("Bitácora guardada. Avance del caso: " + nuevoPorcentaje + "%.");
    }

    public void actualizarBitacora(Long casoId, String descripcion, int nuevoPorcentaje) throws SQLException {
        registrarBitacora(casoId, descripcion, nuevoPorcentaje);
    }

    // ---------- 5) Cuando el programador dice “ya terminé mi parte” ----------


    // El programador marca que entregó; el caso pasa a esperar al probador del área.
    public void finalizarCaso(Long casoId) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) {
            throw new IllegalArgumentException("Caso no encontrado.");
        }
        if (caso.getEstado() != EstadoCaso.EN_DESARROLLO && caso.getEstado() != EstadoCaso.DEVUELTO) {
            throw new IllegalStateException("Solo se puede finalizar cuando está en desarrollo o devuelto para correcciones.");
        }
        caso.setEstado(EstadoCaso.ESPERANDO_APROBACION);
        casoDAO.actualizar(caso);
        System.out.println("Caso enviado a esperar aprobación del área solicitante.");
    }

    // ---------- 6) El probador aprueba o devuelve ----------


    // El probador está conforme: se guarda el día de puesta en producción y el caso queda FINALIZADO.
    public void aprobarCaso(Long casoId, Date fechaPuestaProduccion) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) {
            throw new IllegalArgumentException("Caso no encontrado.");
        }
        if (caso.getEstado() != EstadoCaso.ESPERANDO_APROBACION) {
            throw new IllegalStateException("El caso no está esperando aprobación.");
        }
        if (fechaPuestaProduccion == null) {
            throw new IllegalArgumentException("Debe indicar la fecha de puesta en producción.");
        }
        caso.setEstado(EstadoCaso.FINALIZADO);
        caso.setFechaPuestaProduccion(fechaPuestaProduccion);
        casoDAO.actualizar(caso);
        System.out.println("Caso aprobado y finalizado. Fecha de puesta en producción registrada.");
    }

    /** Sobrecarga: usa la fecha de hoy como puesta en producción (cómodo para pruebas rápidas). */
    public void aprobarCaso(Long casoId) throws SQLException {
        aprobarCaso(casoId, new Date());
    }


    // El probador no acepta el trabajo: queda DEVUELTO, se guardan observaciones y la fecha de devolución (empiezan los 7 días).
    public void rechazarCaso(Long casoId, String observaciones) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) {
            throw new IllegalArgumentException("Caso no encontrado.");
        }
        if (caso.getEstado() != EstadoCaso.ESPERANDO_APROBACION) {
            throw new IllegalStateException("El caso no está esperando aprobación.");
        }
        caso.setEstado(EstadoCaso.DEVUELTO);
        caso.setObservacionesRechazo(observaciones);
        caso.setFechaDevolucion(new Date());
        casoDAO.actualizar(caso);
        System.out.println("Caso devuelto con observaciones. Plazo de 7 días naturales desde la fecha de devolución (revisa vencimientos en el sistema).");
    }

    public void rechazarCasoConObservaciones(Long casoId, String observaciones) throws SQLException {
        rechazarCaso(casoId, observaciones);
    }

    // ---------- 7) Vencimientos: fecha límite del jefe o plazo de 7 días tras devolución del probador ----------


    /**
     * Conviene ejecutarla al arrancar la app o en un job diario: marca VENCIDO cuando ya no aplica el plazo.
     * <p>Criterio unificado (misma idea en ambos casos):</p>
     * <ul>
     *   <li><b>EN_DESARROLLO</b>: el día de {@code fechaLimite} sigue siendo válido para entregar;
     *       si {@code hoy} es posterior a ese día y el caso sigue en desarrollo → VENCIDO.</li>
     *   <li><b>DEVUELTO</b>: 7 días naturales desde la fecha de devolución (solo fecha);
     *       el día 7 del plazo sigue siendo válido; si {@code hoy} es posterior a ese último día → VENCIDO.</li>
     * </ul>
     */
    public void verificarVencimientos() throws SQLException {
        LocalDate hoy = LocalDate.now();

        List<Caso> enDesarrollo = casoDAO.listarPorEstado(EstadoCaso.EN_DESARROLLO);
        for (Caso c : enDesarrollo) {
            if (c.getFechaLimite() == null) {
                continue;
            }
            LocalDate limite = aFechaLocal(c.getFechaLimite());
            // Primer día vencido = día siguiente al de la fecha límite
            if (hoy.isAfter(limite)) {
                c.setEstado(EstadoCaso.VENCIDO);
                casoDAO.actualizar(c);
                System.out.println("Caso " + c.getCodigo() + " venció: se pasó la fecha límite sin entregar.");
            }
        }

        List<Caso> devueltos = casoDAO.listarPorEstado(EstadoCaso.DEVUELTO);
        for (Caso c : devueltos) {
            if (c.getFechaDevolucion() == null) {
                continue;
            }
            LocalDate inicio = aFechaLocal(c.getFechaDevolucion());
            // 7 días naturales: día 1 = inicio, día 7 = inicio + 6; vencido desde inicio + 7
            LocalDate ultimoDiaDelPlazo = inicio.plusDays(6);
            if (hoy.isAfter(ultimoDiaDelPlazo)) {
                c.setEstado(EstadoCaso.VENCIDO);
                casoDAO.actualizar(c);
                System.out.println("Caso " + c.getCodigo() + " venció: se agotaron los 7 días para corregir observaciones.");
            }
        }
    }

    // ---------- Informes (idea para Persona 3; aquí solo el esqueleto) ----------

    public void generarInforme(Date fechaInicio, Date fechaFin) throws SQLException {
        System.out.println("Informe de casos entre " + fechaInicio + " y " + fechaFin + " (implementar consultas con DAO).");
    }

    public List<Bitacora> obtenerBitacoras(Long casoId) throws SQLException {
        return bitacoraDAO.listarPorCaso(casoId);
    }

    // --- 1) Utilidades para interfaces de escritorio (UI) ---
    // Estas sobrecargas facilitan que las pantallas Swing llamen a la lógica del servicio.

    // --- 1) Obtener departamentos para combos (UI) ---
    public List<Departamento> obtenerTodosDepartamentos() throws SQLException {
        return departamentoDAO.listarTodos();
    }

    // --- 1) Obtener usuarios por rol (UI) ---
    // Ejemplos de rol esperados: "PROGRAMADOR", "EMPLEADO", "JEFE_AREA", etc.
    public List<Usuario> obtenerUsuariosPorRol(String rol) throws SQLException {
        if (rol == null || rol.trim().isEmpty()) {
            throw new IllegalArgumentException("Rol no válido.");
        }

        Rol rolEnum;
        try {
            rolEnum = Rol.valueOf(rol.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol no reconocido: " + rol, e);
        }

        return usuarioDAO.listarPorRol(rolEnum);
    }

    // --- 1) Buscar caso por ID (UI) ---
    public Caso obtenerCasoPorId(Long casoId) throws SQLException {
        if (casoId == null) {
            throw new IllegalArgumentException("ID de caso no válido.");
        }
        return casoDAO.buscarPorId(casoId);
    }

    // --- 1) Registrar bitácora sin porcentaje explícito (UI) ---
    public void registrarBitacora(Long casoId, String descripcion) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) {
            throw new IllegalArgumentException("Caso no encontrado.");
        }
        int porcentajeActual = caso.getPorcentajeAvance() != null ? caso.getPorcentajeAvance() : 0;
        registrarBitacora(casoId, descripcion, porcentajeActual);
    }

    // --- 1) Asignación simplificada (UI Punto 1) ---
    // Esta versión toma un programador y asume valores por defecto para completar el flujo real.
    public void asignarProgramador(Long casoId, Long programadorId) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) {
            throw new IllegalArgumentException("Caso no encontrado.");
        }
        if (caso.getEstado() != EstadoCaso.EN_ESPERA) {
            throw new IllegalStateException("El caso no está en estado EN_ESPERA.");
        }

        Usuario programador = usuarioDAO.buscarPorId(programadorId);
        if (programador == null || programador.getRol() != Rol.PROGRAMADOR) {
            throw new IllegalArgumentException("Programador no válido.");
        }
        if (programador.getJefeDesarrollo() == null || programador.getJefeDesarrollo().getId() == null) {
            throw new IllegalArgumentException("El programador no tiene jefe de desarrollo asignado.");
        }
        if (caso.getDepartamento() == null || caso.getDepartamento().getId() == null) {
            throw new IllegalArgumentException("El caso no tiene departamento asignado.");
        }

        // Elegimos un probador del mismo departamento (primer match).
        List<Usuario> empleados = usuarioDAO.listarPorRol(Rol.EMPLEADO);
        Usuario probador = null;
        for (Usuario e : empleados) {
            if (e.getDepartamento() != null
                    && e.getDepartamento().getId() != null
                    && e.getDepartamento().getId().equals(caso.getDepartamento().getId())) {
                probador = e;
                break;
            }
        }
        if (probador == null) {
            throw new IllegalStateException("No se encontró un probador (EMPLEADO) para el departamento del caso.");
        }

        Long jefeDesarrolloId = programador.getJefeDesarrollo().getId();
        Date fechaLimite = new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000); // 7 días por defecto
        String analisisDescripcion = "Análisis inicial (simulado desde interfaz de Punto 1).";

        // Llamamos al método completo del flujo real.
        asignarProgramador(
                casoId,
                jefeDesarrolloId,
                programadorId,
                fechaLimite,
                analisisDescripcion,
                probador.getId()
        );
    }
}
