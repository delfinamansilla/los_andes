package data;

import entities.Partido;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class DataPartido {

    public LinkedList<Partido> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Partido> partidos = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT id, fecha, oponente, hora_desde, hora_hasta, categoria, precio_entrada, id_cancha, id_actividad FROM partido");

            if (rs != null) {
                while (rs.next()) {
                    Partido p = new Partido();
                    p.setId(rs.getInt("id"));
                    p.setFecha(rs.getObject("fecha", LocalDate.class));
                    p.setOponente(rs.getString("oponente"));
                    p.setHora_desde(rs.getObject("hora_desde", LocalTime.class));
                    p.setHora_hasta(rs.getObject("hora_hasta", LocalTime.class));
                    p.setCategoria(rs.getString("categoria"));
                    p.setPrecio_entrada(rs.getDouble("precio_entrada"));
                    p.setId_cancha(rs.getInt("id_cancha"));
                    p.setId_actividad(rs.getInt("id_actividad"));
                    partidos.add(p);
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
        return partidos;
    }
    
    public LinkedList<Partido> getByCanchaAndFecha(int idCancha, LocalDate fecha) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Partido> partidos = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM partido WHERE id_cancha = ? AND fecha = ?"
            );
            stmt.setInt(1, idCancha);
            stmt.setObject(2, fecha);
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Partido p = new Partido();
                // Esto es lo importante: Carga TODOS los datos que necesitas
                p.setId(rs.getInt("id"));
                p.setFecha(rs.getObject("fecha", LocalDate.class));
                p.setOponente(rs.getString("oponente"));
                p.setHora_desde(rs.getObject("hora_desde", LocalTime.class));
                p.setHora_hasta(rs.getObject("hora_hasta", LocalTime.class));
                p.setCategoria(rs.getString("categoria"));
                p.setPrecio_entrada(rs.getDouble("precio_entrada"));
                p.setId_cancha(rs.getInt("id_cancha"));
                p.setId_actividad(rs.getInt("id_actividad"));
                partidos.add(p);
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
        return partidos;
    }

    public Partido getById(int id) {
        Partido p = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT id, fecha, oponente, hora_desde, hora_hasta, categoria, precio_entrada, id_cancha, id_actividad FROM partido WHERE id=?"
            );
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                p = new Partido();
                p.setId(rs.getInt("id"));
                p.setFecha(rs.getObject("fecha", LocalDate.class));
                p.setOponente(rs.getString("oponente"));
                p.setHora_desde(rs.getObject("hora_desde", LocalTime.class));
                p.setHora_hasta(rs.getObject("hora_hasta", LocalTime.class));
                p.setCategoria(rs.getString("categoria"));
                p.setPrecio_entrada(rs.getDouble("precio_entrada"));
                p.setId_cancha(rs.getInt("id_cancha"));
                p.setId_actividad(rs.getInt("id_actividad"));
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

    public void add(Partido p) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO partido(fecha, oponente, hora_desde, hora_hasta, categoria, precio_entrada, id_cancha, id_actividad) VALUES(?,?,?,?,?,?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setObject(1, p.getFecha());
            stmt.setString(2, p.getOponente());
            stmt.setObject(3, p.getHora_desde());
            stmt.setObject(4, p.getHora_hasta());
            stmt.setString(5, p.getCategoria());
            stmt.setDouble(6, p.getPrecio_entrada());
            if (p.getId_cancha() == null) {
                stmt.setNull(7, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(7, p.getId_cancha());
            }
            stmt.setInt(8, p.getId_actividad());
            stmt.executeUpdate();
            
            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                p.setId(keyResultSet.getInt(1));
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

    public void update(Partido p) {
        PreparedStatement stmt = null;
        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE partido SET fecha=?, oponente=?, hora_desde=?, hora_hasta=?, categoria=?, precio_entrada=?, id_cancha=?, id_actividad=? WHERE id=?"
            );
            stmt.setObject(1, p.getFecha());
            stmt.setString(2, p.getOponente());
            stmt.setObject(3, p.getHora_desde());
            stmt.setObject(4, p.getHora_hasta());
            stmt.setString(5, p.getCategoria());
            stmt.setDouble(6, p.getPrecio_entrada());
            if(p.getId_cancha() == null){
                stmt.setNull(7, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(7, p.getId_cancha());
            }
            stmt.setInt(8, p.getId_actividad());
            stmt.setInt(9, p.getId());
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
            stmt = DbConnector.getInstancia().getConn().prepareStatement("DELETE FROM partido WHERE id=?");
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
    
    
    public LinkedList<Partido> getByFechaRango(LocalDate fechaDesde, LocalDate fechaHasta) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        LinkedList<Partido> partidos = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT id, fecha, oponente, hora_desde, hora_hasta, categoria, precio_entrada, id_cancha, id_actividad " +
                "FROM partido WHERE fecha BETWEEN ? AND ? ORDER BY fecha ASC"
            );

            stmt.setObject(1, fechaDesde);
            stmt.setObject(2, fechaHasta);

            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {
                Partido p = new Partido();
                p.setId(rs.getInt("id"));
                p.setFecha(rs.getObject("fecha", LocalDate.class));
                p.setOponente(rs.getString("oponente"));
                p.setHora_desde(rs.getObject("hora_desde", LocalTime.class));
                p.setHora_hasta(rs.getObject("hora_hasta", LocalTime.class));
                p.setCategoria(rs.getString("categoria"));
                p.setPrecio_entrada(rs.getDouble("precio_entrada"));
                p.setId_cancha(rs.getInt("id_cancha"));
                p.setId_actividad(rs.getInt("id_actividad"));

                partidos.add(p);
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

        return partidos;
    }

}