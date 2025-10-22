package entities;

public class Profesor {
    private int id;
    private String nombre_completo;
    private String telefono;
    private String mail;

    
    public Profesor(String nombre_completo, String telefono, String mail) {
        this.nombre_completo = nombre_completo;
        this.telefono = telefono;
        this.mail = mail;
    }

    
    public Profesor() {
    }

   
    public Profesor(int id, String nombre_completo, String telefono, String mail) {
        this.id = id;
        this.nombre_completo = nombre_completo;
        this.telefono = telefono;
        this.mail = mail;
    }

 
    public int getIdProfesor() {
        return id;
    }

    public String getNombreCompleto() {
        return nombre_completo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getMail() {
        return mail;
    }

    
    public void setIdProfesor(int id) {
        this.id = id;
    }

    public void setNombreCompleto(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
