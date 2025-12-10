package data;

import entities.Horario;
import java.sql.*;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class DataHorario {

    public LinkedList<Horario> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Horario> horarios = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM horario");

            while (rs.next()) {
                Horario h = new Horario();
                h.setId(rs.getInt("id"));
                h.setDia(rs.getString("dia"));
                h.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                h.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                h.setIdActividad(rs.getInt("id_actividad"));

                
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

    public List<Horario> getByActividad(int idActividad) throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM horario WHERE id_actividad = ?"
            );
            stmt.setInt(1, idActividad);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Horario h = new Horario();
                h.setId(rs.getInt("id"));
                h.setDia(rs.getString("dia"));
                h.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                h.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                h.setIdActividad(rs.getInt("id_actividad"));
                horarios.add(h);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            DbConnector.getInstancia().releaseConn();
        }

        return horarios;
    }

    public Horario getById(int id) {
        Horario h = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM horario WHERE id = ?"
            );
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                h = new Horario();
                h.setId(rs.getInt("id"));
                h.setDia(rs.getString("dia"));
                h.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                h.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                h.setIdActividad(rs.getInt("id_actividad"));
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

        return h;
    }


    public void add(Horario h) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO horario (dia, hora_desde, hora_hasta, id_actividad) VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, h.getDia());
            stmt.setTime(2, Time.valueOf(h.getHoraDesde()));
            stmt.setTime(3, Time.valueOf(h.getHoraHasta()));
            stmt.setInt(4, h.getIdActividad());

            stmt.executeUpdate();

            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet.next()) {
                h.setId(keyResultSet.getInt(1));
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

    public void update(Horario h) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE horario SET dia=?, hora_desde=?, hora_hasta=?, id_actividad=? WHERE id=?"
            );
            stmt.setString(1, h.getDia());
            stmt.setTime(2, Time.valueOf(h.getHoraDesde()));
            stmt.setTime(3, Time.valueOf(h.getHoraHasta()));
            stmt.setInt(4, h.getIdActividad());
            stmt.setInt(5, h.getId());

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
                "DELETE FROM horario WHERE id = ?"
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
    
    public List<String> verificarConflictos(Horario h, int idProfesor, int idCancha) throws SQLException {
        List<String> conflictos = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT a.id_profesor, a.id_cancha, h.dia, h.hora_desde, h.hora_hasta, act.nombre AS actividad_nombre " +
                "FROM horario h " +
                "JOIN actividad act ON h.id_actividad = act.id " +
                "JOIN actividad a ON a.id = h.id_actividad " +
                "WHERE h.dia = ? AND h.id_actividad <> ?"
            );

            stmt.setString(1, h.getDia());
            stmt.setInt(2, h.getIdActividad());
            rs = stmt.executeQuery();

            while (rs.next()) {
                LocalTime hd = rs.getTime("hora_desde").toLocalTime();
                LocalTime hh = rs.getTime("hora_hasta").toLocalTime();
                LocalTime nuevoDesde = h.getHoraDesde();
                LocalTime nuevoHasta = h.getHoraHasta();

                boolean seSuperpone =
                    nuevoDesde.isBefore(hh) &&
                    nuevoHasta.isAfter(hd);

                if (seSuperpone) {


                    if (rs.getInt("id_profesor") == idProfesor) {
                        conflictos.add("El profesor ya dicta la actividad " + rs.getString("actividad_nombre") +
                                       " en ese horario.");
                    }


                    if (rs.getInt("id_cancha") == idCancha) {
                        conflictos.add("La cancha está ocupada por la actividad " +
                                       rs.getString("actividad_nombre"));
                    }
                }
            }

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            DbConnector.getInstancia().releaseConn();
        }

        return conflictos;
    }
    
    public List<Horario> getOcupadosProfesor(int idProfesor) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Horario> horarios = new ArrayList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT h.* FROM horario h INNER JOIN actividad a ON h.id_actividad = a.id "
                + "WHERE a.id_profesor = ?");
            stmt.setInt(1, idProfesor);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Horario h = new Horario();
                h.setId(rs.getInt("id"));
                h.setDia(rs.getString("dia"));
                h.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                h.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                h.setIdActividad(rs.getInt("id_actividad"));
                horarios.add(h);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            DbConnector.getInstancia().releaseConn();
        }
        return horarios;
    }

    
    public List<Horario> getOcupadosCancha(int idCancha) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Horario> horarios = new ArrayList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT h.* FROM horario h INNER JOIN actividad a ON h.id_actividad = a.id "
                + "WHERE a.id_cancha = ?");
            stmt.setInt(1, idCancha);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Horario h = new Horario();
                h.setId(rs.getInt("id"));
                h.setDia(rs.getString("dia"));
                h.setHoraDesde(rs.getTime("hora_desde").toLocalTime());
                h.setHoraHasta(rs.getTime("hora_hasta").toLocalTime());
                h.setIdActividad(rs.getInt("id_actividad"));
                horarios.add(h);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            DbConnector.getInstancia().releaseConn();
        }
        return horarios;
    }


}
