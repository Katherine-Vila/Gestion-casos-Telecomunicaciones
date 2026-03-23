package sv.edu.udb.gestion.dao;

import sv.edu.udb.gestion.entity.Bitacora;
import sv.edu.udb.gestion.entity.Caso;
import sv.edu.udb.gestion.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAO {
    private CasoDAO casoDAO = new CasoDAO();

    public void insertar(Bitacora bitacora) throws SQLException {
        String sql = "INSERT INTO bitacoras (caso_id, fecha, descripcion, porcentaje_avance) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, bitacora.getCaso().getId());
            ps.setDate(2, new Date(bitacora.getFecha().getTime()));
            ps.setString(3, bitacora.getDescripcion());
            ps.setInt(4, bitacora.getPorcentajeAvance());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                bitacora.setId(rs.getLong(1));
            }
        }
    }

    public List<Bitacora> listarPorCaso(Long casoId) throws SQLException {
        List<Bitacora> lista = new ArrayList<>();
        String sql = "SELECT * FROM bitacoras WHERE caso_id = ? ORDER BY fecha";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, casoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearBitacora(rs));
            }
        }
        return lista;
    }

    private Bitacora mapearBitacora(ResultSet rs) throws SQLException {
        Bitacora b = new Bitacora();
        b.setId(rs.getLong("id"));
        b.setFecha(rs.getDate("fecha"));
        b.setDescripcion(rs.getString("descripcion"));
        b.setPorcentajeAvance(rs.getInt("porcentaje_avance"));
        b.setCreatedAt(rs.getTimestamp("created_at"));

        Long casoId = rs.getLong("caso_id");
        if (!rs.wasNull()) {
            Caso c = casoDAO.buscarPorId(casoId);
            b.setCaso(c);
        }
        return b;
    }
}