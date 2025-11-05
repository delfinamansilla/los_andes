package data;

import entities.Actividad;

import java.util.Map;
import java.util.HashMap;

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
    
    public LinkedList<Map<String, Object>> getActividadesConDetalles() {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Map<String, Object>> actividades = new LinkedList<>();

        try {
            String sql = 
                "SELECT " +
                "    a.id as actividad_id, " +
                "    a.nombre as actividad_nombre, " +
                "    a.descripcion as actividad_descripcion, " +
                "    a.cupo, " +
                "    a.inscripcion_desde, " +
                "    a.inscripcion_hasta, " +
                "    u.nombre_completo as profesor_nombre, " +
                "    c.descripcion as cancha_descripcion, " +
                "    h.dia, " +
                "    h.hora_desde, " +
                "    h.hora_hasta " +
                "FROM actividad a " +
                "LEFT JOIN usuario u ON a.id_profesor = u.id " +
                "LEFT JOIN cancha c ON a.id_cancha = c.id " +
                "LEFT JOIN horario h ON h.id_actividad = a.id " +
                "ORDER BY a.nombre";

            stmt = DbConnector.getInstancia().getConn().prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs != null && rs.next()) {
                Map<String, Object> actividad = new HashMap<>();
                actividad.put("id", rs.getInt("actividad_id"));
                actividad.put("nombre", rs.getString("actividad_nombre"));
                actividad.put("descripcion", rs.getString("actividad_descripcion"));
                actividad.put("cupo", rs.getInt("cupo"));
                actividad.put("inscripcion_desde", rs.getDate("inscripcion_desde").toLocalDate().toString());
                actividad.put("inscripcion_hasta", rs.getDate("inscripcion_hasta").toLocalDate().toString());
                actividad.put("profesor_nombre", rs.getString("profesor_nombre"));
                actividad.put("cancha_descripcion", rs.getString("cancha_descripcion"));
                actividad.put("dia", rs.getString("dia"));
                
                Time horaDesde = rs.getTime("hora_desde");
                Time horaHasta = rs.getTime("hora_hasta");
                actividad.put("hora_desde", horaDesde != null ? horaDesde.toLocalTime().toString() : null);
                actividad.put("hora_hasta", horaHasta != null ? horaHasta.toLocalTime().toString() : null);
                
                actividades.add(actividad);
            }

        }catch (SQLException e) {
            System.err.println("Error al obtener actividades con detalles: " + e.getMessage());
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

