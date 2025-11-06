package entities;

import java.time.LocalDate;

public class Actividad {
    private int id;
    private String nombre;
    private int cupo;
    private String descripcion;
    private LocalDate inscripcion_desde;
    private LocalDate inscripcion_hasta;
    private int id_profesor;
    private int id_cancha;
    private int cupo_restante;
    private boolean yaInscripto;

  
    public Actividad(String nombre, int cupo, String descripcion, LocalDate inscripcion_desde,
                     LocalDate inscripcion_hasta, int id_profesor, int id_cancha) {
        this.nombre = nombre;
        this.cupo = cupo;
        this.descripcion = descripcion;
        this.inscripcion_desde = inscripcion_desde;
        this.inscripcion_hasta = inscripcion_hasta;
        this.id_profesor = id_profesor;
        this.id_cancha = id_cancha;
    }

 
    public Actividad() {
    }

    
    public Actividad(int id, String nombre, int cupo, String descripcion, LocalDate inscripcion_desde,
                     LocalDate inscripcion_hasta, int id_profesor, int id_cancha) {
        this.id = id;
        this.nombre = nombre;
        this.cupo = cupo;
        this.descripcion = descripcion;
        this.inscripcion_desde = inscripcion_desde;
        this.inscripcion_hasta = inscripcion_hasta;
        this.id_profesor = id_profesor;
        this.id_cancha = id_cancha;
    }

 
    public int getIdActividad() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCupo() {
        return cupo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getInscripcionDesde() {
        return inscripcion_desde;
    }

    public LocalDate getInscripcionHasta() {
        return inscripcion_hasta;
    }

    public int getIdProfesor() {
        return id_profesor;
    }

    public int getIdCancha() {
        return id_cancha;
    }

   
    public void setIdActividad(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCupo(int cupo) {
        this.cupo = cupo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setInscripcionDesde(LocalDate inscripcion_desde) {
        this.inscripcion_desde = inscripcion_desde;
    }

    public void setInscripcionHasta(LocalDate inscripcion_hasta) {
        this.inscripcion_hasta = inscripcion_hasta;
    }

    public void setIdProfesor(int id_profesor) {
        this.id_profesor = id_profesor;
    }

    public void setIdCancha(int id_cancha) {
        this.id_cancha = id_cancha;
    }
    
    public int getCupo_restante() {
        return cupo_restante;
    }

    public void setCupo_restante(int cupo_restante) {
        this.cupo_restante = cupo_restante;
    }

    public boolean isYaInscripto() {
        return yaInscripto;
    }

    public void setYaInscripto(boolean yaInscripto) {
        this.yaInscripto = yaInscripto;
    }
}
