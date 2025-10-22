package data;

import entities.Monto_cuota;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class DataMonto_cuota {

    public LinkedList<Monto_cuota> getAll() {
        LinkedList<Monto_cuota> montos = new LinkedList<>();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM monto_cuota");

            while (rs != null && rs.next()) {
                Monto_cuota mc = new Monto_cuota();
                mc.setMonto(rs.getDouble("monto"));
                mc.setId_cuota(rs.getInt("id_cuota"));
                montos.add(mc);
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

        return montos;
    }

   
    public Monto_cuota getByFecha(LocalDate fecha) {
        Monto_cuota mc = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM monto_cuota WHERE fecha = ?"
            );
            stmt.setDate(1, Date.valueOf(fecha));
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                mc = new Monto_cuota();
                mc.setMonto(rs.getDouble("monto"));
                mc.setId_cuota(rs.getInt("id_cuota"));
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

        return mc;
    }

    public void add(Monto_cuota mc) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO monto_cuota (fecha, monto, id_cuota) VALUES (?, ?, ?)"
            );
            stmt.setDate(1, Date.valueOf(mc.getFecha()));
            stmt.setDouble(2, mc.getMonto());
            stmt.setInt(3, mc.getId_cuota());

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

    public void update(Monto_cuota mc) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE monto_cuota SET monto=?, id_cuota=? WHERE fecha=?"
            );
            stmt.setDouble(1, mc.getMonto());
            stmt.setInt(2, mc.getId_cuota());
            stmt.setDate(3, Date.valueOf(mc.getFecha()));

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

    public void delete(LocalDate fecha) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "DELETE FROM monto_cuota WHERE fecha=?"
            );
            stmt.setDate(1, Date.valueOf(fecha));

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
