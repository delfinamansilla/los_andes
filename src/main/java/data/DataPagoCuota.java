package data;

import entities.PagoCuota;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class DataPagoCuota {

    public LinkedList<PagoCuota> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<PagoCuota> pagos = new LinkedList<>();
        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT id, fecha_pago, id_usuario, id_cuota FROM pago_cuota");
            while (rs != null && rs.next()) {
                PagoCuota pc = new PagoCuota();
                pc.setId(rs.getInt("id"));
                pc.setFecha_pago(rs.getObject("fecha_pago", LocalDate.class));
                pc.setId_usuario(rs.getInt("id_usuario"));
                pc.setId_cuota(rs.getInt("id_cuota"));
                pagos.add(pc);
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
        return pagos;
    }
    
    public void add(PagoCuota pc) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO pago_cuota(fecha_pago, id_usuario, id_cuota) VALUES(?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setObject(1, pc.getFecha_pago());
            stmt.setInt(2, pc.getId_usuario());
            stmt.setInt(3, pc.getId_cuota());
            stmt.executeUpdate();
            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                pc.setId(keyResultSet.getInt(1));
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

    public void delete(int id) {
        PreparedStatement stmt = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement("DELETE FROM pago_cuota WHERE id=?");
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