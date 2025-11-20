package data;

import java.sql.*;
import entities.PreReserva;

public class DataPreReserva {

    public void add(PreReserva p) throws Exception {
        String sql = "INSERT INTO reserva_pendiente "
                + "(id_salon, id_usuario, fecha, hora_desde, hora_hasta, token, expiracion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbConnector.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, p.getIdSalon());
            stmt.setInt(2, p.getIdUsuario());
            stmt.setDate(3, Date.valueOf(p.getFecha()));
            stmt.setTime(4, Time.valueOf(p.getHoraDesde()));
            stmt.setTime(5, Time.valueOf(p.getHoraHasta()));
            stmt.setString(6, p.getToken());
            stmt.setTimestamp(7, Timestamp.valueOf(p.getExpiracion()));

            stmt.executeUpdate();
        }
    }

    public PreReserva getByToken(String token) throws Exception {
        String sql = "SELECT * FROM reserva_pendiente WHERE token = ?";
        PreReserva p = null;

        try (Connection conn = DbConnector.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                p = new PreReserva();
                p.setId(rs.getInt("id"));
                p.setIdSalon(rs.getInt("id_salon"));
                p.setIdUsuario(rs.getInt("id_usuario"));
                p.setFecha(rs.getDate("fecha").toLocalDate());
                p.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                p.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                p.setToken(rs.getString("token"));
                p.setExpiracion(rs.getTimestamp("expiracion").toLocalDateTime());
            }
        }

        return p;
    }

    public void deleteByToken(String token) throws Exception {
        String sql = "DELETE FROM reserva_pendiente WHERE token = ?";

        try (Connection conn = DbConnector.getInstancia().getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.executeUpdate();
        }
    }
}
