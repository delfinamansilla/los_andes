package data;

import entities.Profesor;
import java.sql.*;
import java.util.LinkedList;


public class DataProfesor {

 
    public LinkedList<Profesor> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Profesor> profesores = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM profesor");

            if (rs != null) {
                while (rs.next()) {
                    Profesor p = new Profesor();
                    p.setIdProfesor(rs.getInt("id"));
                    p.setNombreCompleto(rs.getString("nombre_completo"));
                    p.setTelefono(rs.getString("telefono"));
                    p.setMail(rs.getString("mail"));

                    profesores.add(p);
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

        return profesores;
    }

    
    public Profesor getOne(int id) {
        Profesor p = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM profesor WHERE id = ?"
            );
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                p = new Profesor();
                p.setIdProfesor(rs.getInt("id"));
                p.setNombreCompleto(rs.getString("nombre_completo"));
                p.setTelefono(rs.getString("telefono"));
                p.setMail(rs.getString("mail"));
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

        return p;
    }

   
    public void add(Profesor p) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO profesor (nombre_completo, telefono, mail) VALUES (?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, p.getNombreCompleto());
            stmt.setString(2, p.getTelefono());
            stmt.setString(3, p.getMail());

            stmt.executeUpdate();

            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                p.setIdProfesor(keyResultSet.getInt(1));
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

  
    public void update(Profesor p) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE profesor SET nombre_completo=?, telefono=?, mail=? WHERE id=?"
            );
            stmt.setString(1, p.getNombreCompleto());
            stmt.setString(2, p.getTelefono());
            stmt.setString(3, p.getMail());
            stmt.setInt(4, p.getIdProfesor());

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
                "DELETE FROM profesor WHERE id = ?"
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
