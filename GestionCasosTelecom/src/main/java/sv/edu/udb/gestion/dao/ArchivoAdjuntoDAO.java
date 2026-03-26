package sv.edu.udb.gestion.dao;

import sv.edu.udb.gestion.entity.ArchivoAdjunto;
import sv.edu.udb.gestion.entity.Caso;
import sv.edu.udb.gestion.entity.Usuario;
import sv.edu.udb.gestion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoAdjuntoDAO {
    private CasoDAO casoDAO = new CasoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void insertar(ArchivoAdjunto archivo) throws SQLException {
        String sql = "INSERT INTO archivos (caso_id, nombre_archivo, ruta, tipo, usuario_subio_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, archivo.getCaso().getId());
            ps.setString(2, archivo.getNombreArchivo());
            ps.setString(3, archivo.getRuta());
            ps.setString(4, archivo.getTipo().name());
            ps.setLong(5, archivo.getUsuarioSubio().getId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                archivo.setId(rs.getLong(1));
            }
        }
    }

    public List<ArchivoAdjunto> listarPorCaso(Long casoId) throws SQLException {
        List<ArchivoAdjunto> lista = new ArrayList<>();
        String sql = "SELECT * FROM archivos WHERE caso_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, casoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearArchivo(rs));
            }
        }
        return lista;
    }

    private ArchivoAdjunto mapearArchivo(ResultSet rs) throws SQLException {
        ArchivoAdjunto a = new ArchivoAdjunto();
        a.setId(rs.getLong("id"));
        a.setNombreArchivo(rs.getString("nombre_archivo"));
        a.setRuta(rs.getString("ruta"));
        a.setTipo(ArchivoAdjunto.TipoArchivo.valueOf(rs.getString("tipo")));
        a.setFechaSubida(rs.getTimestamp("fecha_subida"));

        Long casoId = rs.getLong("caso_id");
        if (!rs.wasNull()) {
            Caso c = casoDAO.buscarPorId(casoId);
            a.setCaso(c);
        }

        Long usuarioId = rs.getLong("usuario_subio_id");
        if (!rs.wasNull()) {
            Usuario u = usuarioDAO.buscarPorId(usuarioId);
            a.setUsuarioSubio(u);
        }

        return a;
    }
}
