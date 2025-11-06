package data;

import entities.Horario;
import java.sql.*;
import java.time.LocalTime;
import java.util.LinkedList;

public class DataHorario {

    /**
     * Devuelve todos los horarios.
     * @return LinkedList<Horario> con todos los registros.
     */
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

    /**
     * Devuelve un horario por ID.
     * @param id ID del horario.
     * @return Horario o null si no existe.
     */
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

    /**
     * Agrega un nuevo horario a la base de datos.
     * @param h Horario a insertar.
     */
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

    /**
     * Actualiza un horario existente.
     * @param h Horario con ID y datos a actualizar.
     */
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

    /**
     * Elimina un horario por su ID.
     * @param id ID del horario a eliminar.
     */
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
}
