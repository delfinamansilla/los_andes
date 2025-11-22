package data;

import java.sql.*;
import entities.RecuperacionPass;

public class DataRecuperacionPass {

    public void add(RecuperacionPass r) {
        String sql = "INSERT INTO recuperacion_pass (id_usuario, token, expiracion) VALUES (?, ?, ?)";
        try (Connection conn = DbConnector.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, r.getIdUsuario());
            stmt.setString(2, r.getToken());
            stmt.setTimestamp(3, Timestamp.valueOf(r.getExpiracion()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RecuperacionPass getByToken(String token) {
        String sql = "SELECT * FROM recuperacion_pass WHERE token = ?";
        RecuperacionPass r = null;
        try (Connection conn = DbConnector.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                r = new RecuperacionPass();
                r.setId(rs.getInt("id"));
                r.setIdUsuario(rs.getInt("id_usuario"));
                r.setToken(rs.getString("token"));
                r.setExpiracion(rs.getTimestamp("expiracion").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    public void deleteByToken(String token) {
        String sql = "DELETE FROM recuperacion_pass WHERE token = ?";
        try (Connection conn = DbConnector.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}