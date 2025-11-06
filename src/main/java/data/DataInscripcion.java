package data;

import entities.Inscripcion;

import java.util.Map;
import java.util.HashMap;
import java.sql.Time;
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

    public LinkedList<Inscripcion> getByUsuario(int id_usuario) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Inscripcion> inscripciones = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn()
                    .prepareStatement("SELECT * FROM inscripcion WHERE id_usuario = ?");
            stmt.setInt(1, id_usuario);
            rs = stmt.executeQuery();

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

    public LinkedList<Map<String, Object>> getInscripcionesConDetalles(int idUsuario) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Map<String, Object>> inscripciones = new LinkedList<>();

        try {
            String sql = 
                "SELECT " +
                "    i.id as inscripcion_id, " +
                "    i.fecha_inscripcion, " +
                "    a.id as actividad_id, " +
                "    a.nombre as actividad_nombre, " +
                "    a.descripcion as actividad_descripcion, " +
                "    p.nombre_completo as profesor_nombre, " +
                "    c.descripcion as cancha_descripcion, " +
                "    h.dia, " +
                "    h.hora_desde, " +
                "    h.hora_hasta " +
                "FROM inscripcion i " +
                "INNER JOIN actividad a ON i.id_actividad = a.id " +
                "LEFT JOIN profesor p ON a.id_profesor = p.id " +
                "LEFT JOIN cancha c ON a.id_cancha = c.id " +
                "LEFT JOIN horario h ON h.id_actividad = a.id " +  
                "WHERE i.id_usuario = ? " +
                "ORDER BY i.fecha_inscripcion DESC";

            stmt = DbConnector.getInstancia().getConn().prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Map<String, Object> inscripcion = new HashMap<>();
                inscripcion.put("inscripcion_id", rs.getInt("inscripcion_id"));
                inscripcion.put("fecha_inscripcion", rs.getDate("fecha_inscripcion").toLocalDate().toString());
                inscripcion.put("actividad_id", rs.getInt("actividad_id"));
                inscripcion.put("actividad_nombre", rs.getString("actividad_nombre"));
                inscripcion.put("actividad_descripcion", rs.getString("actividad_descripcion"));
                inscripcion.put("profesor_nombre", rs.getString("profesor_nombre"));
                inscripcion.put("cancha_descripcion", rs.getString("cancha_descripcion"));
                inscripcion.put("dia", rs.getString("dia"));
                
                Time horaDesde = rs.getTime("hora_desde");
                Time horaHasta = rs.getTime("hora_hasta");
                inscripcion.put("hora_desde", horaDesde != null ? horaDesde.toLocalTime().toString() : null);
                inscripcion.put("hora_hasta", horaHasta != null ? horaHasta.toLocalTime().toString() : null);
                
                inscripciones.add(inscripcion);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener inscripciones con detalles: " + e.getMessage());
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
    
    public LinkedList<Inscripcion> getByActividad(int idActividad) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Inscripcion> inscripciones = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM inscripcion WHERE id_actividad = ?"
            );
            stmt.setInt(1, idActividad);
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Inscripcion i = new Inscripcion();
                i.setIdInscripcion(rs.getInt("id"));
                i.setFechaInscripcion(rs.getDate("fecha_inscripcion").toLocalDate());
                i.setIdUsuario(rs.getInt("id_usuario"));
                i.setIdActividad(rs.getInt("id_actividad"));
                inscripciones.add(i);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener inscripciones por actividad: " + e.getMessage());
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

}