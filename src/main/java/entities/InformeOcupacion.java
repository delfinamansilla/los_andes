package entities;

public class InformeOcupacion {
    private String tipoRecurso; 
    private String nombreRecurso; 
    private String nombreUsuario;
    private String fecha;
    private String horario; 

    public InformeOcupacion() {}

    public String getTipoRecurso() { return tipoRecurso; }
    public void setTipoRecurso(String tipoRecurso) { this.tipoRecurso = tipoRecurso; }
    public String getNombreRecurso() { return nombreRecurso; }
    public void setNombreRecurso(String nombreRecurso) { this.nombreRecurso = nombreRecurso; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}