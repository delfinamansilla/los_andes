package data;

import entities.Alquiler_salon;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class DataAlquiler_salon {

    public LinkedList<Alquiler_salon> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Alquiler_salon> alquileres = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM alquiler_salon");

            while (rs != null && rs.next()) {
                Alquiler_salon a = new Alquiler_salon();
                a.setId(rs.getInt("id"));
                a.setFecha(rs.getDate("fecha").toLocalDate());
                a.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                a.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                a.setIdSalon(rs.getInt("id_salon"));
                a.setIdUsuario(rs.getInt("id_usuario"));

                alquileres.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return alquileres;
    }
    

    public LinkedList<Alquiler_salon> getByUsuarioFuturos(int idUsuario) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Alquiler_salon> alquileres = new LinkedList<>();

        try {

            String sql = "SELECT * FROM alquiler_salon " +
                         "WHERE id_usuario = ? AND fecha >= CURDATE() " +
                         "ORDER BY fecha ASC, hora_desde ASC";

            stmt = DbConnector.getInstancia().getConn().prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Alquiler_salon a = new Alquiler_salon();
                a.setId(rs.getInt("id"));
                a.setFecha(rs.getDate("fecha").toLocalDate());
                a.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                a.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                a.setIdSalon(rs.getInt("id_salon"));
                a.setIdUsuario(rs.getInt("id_usuario"));

                alquileres.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return alquileres;
    }

    public Alquiler_salon getById(int id) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Alquiler_salon a = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM alquiler_salon WHERE id = ?"
            );
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                a = new Alquiler_salon();
                a.setId(rs.getInt("id"));
                a.setFecha(rs.getDate("fecha").toLocalDate());
                a.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                a.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                a.setIdSalon(rs.getInt("id_salon"));
                a.setIdUsuario(rs.getInt("id_usuario"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return a;
    }


    public void add(Alquiler_salon a) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO alquiler_salon (fecha, hora_desde, hora_hasta, id_salon, id_usuario) VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );

            stmt.setDate(1, Date.valueOf(a.getFecha()));
            stmt.setTime(2, Time.valueOf(a.getHoraDesde()));
            stmt.setTime(3, Time.valueOf(a.getHoraHasta()));
            stmt.setInt(4, a.getIdSalon());
            stmt.setInt(5, a.getIdUsuario());

            stmt.executeUpdate();

            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                a.setId(keyResultSet.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (keyResultSet != null) keyResultSet.close();
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(Alquiler_salon a) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE alquiler_salon SET fecha=?, hora_desde=?, hora_hasta=?, id_salon=?, id_usuario=? WHERE id=?"
            );

            stmt.setDate(1, Date.valueOf(a.getFecha()));
            stmt.setTime(2, Time.valueOf(a.getHoraDesde()));
            stmt.setTime(3, Time.valueOf(a.getHoraHasta()));
            stmt.setInt(4, a.getIdSalon());
            stmt.setInt(5, a.getIdUsuario());
            stmt.setInt(6, a.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void delete(int id) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "DELETE FROM alquiler_salon WHERE id = ?"
            );
            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    


    public LinkedList<Alquiler_salon> getBySalon(int idSalon) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Alquiler_salon> alquileres = new LinkedList<>();
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM alquiler_salon WHERE id_salon = ? ORDER BY fecha ASC, hora_desde ASC"
            );
            stmt.setInt(1, idSalon);
            rs = stmt.executeQuery();
            while (rs != null && rs.next()) {
                Alquiler_salon a = new Alquiler_salon();
                a.setId(rs.getInt("id"));
                a.setFecha(rs.getDate("fecha").toLocalDate());
                a.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                a.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                a.setIdSalon(rs.getInt("id_salon"));
                a.setIdUsuario(rs.getInt("id_usuario"));
                alquileres.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return alquileres;
    }    
    public boolean tieneReservasActivas(int idSalon) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean tiene = false;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT COUNT(*) FROM alquiler_salon " +
                "WHERE id_salon = ? " +
                "AND (fecha > CURDATE() OR (fecha = CURDATE() AND hora_hasta > CURTIME()))"
            );

            stmt.setInt(1, idSalon);
            rs = stmt.executeQuery();

            if (rs.next()) {
                tiene = rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return tiene;
    }
    public LinkedList<Alquiler_salon> getBySalonYFecha(int idSalon, LocalDate fecha) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Alquiler_salon> alquileres = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM alquiler_salon WHERE id_salon = ? AND fecha = ?"
            );
            stmt.setInt(1, idSalon);
            stmt.setDate(2, Date.valueOf(fecha));
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Alquiler_salon a = new Alquiler_salon();
                a.setId(rs.getInt("id"));
                a.setFecha(rs.getDate("fecha").toLocalDate());
                a.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                a.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                a.setIdSalon(rs.getInt("id_salon"));
                a.setIdUsuario(rs.getInt("id_usuario"));
                alquileres.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return alquileres;
    }
}
