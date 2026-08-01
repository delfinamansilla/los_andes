package data;

import java.sql.*;
import java.util.LinkedList;
import entities.Configuracion;

public class DataConfiguracion {

    public LinkedList<Configuracion> getAll() {
        LinkedList<Configuracion> lista = new LinkedList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM configuracion ORDER BY id DESC");
            while (rs.next()) {
                Configuracion c = new Configuracion();
                c.setId(rs.getInt("id"));
                c.setClave(rs.getString("clave"));
                c.setValor(rs.getString("valor"));
                c.setFechaCambio(rs.getTimestamp("fecha_cambio").toLocalDateTime());
                lista.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally {
            try {
                if(rs!=null) rs.close();
                if(stmt!=null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista;
    }

    public String getValorActual(String clave) {
        String valor = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT valor FROM configuracion WHERE clave = ? ORDER BY id DESC LIMIT 1"
            );
            stmt.setString(1, clave);
            rs = stmt.executeQuery();
            if (rs.next()) { valor = rs.getString("valor"); }
        } catch (SQLException e) { e.printStackTrace(); }
        finally {
            try {
                if(rs!=null) rs.close();
                if(stmt!=null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return valor;
    }

    public void add(Configuracion c) {
        PreparedStatement stmt = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO configuracion (clave, valor) VALUES (?, ?)"
            );
            stmt.setString(1, c.getClave());
            stmt.setString(2, c.getValor());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        finally {
            try {
                if(stmt!=null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    public void delete(int id) {
        PreparedStatement stmt = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement("DELETE FROM configuracion WHERE id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        finally {
            try {
                if(stmt!=null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}