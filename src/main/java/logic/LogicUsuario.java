package logic;
import entities.Usuario;
import data.DataUsuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import javax.xml.bind.DatatypeConverter;
import java.util.HexFormat;
import java.io.InputStream;


public class LogicUsuario {

    private DataUsuario du;

    public LogicUsuario() {
        this.du = new DataUsuario();
    }

    /**
     * Realiza el proceso de login validando las credenciales.
     * @param usuario Objeto con mail y contraseña.
     * @return El usuario completo si el login es exitoso, sino null.
     */
    public Usuario login(Usuario usuario) {
        // Validación básica de entrada
        if (usuario.getMail() == null || usuario.getMail().isEmpty() ||
            usuario.getContrasenia() == null || usuario.getContrasenia().isEmpty()) {
            return null;
        }

        // Hashear la contraseña antes de enviarla a la base de datos
        usuario.setContrasenia(hashPassword(usuario.getContrasenia()));
        
        return du.getByMailAndContrasenia(usuario);
    }
    
    /**
     * Devuelve todos los usuarios.
     * @return Lista de todos los usuarios.
     */
    public LinkedList<Usuario> getAll() {
        return du.getAll();
    }

    /**
     * Procesa la creación de un nuevo usuario, aplicando todas las validaciones.
     * @param u El nuevo usuario a registrar.
     * @throws Exception Si alguna validación de negocio falla.
     */
    public void add(Usuario u) throws Exception {
        // 1. Validar todos los campos del usuario
        validarUsuario(u);

        // 2. Formatear datos antes de guardar
        u.setMail(u.getMail().toLowerCase()); // Guardar siempre el email en minúsculas

        // 3. Hashear la contraseña por seguridad
        u.setContrasenia(hashPassword(u.getContrasenia()));

        // 4. Llamar a la capa de datos para guardar
        du.add(u);
    }
    
    /**
     * Procesa la actualización de un usuario existente.
     * Si la contraseña en el objeto 'u' viene vacía, se mantiene la existente en la BD.
     * @param u El usuario con los datos a modificar.
     * @throws Exception Si alguna validación de negocio falla o el usuario no existe.
     */
    public void update(Usuario u) throws Exception {
        if (u.getContrasenia() == null || u.getContrasenia().isEmpty()){
        } else if (u.getContrasenia().length() < 8) {
            throw new Exception("La nueva contraseña debe tener al menos 8 caracteres.");
        }
        validarUsuario(u);
        
        u.setMail(u.getMail().toLowerCase());

        if (u.getContrasenia() != null && !u.getContrasenia().trim().isEmpty()) {

            u.setContrasenia(hashPassword(u.getContrasenia()));
        } else {
            Usuario usuarioActual = du.getById(u.getIdUsuario());
            
            if (usuarioActual == null) {
                throw new Exception("No se encontró el usuario que intenta modificar.");
            }
            
            u.setContrasenia(usuarioActual.getContrasenia());
        }

        du.update(u);
    }
    /**
     * Procesa la eliminación de un usuario.
     * @param id El ID del usuario a eliminar.
     */
    public void delete(int id) {
        if (id > 0) {
            du.delete(id);
        }
    }
    
    /**
     * Busca un usuario por su ID.
     * @param id El ID del usuario a buscar.
     * @return El objeto Usuario si se encuentra, sino null.
     */
    public Usuario getById(int id) {
        return du.getById(id); 
    }

    /**
     * Procesa la actualización de la foto de perfil de un usuario.
     * @param idUsuario El ID del usuario a modificar.
     * @param fotoStream El flujo de bytes de la nueva imagen.
     */
    public void updateFoto(int idUsuario, InputStream fotoStream) {
        du.updateFoto(idUsuario, fotoStream);
    }

    /**
     * Obtiene los bytes de la foto de un usuario.
     * @param idUsuario El ID del usuario.
     * @return Un array de bytes con los datos de la imagen.
     */
    public byte[] getFotoById(int idUsuario) {
        return du.getFotoById(idUsuario);
    }


    /**
     * Método centralizado de validaciones para la entidad Usuario.
     * Lanza una excepción si alguna regla no se cumple.
     * @param u El usuario a validar.
     * @throws Exception con el mensaje del error de validación.
     */
    private void validarUsuario(Usuario u) throws Exception {

        if (u.getNombreCompleto() == null || u.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("El nombre no puede estar vacío.");
        }
        if (u.getDni() == null || u.getDni().trim().isEmpty()) {
            throw new Exception("El DNI no puede estar vacío.");
        }
        if (u.getMail() == null || u.getMail().trim().isEmpty()) {
            throw new Exception("El email no puede estar vacío.");
        }
        if (u.getContrasenia() == null || u.getContrasenia().isEmpty()) {
            throw new Exception("La contraseña no puede estar vacía.");
        }
        if (u.getFechaNacimiento() == null) {
            throw new Exception("La fecha de nacimiento no puede estar vacía.");
        }

        if (u.getContrasenia().length() < 8) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres.");
        }

        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(u.getMail());
        if (!matcher.matches()) {
            throw new Exception("El formato del email no es válido.");
        }

        if (!u.getDni().matches("\\d{7,8}")) {
            throw new Exception("El DNI debe contener entre 7 y 8 dígitos numéricos.");
        }
        
        Usuario usuarioExistente = du.getByDNI(u.getDni());

        if (usuarioExistente != null && usuarioExistente.getIdUsuario() != u.getIdUsuario()) {

            throw new Exception("El DNI ya se encuentra registrado para otro usuario.");
        }

        if (u.getTelefono() != null && !u.getTelefono().isEmpty() && !u.getTelefono().matches("\\d+")) {
            throw new Exception("El teléfono solo puede contener números.");
        }

        String rol = u.getRol();
        if (rol == null || (!rol.equalsIgnoreCase("administrador") && !rol.equalsIgnoreCase("socio") )) {
            throw new Exception("El rol especificado no es válido.");
        }
  
    }

    /**
     * Hashea una contraseña usando el algoritmo SHA-256 por seguridad.
     * Nunca se deben guardar contraseñas en texto plano.
     * @param password La contraseña en texto plano.
     * @return El hash de la contraseña en formato hexadecimal.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            return HexFormat.of().formatHex(digest).toUpperCase(); 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo hashear la contraseña.", e);
        }
    }
}