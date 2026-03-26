package sv.edu.udb.gestion.dao;

import sv.edu.udb.gestion.entity.Departamento;
import sv.edu.udb.gestion.entity.Rol;
import sv.edu.udb.gestion.entity.Usuario;
import sv.edu.udb.gestion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private DepartamentoDAO departamentoDAO = new DepartamentoDAO();

    public void insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, email, password, rol, departamento_id, jefe_desarrollo_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPassword());
            ps.setString(4, usuario.getRol().name());

            if (usuario.getDepartamento() != null && usuario.getDepartamento().getId() != null) {
                ps.setLong(5, usuario.getDepartamento().getId());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            if (usuario.getJefeDesarrollo() != null && usuario.getJefeDesarrollo().getId() != null) {
                ps.setLong(6, usuario.getJefeDesarrollo().getId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getLong(1));
            }
        }
    }

    public Usuario buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearUsuario(rs);
            }
            return null;
        }
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearUsuario(rs);
            }
            return null;
        }
    }

    public List<Usuario> listarPorRol(Rol rol) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rol.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

    public List<Usuario> listarProgramadoresPorJefe(Long jefeDesarrolloId) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = 'PROGRAMADOR' AND jefe_desarrollo_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, jefeDesarrolloId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNombre(rs.getString("nombre"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRol(Rol.valueOf(rs.getString("rol")));
        u.setCreatedAt(rs.getTimestamp("created_at"));

        Long deptoId = rs.getLong("departamento_id");
        if (!rs.wasNull()) {
            DepartamentoDAO deptoDAO = new DepartamentoDAO();
            Departamento d = deptoDAO.buscarPorId(deptoId);
            u.setDepartamento(d);
        }

        Long jefeId = rs.getLong("jefe_desarrollo_id");
        if (!rs.wasNull()) {
            Usuario jefe = buscarPorId(jefeId);
            u.setJefeDesarrollo(jefe);
        }

        return u;
    }
}