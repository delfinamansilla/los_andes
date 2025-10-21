package entities;

import java.time.LocalDate;

public class Usuario {
    private int id;
    private String nombre_completo;
    private String dni;
    private String telefono;
    private String mail;
    private LocalDate fecha_nacimiento;
    private String contrasenia;
    private boolean estado;
    private String rol;
    private Integer nro_socio; // puede ser nulo

    // Constructor sin ID (para nuevos usuarios)
    public Usuario(String nombre_completo, String dni, String telefono, String mail,
                   LocalDate fecha_nacimiento, String contrasenia, boolean estado, String rol, Integer nro_socio) {
        this.nombre_completo = nombre_completo;
        this.dni = dni;
        this.telefono = telefono;
        this.mail = mail;
        this.fecha_nacimiento = fecha_nacimiento;
        this.contrasenia = contrasenia;
        this.estado = estado;
        this.rol = rol;
        this.nro_socio = nro_socio;
    }
    
    public Usuario() {
	}

    // Constructor con ID (para usuarios ya cargados en la BD)
    public Usuario(int id, String nombre_completo, String dni, String telefono, String mail,
                   LocalDate fecha_nacimiento, String contrasenia, boolean estado, String rol, Integer nro_socio) {
        this.id = id;
        this.nombre_completo = nombre_completo;
        this.dni = dni;
        this.telefono = telefono;
        this.mail = mail;
        this.fecha_nacimiento = fecha_nacimiento;
        this.contrasenia = contrasenia;
        this.estado = estado;
        this.rol = rol;
        this.nro_socio = nro_socio;
    }

    // Getters
    public int getIdUsuario() { return id; }
    public String getNombreCompleto() { return nombre_completo; }
    public String getDni() { return dni; }
    public String getTelefono() { return telefono; }
    public String getMail() { return mail; }
    public LocalDate getFechaNacimiento() { return fecha_nacimiento; }
    public String getContrasenia() { return contrasenia; }
    public boolean isEstado() { return estado; }
    public String getRol() { return rol; }
    public Integer getNroSocio() { return nro_socio; }

    // Setters
    public void setNombreCompleto(String nombre_completo) { this.nombre_completo = nombre_completo; }
    public void setDni(String dni) { this.dni = dni; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setMail(String mail) { this.mail = mail; }
    public void setFechaNacimiento(LocalDate fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }
    public void setEstado(boolean estado) { this.estado = estado; }
    public void setRol(String rol) { this.rol = rol; }
    public void setNroSocio(Integer nro_socio) { this.nro_socio = nro_socio; }
    public void setIdUsuario(Integer id) { this.id = id; }

}