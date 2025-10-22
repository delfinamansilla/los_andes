package data;

import entities.Cancha;
import entities.Partido;
import entities.Alquiler_cancha;
import entities.Actividad;
import java.sql.*;
import java.util.LinkedList;

public class DataCancha {

    public LinkedList<Cancha> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Cancha> canchas = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM cancha");

            while (rs != null && rs.next()) {
                Cancha c = new Cancha();
                c.setId(rs.getInt("id"));
                c.setNro_cancha(rs.getInt("nro_cancha"));
                c.setUbicacion(rs.getString("ubicacion"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setTamanio(rs.getFloat("tamanio"));
                c.setEstado(rs.getBoolean("estado"));
                canchas.add(c);
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

        return canchas;
    }

    public Cancha getOne(int id) {
        Cancha c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM cancha WHERE id = ?"
            );
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                c = new Cancha();
                c.setId(rs.getInt("id"));
                c.setNro_cancha(rs.getInt("nro_cancha"));
                c.setUbicacion(rs.getString("ubicacion"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setTamanio(rs.getFloat("tamanio"));
                c.setEstado(rs.getBoolean("estado"));
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

        return c;
    }

    public void add(Cancha c) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO cancha (nro_cancha, ubicacion, descripcion, tamanio, estado) VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setInt(1, c.getNro_cancha());
            stmt.setString(2, c.getUbicacion());
            stmt.setString(3, c.getDescripcion());
            stmt.setFloat(4, c.getTamanio());
            stmt.setBoolean(5, c.isEstado());

            stmt.executeUpdate();

            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                c.setId(keyResultSet.getInt(1));
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

    public void update(Cancha c) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE cancha SET nro_cancha=?, ubicacion=?, descripcion=?, tamanio=?, estado=? WHERE id=?"
            );
            stmt.setInt(1, c.getNro_cancha());
            stmt.setString(2, c.getUbicacion());
            stmt.setString(3, c.getDescripcion());
            stmt.setFloat(4, c.getTamanio());
            stmt.setBoolean(5, c.isEstado());
            stmt.setInt(6, c.getId());
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
                "DELETE FROM cancha WHERE id = ?"
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

    
    
    public LinkedList<Partido> getPartidosByCancha(int idCancha) {
        LinkedList<Partido> partidos = new LinkedList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM partido WHERE id_cancha = ?"
            );
            stmt.setInt(1, idCancha);
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Partido p = new Partido();
                p.setId(rs.getInt("id"));
                partidos.add(p);
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

        return partidos;
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

    public LinkedList<Actividad> getActividadesByCancha(int idCancha) {
        LinkedList<Actividad> actividades = new LinkedList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM actividad WHERE id_cancha = ?"
            );
            stmt.setInt(1, idCancha);
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Actividad a = new Actividad();
                a.setIdActividad(rs.getInt("id"));
                actividades.add(a);
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

        return actividades;
    }
}
