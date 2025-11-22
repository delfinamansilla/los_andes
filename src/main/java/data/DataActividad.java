package data;

import entities.Actividad;
import entities.Horario;

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
    
    public LinkedList<Map<String, Object>> getActividadesConDetalles(int idUsuario) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Map<String, Object>> actividades = new LinkedList<>();

        // --- SQL CORREGIDO Y MÁS LEGIBLE ---
        String sql = 
            "SELECT " +
            "    a.*, " + // a.* trae todas las columnas de la tabla actividad
            "    p.nombre_completo as profesor_nombre, " +
            "    c.descripcion as cancha_descripcion, " +
            "    h.dia, " +
            "    h.hora_desde, " +
            "    h.hora_hasta, " +
            "    (a.cupo - (SELECT COUNT(*) FROM inscripcion WHERE id_actividad = a.id)) as cupo_restante, " +
            "    EXISTS ( " +
            "        SELECT 1 " +
            "        FROM inscripcion " +
            "        WHERE id_actividad = a.id AND id_usuario = ? " +
            "    ) as ya_inscripto " + // ¡OJO! Faltaba un espacio aquí
            "FROM " +
            "    actividad a " +
            "LEFT JOIN " +
            "    profesor p ON a.id_profesor = p.id " +
            "LEFT JOIN " +
            "    cancha c ON a.id_cancha = c.id " +
            "LEFT JOIN " +
            "    horario h ON a.id = h.id_actividad";

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(sql);
            
            // --- 1. ERROR CRÍTICO CORREGIDO: Faltaba pasar el parámetro ---
            // Le decimos a la consulta qué valor debe reemplazar en el '?'
            stmt.setInt(1, idUsuario); 
            
            rs = stmt.executeQuery();
            
            while (rs != null && rs.next()) {
                Map<String, Object> actividad = new HashMap<>();
                
                // --- 2. MAPEADO CORREGIDO Y COMPLETO ---
                // Usamos los nombres de columna directamente de la tabla 'actividad' (gracias a 'a.*')
                actividad.put("id", rs.getInt("id")); 
                actividad.put("nombre", rs.getString("nombre"));
                actividad.put("descripcion", rs.getString("descripcion"));
                actividad.put("cupo", rs.getInt("cupo"));
                actividad.put("inscripcion_desde", rs.getDate("inscripcion_desde").toLocalDate().toString());
                actividad.put("inscripcion_hasta", rs.getDate("inscripcion_hasta").toLocalDate().toString());
                
                // Campos de las tablas unidas (JOINs)
                actividad.put("profesor_nombre", rs.getString("profesor_nombre"));
                actividad.put("cancha_descripcion", rs.getString("cancha_descripcion"));
                actividad.put("dia", rs.getString("dia"));
                
                Time horaDesde = rs.getTime("hora_desde");
                Time horaHasta = rs.getTime("hora_hasta");
                actividad.put("hora_desde", horaDesde != null ? horaDesde.toLocalTime().toString() : null);
                actividad.put("hora_hasta", horaHasta != null ? horaHasta.toLocalTime().toString() : null);
                
                // --- 3. AÑADIMOS LOS NUEVOS CAMPOS CALCULADOS ---
                actividad.put("cupo_restante", rs.getInt("cupo_restante"));
                actividad.put("yaInscripto", rs.getBoolean("ya_inscripto"));
                
                actividades.add(actividad);
            }

        } catch (SQLException e) {
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
    
    
    public LinkedList<Horario> getHorariosByCanchaAndDia(int idCancha, String dia) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Horario> horarios = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT h.* FROM horario h " +
                "INNER JOIN actividad a ON h.id_actividad = a.id " +
                "WHERE a.id_cancha = ? AND h.dia = ?"
            );

            stmt.setInt(1, idCancha);
            stmt.setString(2, dia);

            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Horario h = new Horario();

                h.setId(rs.getInt("id"));
                h.setIdActividad(rs.getInt("id_actividad"));
                h.setDia(rs.getString("dia"));
                h.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                h.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());

                horarios.add(h);
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

        return horarios;
    }

    
}

