package data;

import entities.Actividad;
import java.sql.*;
import java.util.LinkedList;

public class DataActividad {

    public LinkedList<Actividad> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Actividad> actividades = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM actividad");

            if (rs != null) {
                while (rs.next()) {
                    Actividad a = new Actividad();
                    a.setIdActividad(rs.getInt("id"));
                    a.setNombre(rs.getString("nombre"));
                    a.setDescripcion(rs.getString("descripcion"));
                    a.setCupo(rs.getInt("cupo"));
                    a.setInscripcionDesde(rs.getDate("inscripcion_desde").toLocalDate());
                    a.setInscripcionHasta(rs.getDate("inscripcion_hasta").toLocalDate());
                    a.setIdProfesor(rs.getInt("id_profesor"));
                    a.setIdCancha(rs.getInt("id_cancha"));

                    actividades.add(a);
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

        return actividades;
    }

    public Actividad getOne(int id) {
        Actividad a = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM actividad WHERE id = ?"
            );
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                a = new Actividad();
                a.setIdActividad(rs.getInt("id"));
                a.setNombre(rs.getString("nombre"));
                a.setDescripcion(rs.getString("descripcion"));
                a.setCupo(rs.getInt("cupo"));
                a.setInscripcionDesde(rs.getDate("inscripcion_desde").toLocalDate());
                a.setInscripcionHasta(rs.getDate("inscripcion_hasta").toLocalDate());
                a.setIdProfesor(rs.getInt("id_profesor"));
                a.setIdCancha(rs.getInt("id_cancha"));
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

    public void add(Actividad a) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            Connection conn = DbConnector.getInstancia().getConn();

            // 🔍 Verificar que exista el profesor
            PreparedStatement checkProfesor = conn.prepareStatement(
                "SELECT id FROM profesor WHERE id = ?"
            );
            checkProfesor.setInt(1, a.getIdProfesor());
            ResultSet rsProfesor = checkProfesor.executeQuery();
            if (!rsProfesor.next()) {
                throw new SQLException("El profesor con ID " + a.getIdProfesor() + " no existe.");
            }

            // 🔍 Verificar que exista la cancha
            PreparedStatement checkCancha = conn.prepareStatement(
                "SELECT id FROM cancha WHERE id = ?"
            );
            checkCancha.setInt(1, a.getIdCancha());
            ResultSet rsCancha = checkCancha.executeQuery();
            if (!rsCancha.next()) {
                throw new SQLException("La cancha con ID " + a.getIdCancha() + " no existe.");
            }

            // ✅ Insertar la actividad
            stmt = conn.prepareStatement(
                "INSERT INTO actividad (nombre, descripcion, cupo, inscripcion_desde, inscripcion_hasta, id_profesor, id_cancha) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, a.getNombre());
            stmt.setString(2, a.getDescripcion());
            stmt.setInt(3, a.getCupo());
            stmt.setDate(4, Date.valueOf(a.getInscripcionDesde()));
            stmt.setDate(5, Date.valueOf(a.getInscripcionHasta()));
            stmt.setInt(6, a.getIdProfesor());
            stmt.setInt(7, a.getIdCancha());

            stmt.executeUpdate();

            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                a.setIdActividad(keyResultSet.getInt(1));
            }

        } catch (SQLException e) {
            System.err.println("Error al agregar la actividad: " + e.getMessage());
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

    public void update(Actividad a) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE actividad SET nombre=?, descripcion=?, cupo=?, inscripcion_desde=?, inscripcion_hasta=?, id_profesor=?, id_cancha=? WHERE id=?"
            );
            stmt.setString(1, a.getNombre());
            stmt.setString(2, a.getDescripcion());
            stmt.setInt(3, a.getCupo());
            stmt.setDate(4, Date.valueOf(a.getInscripcionDesde()));
            stmt.setDate(5, Date.valueOf(a.getInscripcionHasta()));
            stmt.setInt(6, a.getIdProfesor());
            stmt.setInt(7, a.getIdCancha());
            stmt.setInt(8, a.getIdActividad());

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
                "DELETE FROM actividad WHERE id = ?"
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

