package data;

import entities.Cuota;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class DataCuota {

    public LinkedList<Cuota> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Cuota> cuotas = new LinkedList<>();
        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT id, nro_cuota, fecha_cuota, fecha_vencimiento FROM cuota");
            while (rs != null && rs.next()) {
                Cuota c = new Cuota();
                c.setId(rs.getInt("id"));
                c.setNro_cuota(rs.getInt("nro_cuota"));
                c.setFecha_cuota(rs.getObject("fecha_cuota", LocalDate.class));
                c.setFecha_vencimiento(rs.getObject("fecha_vencimiento", LocalDate.class));
                cuotas.add(c);
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
        return cuotas;
    }

    public Cuota getById(int id) {
        Cuota c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement("SELECT id, nro_cuota, fecha_cuota, fecha_vencimiento FROM cuota WHERE id=?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                c = new Cuota();
                c.setId(rs.getInt("id"));
                c.setNro_cuota(rs.getInt("nro_cuota"));
                c.setFecha_cuota(rs.getObject("fecha_cuota", LocalDate.class));
                c.setFecha_vencimiento(rs.getObject("fecha_vencimiento", LocalDate.class));
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
    
    public void add(Cuota c) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO cuota(nro_cuota, fecha_cuota, fecha_vencimiento) VALUES(?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setInt(1, c.getNro_cuota());
            stmt.setObject(2, c.getFecha_cuota());
            stmt.setObject(3, c.getFecha_vencimiento());
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

    public void update(Cuota c) {
        PreparedStatement stmt = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE cuota SET nro_cuota=?, fecha_cuota=?, fecha_vencimiento=? WHERE id=?"
            );
            stmt.setInt(1, c.getNro_cuota());
            stmt.setObject(2, c.getFecha_cuota());
            stmt.setObject(3, c.getFecha_vencimiento());
            stmt.setInt(4, c.getId());
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
            stmt = DbConnector.getInstancia().getConn().prepareStatement("DELETE FROM cuota WHERE id=?");
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