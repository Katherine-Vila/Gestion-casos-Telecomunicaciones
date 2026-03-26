package sv.edu.udb.gestion.dao;

import sv.edu.udb.gestion.entity.Departamento;
import sv.edu.udb.gestion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoDAO {
    public void insertar(Departamento depto) throws SQLException {
        String sql = "INSERT INTO departamentos (codigo, nombre) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, depto.getCodigo());
            ps.setString(2, depto.getNombre());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                depto.setId(rs.getLong(1));
            }
        }
    }

    public Departamento buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM departamentos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearDepartamento(rs);
            }
            return null;
        }
    }

    public Departamento buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM departamentos WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearDepartamento(rs);
            }
            return null;
        }
    }

    public List<Departamento> listarTodos() throws SQLException {
        List<Departamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM departamentos";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearDepartamento(rs));
            }
        }
        return lista;
    }

    private Departamento mapearDepartamento(ResultSet rs) throws SQLException {
        Departamento d = new Departamento();
        d.setId(rs.getLong("id"));
        d.setCodigo(rs.getString("codigo"));
        d.setNombre(rs.getString("nombre"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        return d;
    }
}
