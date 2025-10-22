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
                a.setHoraDesde(rs.getString("hora_desde"));
                a.setHoraHasta(rs.getString("hora_hasta"));
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
                a.setHoraDesde(rs.getString("hora_desde"));
                a.setHoraHasta(rs.getString("hora_hasta"));
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
            stmt.setString(2, a.getHoraDesde());
            stmt.setString(3, a.getHoraHasta());
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
            stmt.setString(2, a.getHoraDesde());
            stmt.setString(3, a.getHoraHasta());
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
    
    public boolean isHorarioDisponible(int idCancha, LocalDate fecha, String horaDesde, String horaHasta) {
        LinkedList<Alquiler_cancha> alquileres = getAlquileresByCancha(idCancha);
        
        LocalTime nuevoDesde = LocalTime.parse(horaDesde);
        LocalTime nuevoHasta = LocalTime.parse(horaHasta);

        for (Alquiler_cancha alq : alquileres) {
            if (alq.getFechaAlquiler().equals(fecha)) {
                LocalTime existDesde = LocalTime.parse(alq.getHoraDesde());
                LocalTime existHasta = LocalTime.parse(alq.getHoraHasta());
                if (nuevoDesde.isBefore(existHasta) && nuevoHasta.isAfter(existDesde)) {
                    return false;
                }
            }
        }
        return true;
    }
    
}
