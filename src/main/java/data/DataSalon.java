package data;

import entities.Salon;
import java.sql.*;
import java.util.LinkedList;

public class DataSalon {

    /**
     * Devuelve una lista con todos los salones.
     * @return LinkedList<Salon> con todos los registros.
     */
    public LinkedList<Salon> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Salon> salones = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM salon");

            if (rs != null) {
                while (rs.next()) {
                    Salon s = new Salon();
                    s.setId(rs.getInt("id"));
                    s.setNombre(rs.getString("nombre"));
                    s.setCapacidad(rs.getInt("capacidad"));
                    s.setDescripcion(rs.getString("descripcion"));

                    // Imagen puede ser null
                    s.setImagen(rs.getBytes("imagen"));

                    salones.add(s);
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

        return salones;
    }

    /**
     * Devuelve un salón por su ID.
     * @param id ID del salón.
     * @return Salon o null si no se encuentra.
     */
    public Salon getById(int id) {
        Salon s = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM salon WHERE id = ?"
            );
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                s = new Salon();
                s.setId(rs.getInt("id"));
                s.setNombre(rs.getString("nombre"));
                s.setCapacidad(rs.getInt("capacidad"));
                s.setDescripcion(rs.getString("descripcion"));
                s.setImagen(rs.getBytes("imagen")); // puede ser null
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

        return s;
    }

    /**
     * Agrega un nuevo salón a la base de datos.
     * @param s Objeto Salon con los datos a insertar.
     */
    public void add(Salon s) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO salon (nombre, capacidad, descripcion, imagen) VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, s.getNombre());
            stmt.setInt(2, s.getCapacidad());
            stmt.setString(3, s.getDescripcion());

            if (s.getImagen() != null) {
                stmt.setBytes(4, s.getImagen());
            } else {
                stmt.setNull(4, Types.BLOB);
            }

            stmt.executeUpdate();

            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                s.setId(keyResultSet.getInt(1));
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
     * Actualiza los datos de un salón existente.
     * @param s Objeto Salon con ID y datos actualizados.
     */
    public void update(Salon s) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE salon SET nombre=?, capacidad=?, descripcion=?, imagen=? WHERE id=?"
            );
            stmt.setString(1, s.getNombre());
            stmt.setInt(2, s.getCapacidad());
            stmt.setString(3, s.getDescripcion());

            if (s.getImagen() != null) {
                stmt.setBytes(4, s.getImagen());
            } else {
                stmt.setNull(4, Types.BLOB);
            }

            stmt.setInt(5, s.getId());
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
     * Elimina un salón por su ID.
     * @param id ID del salón a eliminar.
     */
    public void delete(int id) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "DELETE FROM salon WHERE id = ?"
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
