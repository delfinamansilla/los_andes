package data;

import entities.Alquiler_salon;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class DataAlquiler_salon {

    /**
     * Devuelve todos los alquileres de la base de datos.
     */
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
                a.setHoraDesde(rs.getString("hora_desde"));
                a.setHoraHasta(rs.getString("hora_hasta"));
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

    /**
     * Devuelve un alquiler por su ID.
     */
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
                a.setHoraDesde(rs.getString("hora_desde"));
                a.setHoraHasta(rs.getString("hora_hasta"));
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

    /**
     * Inserta un nuevo alquiler en la base de datos.
     */
    public void add(Alquiler_salon a) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO alquiler_salon (fecha, hora_desde, hora_hasta, id_salon, id_usuario) VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );

            stmt.setDate(1, Date.valueOf(a.getFecha()));
            stmt.setString(2, a.getHoraDesde());
            stmt.setString(3, a.getHoraHasta());
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

    /**
     * Actualiza un alquiler existente.
     */
    public void update(Alquiler_salon a) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE alquiler_salon SET fecha=?, hora_desde=?, hora_hasta=?, id_salon=?, id_usuario=? WHERE id=?"
            );

            stmt.setDate(1, Date.valueOf(a.getFecha()));
            stmt.setString(2, a.getHoraDesde());
            stmt.setString(3, a.getHoraHasta());
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

    /**
     * Elimina un alquiler por su ID.
     */
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

    /**
     * Devuelve todos los alquileres de un salón específico.
     */
    public LinkedList<Alquiler_salon> getBySalon(int idSalon) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Alquiler_salon> alquileres = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM alquiler_salon WHERE id_salon = ?"
            );
            stmt.setInt(1, idSalon);
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Alquiler_salon a = new Alquiler_salon();
                a.setId(rs.getInt("id"));
                a.setFecha(rs.getDate("fecha").toLocalDate());
                a.setHoraDesde(rs.getString("hora_desde"));
                a.setHoraHasta(rs.getString("hora_hasta"));
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
