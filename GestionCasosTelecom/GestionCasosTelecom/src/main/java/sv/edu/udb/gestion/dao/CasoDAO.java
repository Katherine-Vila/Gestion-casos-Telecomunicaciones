package sv.edu.udb.gestion.dao;

import sv.edu.udb.gestion.entity.*;
import sv.edu.udb.gestion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CasoDAO {
    private DepartamentoDAO deptoDAO = new DepartamentoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void insertar(Caso caso) throws SQLException {
        String sql = "INSERT INTO casos (codigo, descripcion_solicitud, fecha_solicitud, estado, porcentaje_avance, " +
                "fecha_limite, fecha_puesta_produccion, fecha_devolucion, analisis_descripcion, argumento_rechazo, observaciones_rechazo, " +
                "departamento_id, solicitante_id, jefe_desarrollo_id, programador_id, probador_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (caso.getCodigo() != null) {
                ps.setString(1, caso.getCodigo());
            } else {
                ps.setNull(1, Types.VARCHAR);
            }
            ps.setString(2, caso.getDescripcionSolicitud());
            ps.setDate(3, new Date(caso.getFechaSolicitud().getTime()));
            ps.setString(4, caso.getEstado().name());
            ps.setInt(5, caso.getPorcentajeAvance());
            ps.setDate(6, caso.getFechaLimite() != null ? new Date(caso.getFechaLimite().getTime()) : null);
            ps.setDate(7, caso.getFechaPuestaProduccion() != null ? new Date(caso.getFechaPuestaProduccion().getTime()) : null);
            ps.setDate(8, caso.getFechaDevolucion() != null ? new Date(caso.getFechaDevolucion().getTime()) : null);
            ps.setString(9, caso.getAnalisisDescripcion());
            ps.setString(10, caso.getArgumentoRechazo());
            ps.setString(11, caso.getObservacionesRechazo());
            ps.setLong(12, caso.getDepartamento().getId());
            ps.setLong(13, caso.getSolicitante().getId());
            if (caso.getJefeDesarrollo() != null && caso.getJefeDesarrollo().getId() != null) {
                ps.setLong(14, caso.getJefeDesarrollo().getId());
            } else {
                ps.setNull(14, Types.BIGINT);
            }
            if (caso.getProgramador() != null && caso.getProgramador().getId() != null) {
                ps.setLong(15, caso.getProgramador().getId());
            } else {
                ps.setNull(15, Types.BIGINT);
            }
            if (caso.getProbador() != null && caso.getProbador().getId() != null) {
                ps.setLong(16, caso.getProbador().getId());
            } else {
                ps.setNull(16, Types.BIGINT);
            }

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                caso.setId(rs.getLong(1));
            }
        }
    }

    public Caso buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM casos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearCaso(rs);
            }
            return null;
        }
    }

    public Caso buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM casos WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearCaso(rs);
            }
            return null;
        }
    }

    public List<Caso> listarPorEstado(EstadoCaso estado) throws SQLException {
        List<Caso> lista = new ArrayList<>();
        String sql = "SELECT * FROM casos WHERE estado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearCaso(rs));
            }
        }
        return lista;
    }

    public List<Caso> listarPorJefeDesarrollo(Long jefeId) throws SQLException {
        List<Caso> lista = new ArrayList<>();
        String sql = "SELECT * FROM casos WHERE jefe_desarrollo_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, jefeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearCaso(rs));
            }
        }
        return lista;
    }

    public List<Caso> listarPorProgramador(Long programadorId) throws SQLException {
        List<Caso> lista = new ArrayList<>();
        String sql = "SELECT * FROM casos WHERE programador_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, programadorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearCaso(rs));
            }
        }
        return lista;
    }

    public List<Caso> listarPorProbador(Long probadorId) throws SQLException {
        List<Caso> lista = new ArrayList<>();
        String sql = "SELECT * FROM casos WHERE probador_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, probadorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearCaso(rs));
            }
        }
        return lista;
    }

    public void actualizar(Caso caso) throws SQLException {
        String sql = "UPDATE casos SET codigo=?, descripcion_solicitud=?, fecha_solicitud=?, estado=?, " +
                "porcentaje_avance=?, fecha_limite=?, fecha_puesta_produccion=?, fecha_devolucion=?, analisis_descripcion=?, argumento_rechazo=?, " +
                "observaciones_rechazo=?, departamento_id=?, solicitante_id=?, jefe_desarrollo_id=?, " +
                "programador_id=?, probador_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (caso.getCodigo() != null) {
                ps.setString(1, caso.getCodigo());
            } else {
                ps.setNull(1, Types.VARCHAR);
            }
            ps.setString(2, caso.getDescripcionSolicitud());
            ps.setDate(3, new Date(caso.getFechaSolicitud().getTime()));
            ps.setString(4, caso.getEstado().name());
            ps.setInt(5, caso.getPorcentajeAvance());
            ps.setDate(6, caso.getFechaLimite() != null ? new Date(caso.getFechaLimite().getTime()) : null);
            ps.setDate(7, caso.getFechaPuestaProduccion() != null ? new Date(caso.getFechaPuestaProduccion().getTime()) : null);
            ps.setDate(8, caso.getFechaDevolucion() != null ? new Date(caso.getFechaDevolucion().getTime()) : null);
            ps.setString(9, caso.getAnalisisDescripcion());
            ps.setString(10, caso.getArgumentoRechazo());
            ps.setString(11, caso.getObservacionesRechazo());
            ps.setLong(12, caso.getDepartamento().getId());
            ps.setLong(13, caso.getSolicitante().getId());
            if (caso.getJefeDesarrollo() != null && caso.getJefeDesarrollo().getId() != null) {
                ps.setLong(14, caso.getJefeDesarrollo().getId());
            } else {
                ps.setNull(14, Types.BIGINT);
            }
            if (caso.getProgramador() != null && caso.getProgramador().getId() != null) {
                ps.setLong(15, caso.getProgramador().getId());
            } else {
                ps.setNull(15, Types.BIGINT);
            }
            if (caso.getProbador() != null && caso.getProbador().getId() != null) {
                ps.setLong(16, caso.getProbador().getId());
            } else {
                ps.setNull(16, Types.BIGINT);
            }
            ps.setLong(17, caso.getId());

            ps.executeUpdate();
        }
    }

    // --- 3) Reportes por rango de fechas (DAO) ---
    public Map<EstadoCaso, Integer> contarCasosPorEstadoYFechas(Date fechaInicio, Date fechaFin) throws SQLException {
        Map<EstadoCaso, Integer> conteoPorEstado = new HashMap<>();

        // Inicializamos solo los estados que el reporte de Punto 3 necesita.
        conteoPorEstado.put(EstadoCaso.FINALIZADO, 0);
        conteoPorEstado.put(EstadoCaso.EN_DESARROLLO, 0);
        conteoPorEstado.put(EstadoCaso.RECHAZADO, 0);

        // En tu BD, la columna de creación/solicitud que se usa en INSERT es: fecha_solicitud.
        String sql = "SELECT estado, COUNT(*) AS cantidad FROM casos " +
                "WHERE fecha_solicitud BETWEEN ? AND ? " +
                "GROUP BY estado";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, fechaInicio);
            pstmt.setDate(2, fechaFin);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String estadoStr = rs.getString("estado");
                int cantidad = rs.getInt("cantidad");

                try {
                    EstadoCaso estado = EstadoCaso.valueOf(estadoStr);
                    if (conteoPorEstado.containsKey(estado)) {
                        conteoPorEstado.put(estado, cantidad);
                    }
                } catch (IllegalArgumentException e) {
                    // Ignoramos estados que no existan en el enum o no nos interesen.
                    System.err.println("Advertencia: Estado de caso no reconocido: " + estadoStr);
                }
            }
        }

        return conteoPorEstado;
    }

    private Caso mapearCaso(ResultSet rs) throws SQLException {
        Caso c = new Caso();
        c.setId(rs.getLong("id"));
        c.setCodigo(rs.getString("codigo"));
        c.setDescripcionSolicitud(rs.getString("descripcion_solicitud"));
        c.setFechaSolicitud(rs.getDate("fecha_solicitud"));
        c.setEstado(EstadoCaso.valueOf(rs.getString("estado")));
        c.setPorcentajeAvance(rs.getInt("porcentaje_avance"));
        c.setFechaLimite(rs.getDate("fecha_limite"));
        c.setFechaPuestaProduccion(rs.getDate("fecha_puesta_produccion"));
        c.setFechaDevolucion(rs.getDate("fecha_devolucion"));
        c.setAnalisisDescripcion(rs.getString("analisis_descripcion"));
        c.setArgumentoRechazo(rs.getString("argumento_rechazo"));
        c.setObservacionesRechazo(rs.getString("observaciones_rechazo"));
        c.setCreatedAt(rs.getTimestamp("created_at"));

        Long deptoId = rs.getLong("departamento_id");
        if (!rs.wasNull()) c.setDepartamento(deptoDAO.buscarPorId(deptoId));

        Long solicitanteId = rs.getLong("solicitante_id");
        if (!rs.wasNull()) c.setSolicitante(usuarioDAO.buscarPorId(solicitanteId));

        Long jefeId = rs.getLong("jefe_desarrollo_id");
        if (!rs.wasNull()) c.setJefeDesarrollo(usuarioDAO.buscarPorId(jefeId));

        Long progId = rs.getLong("programador_id");
        if (!rs.wasNull()) c.setProgramador(usuarioDAO.buscarPorId(progId));

        Long probId = rs.getLong("probador_id");
        if (!rs.wasNull()) c.setProbador(usuarioDAO.buscarPorId(probId));

        return c;
    }
}