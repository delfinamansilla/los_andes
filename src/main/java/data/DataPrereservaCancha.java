package data;

import java.sql.*;
import entities.PrereservaCancha;

public class DataPrereservaCancha {

	public void add(PrereservaCancha p) throws Exception {
	    String sql = "INSERT INTO prereserva_cancha "
	            + "(id_cancha, id_usuario, fecha, hora_desde, hora_hasta, token, expiracion) "
	            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

	    try (Connection conn = DbConnector.getInstancia().getConn();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, p.getIdCancha());
	        stmt.setInt(2, p.getIdUsuario());
	        stmt.setDate(3, Date.valueOf(p.getFecha()));

	        if (p.getHoraDesde() != null) {
	            stmt.setTime(4, Time.valueOf(p.getHoraDesde()));
	        } else {
	            stmt.setNull(4, java.sql.Types.TIME);
	        }

	        stmt.setTime(5, Time.valueOf(p.getHoraHasta()));

	        stmt.setString(6, p.getToken());
	        stmt.setTimestamp(7, Timestamp.valueOf(p.getExpiracion()));

	        stmt.executeUpdate();
	    }
	}

	public PrereservaCancha getByToken(String token) throws Exception {
	    String sql = "SELECT * FROM prereserva_cancha WHERE token = ?";
	    PrereservaCancha p = null;

	    try (Connection conn = DbConnector.getInstancia().getConn();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, token);
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            p = new PrereservaCancha();
	            p.setId(rs.getInt("id"));
	            p.setIdCancha(rs.getInt("id_cancha"));
	            p.setIdUsuario(rs.getInt("id_usuario"));
	            p.setFecha(rs.getDate("fecha").toLocalDate());

	            Time hd = rs.getTime("hora_desde");
	            if (hd != null) {
	                p.setHoraDesde(hd.toLocalTime());
	            }

	            p.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
	            p.setToken(rs.getString("token"));
	            p.setExpiracion(rs.getTimestamp("expiracion").toLocalDateTime());
	        }
	    }

	    return p;
	}

	public void deleteByToken(String token) throws Exception {
	    String sql = "DELETE FROM prereserva_cancha WHERE token = ?";

	    try (Connection conn = DbConnector.getInstancia().getConn();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, token);
	        stmt.executeUpdate();
	    }
	}

}
