package com.empresa.gestion.dao;

import com.empresa.gestion.entity.*;
import com.empresa.gestion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CasoDAO {
    private DepartamentoDAO deptoDAO = new DepartamentoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void insertar(Caso caso) throws SQLException {
        String sql = "INSERT INTO casos (codigo, descripcion_solicitud, fecha_solicitud, estado, porcentaje_avance, " +
                "fecha_limite, analisis_descripcion, argumento_rechazo, observaciones_rechazo, " +
                "departamento_id, solicitante_id, jefe_desarrollo_id, programador_id, probador_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, caso.getCodigo());
            ps.setString(2, caso.getDescripcionSolicitud());
            ps.setDate(3, new java.sql.Date(caso.getFechaSolicitud().getTime()));
            ps.setString(4, caso.getEstado().name());
            ps.setInt(5, caso.getPorcentajeAvance());
            ps.setDate(6, caso.getFechaLimite() != null ? new java.sql.Date(caso.getFechaLimite().getTime()) : null);
            ps.setString(7, caso.getAnalisisDescripcion());
            ps.setString(8, caso.getArgumentoRechazo());
            ps.setString(9, caso.getObservacionesRechazo());
            ps.setLong(10, caso.getDepartamento().getId());
            ps.setLong(11, caso.getSolicitante().getId());
            ps.setLong(12, caso.getJefeDesarrollo() != null ? caso.getJefeDesarrollo().getId() : null);
            ps.setLong(13, caso.getProgramador() != null ? caso.getProgramador().getId() : null);
            ps.setLong(14, caso.getProbador() != null ? caso.getProbador().getId() : null);

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
                "porcentaje_avance=?, fecha_limite=?, analisis_descripcion=?, argumento_rechazo=?, " +
                "observaciones_rechazo=?, departamento_id=?, solicitante_id=?, jefe_desarrollo_id=?, " +
                "programador_id=?, probador_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, caso.getCodigo());
            ps.setString(2, caso.getDescripcionSolicitud());
            ps.setDate(3, new java.sql.Date(caso.getFechaSolicitud().getTime()));
            ps.setString(4, caso.getEstado().name());
            ps.setInt(5, caso.getPorcentajeAvance());
            ps.setDate(6, caso.getFechaLimite() != null ? new java.sql.Date(caso.getFechaLimite().getTime()) : null);
            ps.setString(7, caso.getAnalisisDescripcion());
            ps.setString(8, caso.getArgumentoRechazo());
            ps.setString(9, caso.getObservacionesRechazo());
            ps.setLong(10, caso.getDepartamento().getId());
            ps.setLong(11, caso.getSolicitante().getId());
            ps.setLong(12, caso.getJefeDesarrollo() != null ? caso.getJefeDesarrollo().getId() : null);
            ps.setLong(13, caso.getProgramador() != null ? caso.getProgramador().getId() : null);
            ps.setLong(14, caso.getProbador() != null ? caso.getProbador().getId() : null);
            ps.setLong(15, caso.getId());

            ps.executeUpdate();
        }
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