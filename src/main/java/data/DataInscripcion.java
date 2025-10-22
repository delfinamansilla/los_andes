package data;

import entities.Inscripcion;
import java.sql.*;
import java.util.LinkedList;

public class DataInscripcion {

    public LinkedList<Inscripcion> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Inscripcion> inscripciones = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM inscripcion");

            if (rs != null) {
                while (rs.next()) {
                    Inscripcion i = new Inscripcion();
                    i.setIdInscripcion(rs.getInt("id"));
                    i.setFechaInscripcion(rs.getDate("fecha_inscripcion").toLocalDate());
                    i.setIdUsuario(rs.getInt("id_usuario"));
                    i.setIdActividad(rs.getInt("id_actividad"));

                    inscripciones.add(i);
                }
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

        return inscripciones;
    }

    public Inscripcion getOne(int id) {
        Inscripcion i = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM inscripcion WHERE id = ?"
            );
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                i = new Inscripcion();
                i.setIdInscripcion(rs.getInt("id"));
                i.setFechaInscripcion(rs.getDate("fecha_inscripcion").toLocalDate());
                i.setIdUsuario(rs.getInt("id_usuario"));
                i.setIdActividad(rs.getInt("id_actividad"));
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

        return i;
    }

    public void add(Inscripcion i) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            Connection conn = DbConnector.getInstancia().getConn();

            // 🔍 Verificar que exista el usuario
            PreparedStatement checkUsuario = conn.prepareStatement(
                "SELECT id FROM usuario WHERE id = ?"
            );
            checkUsuario.setInt(1, i.getIdUsuario());
            ResultSet rsUsuario = checkUsuario.executeQuery();
            if (!rsUsuario.next()) {
                throw new SQLException("El usuario con ID " + i.getIdUsuario() + " no existe.");
            }

            // 🔍 Verificar que exista la actividad
            PreparedStatement checkActividad = conn.prepareStatement(
                "SELECT id FROM actividad WHERE id = ?"
            );
            checkActividad.setInt(1, i.getIdActividad());
            ResultSet rsActividad = checkActividad.executeQuery();
            if (!rsActividad.next()) {
                throw new SQLException("La actividad con ID " + i.getIdActividad() + " no existe.");
            }

            // ✅ Insertar la inscripción
            stmt = conn.prepareStatement(
                "INSERT INTO inscripcion (fecha_inscripcion, id_usuario, id_actividad) VALUES (?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setDate(1, Date.valueOf(i.getFechaInscripcion()));
            stmt.setInt(2, i.getIdUsuario());
            stmt.setInt(3, i.getIdActividad());

            stmt.executeUpdate();

            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                i.setIdInscripcion(keyResultSet.getInt(1));
            }

        } catch (SQLException e) {
            System.err.println("Error al agregar la inscripción: " + e.getMessage());
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

    public void update(Inscripcion i) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE inscripcion SET fecha_inscripcion=?, id_usuario=?, id_actividad=? WHERE id=?"
            );
            stmt.setDate(1, Date.valueOf(i.getFechaInscripcion()));
            stmt.setInt(2, i.getIdUsuario());
            stmt.setInt(3, i.getIdActividad());
            stmt.setInt(4, i.getIdInscripcion());

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
                "DELETE FROM inscripcion WHERE id = ?"
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
}

