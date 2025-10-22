package entities;

import java.time.LocalDate;

public class Inscripcion {
    private int id;
    private LocalDate fecha_inscripcion;
    private int id_usuario;
    private int id_actividad;

    
    public Inscripcion(LocalDate fecha_inscripcion, int id_usuario, int id_actividad) {
        this.fecha_inscripcion = fecha_inscripcion;
        this.id_usuario = id_usuario;
        this.id_actividad = id_actividad;
    }

    
    public Inscripcion() {
    }

    
    public Inscripcion(int id, LocalDate fecha_inscripcion, int id_usuario, int id_actividad) {
        this.id = id;
        this.fecha_inscripcion = fecha_inscripcion;
        this.id_usuario = id_usuario;
        this.id_actividad = id_actividad;
    }

   
    public int getIdInscripcion() {
        return id;
    }

    public LocalDate getFechaInscripcion() {
        return fecha_inscripcion;
    }

    public int getIdUsuario() {
        return id_usuario;
    }

    public int getIdActividad() {
        return id_actividad;
    }

    
    public void setIdInscripcion(int id) {
        this.id = id;
    }

    public void setFechaInscripcion(LocalDate fecha_inscripcion) {
        this.fecha_inscripcion = fecha_inscripcion;
    }

    public void setIdUsuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public void setIdActividad(int id_actividad) {
        this.id_actividad = id_actividad;
    }
}
