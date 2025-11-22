package data;

import entities.Alquiler_cancha;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.sql.*;

public class DataAlquiler_cancha {

  
    public LinkedList<Alquiler_cancha> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Alquiler_cancha> alquileres = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM alquiler_cancha");

            while (rs != null && rs.next()) {
                Alquiler_cancha a = new Alquiler_cancha();
                a.setId(rs.getInt("id"));
                a.setFechaAlquiler(rs.getDate("fecha_alquiler").toLocalDate());
                a.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                a.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                a.setId_cancha(rs.getInt("id_cancha"));
                a.setId_usuario(rs.getInt("id_usuario"));

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


    public Alquiler_cancha getById(int id) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Alquiler_cancha a = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM alquiler_cancha WHERE id = ?"
            );
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                a = new Alquiler_cancha();
                a.setId(rs.getInt("id"));
                a.setFechaAlquiler(rs.getDate("fecha_alquiler").toLocalDate());
                a.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                a.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                a.setId_cancha(rs.getInt("id_cancha"));
                a.setId_usuario(rs.getInt("id_usuario"));
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

    public void add(Alquiler_cancha a) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO alquiler_cancha (fecha_alquiler, hora_desde, hora_hasta, id_cancha, id_usuario) VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );

            stmt.setDate(1, Date.valueOf(a.getFechaAlquiler()));
            stmt.setTime(2, Time.valueOf(a.getHoraDesde()));
            stmt.setTime(3, Time.valueOf(a.getHoraHasta()));
            stmt.setInt(4, a.getId_cancha());
            stmt.setInt(5, a.getId_usuario());

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

    public void update(Alquiler_cancha a) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE alquiler_cancha SET fecha_alquiler=?, hora_desde=?, hora_hasta=?, id_cancha=?, id_usuario=? WHERE id=?"
            );

            stmt.setDate(1, Date.valueOf(a.getFechaAlquiler()));
            stmt.setTime(2, Time.valueOf(a.getHoraDesde()));
            stmt.setTime(3, Time.valueOf(a.getHoraHasta()));
            stmt.setInt(4, a.getId_cancha());
            stmt.setInt(5, a.getId_usuario());
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
                "DELETE FROM alquiler_cancha WHERE id = ?"
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
    
    public LinkedList<Alquiler_cancha> getAlquileresByCancha(int idCancha) {
        LinkedList<Alquiler_cancha> alquileres = new LinkedList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM alquiler_cancha WHERE id_cancha = ?"
            );
            stmt.setInt(1, idCancha);
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Alquiler_cancha a = new Alquiler_cancha();
                a.setId(rs.getInt("id"));
                a.setFechaAlquiler(rs.getDate("fecha_alquiler").toLocalDate());
                a.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                a.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                a.setId_cancha(rs.getInt("id_cancha"));
                a.setId_usuario(rs.getInt("id_usuario"));

                alquileres.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return alquileres;
    }
    
    public boolean isHorarioDisponible(int idCancha, LocalDate fecha_alquiler, LocalTime horaDesde, LocalTime horaHasta) {
        LinkedList<Alquiler_cancha> alquileres = getAlquileresByCancha(idCancha);
        
        LocalTime nuevoDesde = horaDesde;
        LocalTime nuevoHasta = horaHasta;


        for (Alquiler_cancha alq : alquileres) {
            if (alq.getFechaAlquiler().equals(fecha_alquiler)) {
            	LocalTime existDesde = alq.getHoraDesde();
                LocalTime existHasta = alq.getHoraHasta();
                if (nuevoDesde.isBefore(existHasta) && nuevoHasta.isAfter(existDesde)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public LinkedList<Alquiler_cancha> getByUsuarioFuturos(int idUsuario) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Alquiler_cancha> alquileres = new LinkedList<>();

        try {
            String sql = "SELECT * FROM alquiler_cancha " +
                         "WHERE id_usuario = ? AND fecha_alquiler >= CURDATE() " +
                         "ORDER BY fecha_alquiler ASC, hora_desde ASC";

            stmt = DbConnector.getInstancia().getConn().prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Alquiler_cancha a = new Alquiler_cancha();
                a.setId(rs.getInt("id"));
                a.setFechaAlquiler(rs.getDate("fecha_alquiler").toLocalDate());
                a.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                a.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                a.setId_cancha(rs.getInt("id_cancha"));
                a.setId_usuario(rs.getInt("id_usuario"));

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
    
    public LinkedList<Alquiler_cancha> getByCancha(int idCancha) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Alquiler_cancha> alquileres = new LinkedList<>();
        try {
            // AGREGAMOS EL ORDER BY fecha ASC, hora_desde ASC
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM alquiler_cancha WHERE id_cancha = ? ORDER BY fecha_alquiler ASC, hora_desde ASC"
            );
            stmt.setInt(1, idCancha);
            rs = stmt.executeQuery();
            while (rs != null && rs.next()) {
                Alquiler_cancha a = new Alquiler_cancha();
                a.setId(rs.getInt("id"));
                a.setFechaAlquiler(rs.getDate("fecha_alquiler").toLocalDate());
                a.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                a.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                a.setId_cancha(rs.getInt("id_cancha"));
                a.setId_usuario(rs.getInt("id_usuario"));
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
                "AND (fecha_alquiler > CURDATE() OR (fecha_alquiler = CURDATE() AND hora_hasta > CURTIME()))"
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

}
