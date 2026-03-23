package sv.edu.udb.gestion.service;

import sv.edu.udb.gestion.dao.*;
import sv.edu.udb.gestion.entity.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CasoService {
    private CasoDAO casoDAO = new CasoDAO();
    private BitacoraDAO bitacoraDAO = new BitacoraDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private DepartamentoDAO departamentoDAO = new DepartamentoDAO();

    // Genera código de caso: códigoDepartamento + YYMMDD + 3 dígitos aleatorios
    private String generarCodigo(Departamento depto, Date fecha) {
        String codDepto = depto.getCodigo().toUpperCase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String fechaStr = sdf.format(fecha);
        Random rand = new Random();
        int num = rand.nextInt(1000); // 0-999
        String numStr = String.format("%03d", num);
        return codDepto + fechaStr + numStr;
    }

    // 1. Jefe de área solicita un caso
    public void solicitarCaso(String descripcion, Long departamentoId, Long solicitanteId) throws SQLException {
        Departamento depto = departamentoDAO.buscarPorId(departamentoId);
        Usuario solicitante = usuarioDAO.buscarPorId(solicitanteId);
        if (depto == null || solicitante == null) {
            throw new IllegalArgumentException("Departamento o solicitante no válido");
        }
        Caso caso = new Caso(descripcion, new Date(), depto, solicitante);
        caso.setCodigo(generarCodigo(depto, new Date()));
        caso.setEstado(EstadoCaso.EN_ESPERA);
        caso.setPorcentajeAvance(0);
        casoDAO.insertar(caso);
        System.out.println("Caso solicitado con código: " + caso.getCodigo());
    }

    // 2. Jefe de desarrollo rechaza solicitud
    public void rechazarSolicitud(Long casoId, String argumento) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) throw new IllegalArgumentException("Caso no encontrado");
        if (caso.getEstado() != EstadoCaso.EN_ESPERA) {
            throw new IllegalStateException("El caso no está en espera de respuesta");
        }
        caso.setEstado(EstadoCaso.RECHAZADO);
        caso.setArgumentoRechazo(argumento);
        casoDAO.actualizar(caso);
        System.out.println("Solicitud rechazada.");
    }

    // 3. Jefe de desarrollo acepta y asigna programador, fecha límite, probador
    public void aceptarYAsignar(Long casoId, Long programadorId, Date fechaLimite, String analisisDescripcion, Long probadorId) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) throw new IllegalArgumentException("Caso no encontrado");
        if (caso.getEstado() != EstadoCaso.EN_ESPERA) {
            throw new IllegalStateException("El caso no está en espera de respuesta");
        }

        Usuario programador = usuarioDAO.buscarPorId(programadorId);
        if (programador == null || programador.getRol() != Rol.PROGRAMADOR) {
            throw new IllegalArgumentException("Programador no válido");
        }
        Usuario probador = usuarioDAO.buscarPorId(probadorId);
        if (probador == null || probador.getRol() != Rol.EMPLEADO) {
            throw new IllegalArgumentException("Probador debe ser un empleado del área funcional");
        }

        caso.setProgramador(programador);
        caso.setProbador(probador);
        caso.setFechaLimite(fechaLimite);
        caso.setAnalisisDescripcion(analisisDescripcion);
        caso.setEstado(EstadoCaso.EN_DESARROLLO);
        casoDAO.actualizar(caso);
        System.out.println("Caso asignado y en desarrollo.");
    }

    // 4. Programador actualiza bitácora y porcentaje
    public void actualizarBitacora(Long casoId, String descripcion, int nuevoPorcentaje) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) throw new IllegalArgumentException("Caso no encontrado");
        if (caso.getEstado() != EstadoCaso.EN_DESARROLLO && caso.getEstado() != EstadoCaso.DEVUELTO) {
            throw new IllegalStateException("El caso no está en desarrollo o devuelto");
        }
        if (nuevoPorcentaje < 0 || nuevoPorcentaje > 100) {
            throw new IllegalArgumentException("Porcentaje debe estar entre 0 y 100");
        }

        Bitacora bit = new Bitacora(caso, new Date(), descripcion, nuevoPorcentaje);
        bitacoraDAO.insertar(bit);

        caso.setPorcentajeAvance(nuevoPorcentaje);
        if (nuevoPorcentaje == 100) {
            caso.setEstado(EstadoCaso.ESPERANDO_APROBACION);
            System.out.println("Caso finalizado por programador, pasa a esperar aprobación.");
        }
        casoDAO.actualizar(caso);
        System.out.println("Bitácora registrada. Avance: " + nuevoPorcentaje + "%");
    }

    // 5. Probador aprueba o rechaza
    public void aprobarCaso(Long casoId) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) throw new IllegalArgumentException("Caso no encontrado");
        if (caso.getEstado() != EstadoCaso.ESPERANDO_APROBACION) {
            throw new IllegalStateException("El caso no está esperando aprobación");
        }
        caso.setEstado(EstadoCaso.FINALIZADO);
        casoDAO.actualizar(caso);
        System.out.println("Caso aprobado y finalizado.");
    }

    public void rechazarCasoConObservaciones(Long casoId, String observaciones) throws SQLException {
        Caso caso = casoDAO.buscarPorId(casoId);
        if (caso == null) throw new IllegalArgumentException("Caso no encontrado");
        if (caso.getEstado() != EstadoCaso.ESPERANDO_APROBACION) {
            throw new IllegalStateException("El caso no está esperando aprobación");
        }
        caso.setEstado(EstadoCaso.DEVUELTO);
        caso.setObservacionesRechazo(observaciones);
        // Reiniciamos porcentaje? No, solo se registra observación
        casoDAO.actualizar(caso);
        System.out.println("Caso devuelto con observaciones. El programador tiene 7 días para corregir.");
        // Aquí se podría programar una tarea para verificar vencimiento, pero lo dejamos manual.
    }

    // Verificar vencimiento (se debe llamar periódicamente)
    public void verificarVencimientos() throws SQLException {
        List<Caso> casos = casoDAO.listarPorEstado(EstadoCaso.EN_DESARROLLO);
        Date hoy = new Date();
        for (Caso c : casos) {
            if (c.getFechaLimite() != null && c.getFechaLimite().before(hoy)) {
                c.setEstado(EstadoCaso.VENCIDO);
                casoDAO.actualizar(c);
                System.out.println("Caso " + c.getCodigo() + " ha vencido.");
            }
        }
    }

    // 6. Generar informe por rango de fechas (cantidad de casos cumplidos, en desarrollo, rechazados)
    // Esto se puede implementar con consultas SQL agrupadas. Aquí solo mostramos la idea.
    public void generarInforme(Date fechaInicio, Date fechaFin) throws SQLException {
        // Implementar según necesidad
        System.out.println("Informe de casos entre " + fechaInicio + " y " + fechaFin);
        // Podrías hacer consultas con COUNT y GROUP BY estado.
    }

    // Método auxiliar para obtener bitácoras de un caso
    public List<Bitacora> obtenerBitacoras(Long casoId) throws SQLException {
        return bitacoraDAO.listarPorCaso(casoId);
    }
}
