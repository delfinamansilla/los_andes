package data;

import entities.Salon;
import java.sql.*;
import java.util.LinkedList;

public class DataSalon {


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
                s.setImagen(rs.getBytes("imagen")); 
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
