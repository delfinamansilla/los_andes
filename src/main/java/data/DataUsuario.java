package data;

import entities.Usuario;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;


/**
 * Clase para interactuar con la tabla 'usuario' en la base de datos.
 * Contiene métodos CRUD: obtener, buscar, agregar, actualizar y eliminar usuarios.
 */
public class DataUsuario {
	/** Busca un usuario por su DNI.
	 * @param dni El DNI a buscar.
	 * @return El objeto Usuario completo si existe, o null si no.
	 */
	public Usuario getByDNI(String dni) {
	    Usuario u = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        stmt = DbConnector.getInstancia().getConn().prepareStatement(
	            "SELECT * FROM usuario WHERE dni = ?"
	        );
	        stmt.setString(1, dni);
	        rs = stmt.executeQuery();

	        if (rs != null && rs.next()) {
	            u = new Usuario();
	            u.setIdUsuario(rs.getInt("id"));
	            u.setNombreCompleto(rs.getString("nombre_completo"));
	            u.setDni(rs.getString("dni"));
	            u.setTelefono(rs.getString("telefono"));
	            u.setMail(rs.getString("mail"));
	            
	            Date fecha = rs.getDate("fecha_nacimiento");
	            if (fecha != null) {
	                u.setFechaNacimiento(fecha.toLocalDate());
	            }
	            u.setContrasenia(rs.getString("contrasenia"));
	            u.setEstado(rs.getBoolean("estado"));
	            u.setRol(rs.getString("rol"));
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

	    return u;
	}

    /**
     * Devuelve una lista con todos los usuarios de la base de datos.
     * @return LinkedList<Usuario> con todos los usuarios.
     */
    public LinkedList<Usuario> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Usuario> usuarios = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();
            rs = stmt.executeQuery("SELECT * FROM usuario");

            if (rs != null) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id"));
                    u.setNombreCompleto(rs.getString("nombre_completo"));
                    u.setDni(rs.getString("dni"));
                    u.setTelefono(rs.getString("telefono"));
                    u.setMail(rs.getString("mail"));
                    
                    Date fecha = rs.getDate("fecha_nacimiento");
                    if (fecha != null) {
                        u.setFechaNacimiento(fecha.toLocalDate());
                    }
                    
                    u.setContrasenia(rs.getString("contrasenia"));
                    u.setEstado(rs.getBoolean("estado"));
                    u.setRol(rs.getString("rol"));

                    usuarios.add(u);
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

        return usuarios;
    }

    /**
     * Busca un usuario por su email y clave. Ideal para login.
     * @param usuario Objeto Usuario con mail y clave cargados.
     * @return El objeto Usuario completo si existe, o null si no.
     */
    public Usuario getByMailAndContrasenia(Usuario usuario) {
        Usuario u = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM usuario WHERE mail = ? AND contrasenia = ?"
            );
            stmt.setString(1, usuario.getMail());
            stmt.setString(2, usuario.getContrasenia());
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                u = new Usuario();
                u.setIdUsuario(rs.getInt("id"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setDni(rs.getString("dni"));
                u.setTelefono(rs.getString("telefono"));
                u.setMail(rs.getString("mail"));
                
                Date fecha = rs.getDate("fecha_nacimiento");
                if (fecha != null) {
                    u.setFechaNacimiento(fecha.toLocalDate());
                }
                u.setContrasenia(rs.getString("contrasenia"));
                u.setEstado(rs.getBoolean("estado"));
                u.setRol(rs.getString("rol"));
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

        return u;
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     * @param u El objeto Usuario con los datos a insertar.
     */
    public void add(Usuario u) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO usuario (nombre_completo, dni, telefono, mail, fecha_nacimiento, contrasenia, estado, rol) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, u.getNombreCompleto());
            stmt.setString(2, u.getDni());
            stmt.setString(3, u.getTelefono());
            stmt.setString(4, u.getMail());
            stmt.setDate(5, Date.valueOf(u.getFechaNacimiento()));
            stmt.setString(6, u.getContrasenia());
            stmt.setBoolean(7, u.isEstado());
            stmt.setString(8, u.getRol());

            stmt.executeUpdate();

            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                u.setIdUsuario(keyResultSet.getInt(1));
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
     * Actualiza los datos de un usuario existente.
     * @param u Objeto Usuario con el ID cargado y los datos actualizados.
     */
    public void update(Usuario u) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE usuario SET nombre_completo=?, dni=?, telefono=?, mail=?, fecha_nacimiento=?, contrasenia=?, estado=?, rol=? " +
                "WHERE id=?"
            );
            stmt.setString(1, u.getNombreCompleto());
            stmt.setString(2, u.getDni());
            stmt.setString(3, u.getTelefono());
            stmt.setString(4, u.getMail());
            stmt.setDate(5, Date.valueOf(u.getFechaNacimiento()));
            stmt.setString(6, u.getContrasenia());
            stmt.setBoolean(7, u.isEstado());
            stmt.setString(8, u.getRol());
            stmt.setInt(9, u.getIdUsuario());

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
     * Busca y devuelve un usuario por su ID.
     * @param id El ID del usuario a buscar.
     * @return El objeto Usuario completo si se encuentra, o null si no.
     */
    public Usuario getById(int id) {
        Usuario u = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT * FROM usuario WHERE id = ?"
            );
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                u = new Usuario();
                u.setIdUsuario(rs.getInt("id"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setDni(rs.getString("dni"));
                u.setTelefono(rs.getString("telefono"));
                u.setMail(rs.getString("mail"));
                
                Date fecha = rs.getDate("fecha_nacimiento");
                if (fecha != null) {
                    u.setFechaNacimiento(fecha.toLocalDate());
                }
                u.setContrasenia(rs.getString("contrasenia"));
                u.setEstado(rs.getBoolean("estado"));
                u.setRol(rs.getString("rol"));
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

        return u;
    }
    

    /**
     * Elimina un usuario de la base de datos por su ID.
     * @param idUsuario ID del usuario a eliminar.
     */
    public void delete(int id) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "DELETE FROM usuario WHERE id = ?"
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