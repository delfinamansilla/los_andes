package entities;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Horario {
    private int id;
    private String dia;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
    private int idActividad; 
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");


    public Horario() {
    }

    public Horario(String dia, LocalTime horaDesde, LocalTime horaHasta, int idActividad) {
        this.dia = dia;
        this.horaDesde = horaDesde;
        this.horaHasta = horaHasta;
        this.idActividad = idActividad;
    }

    public Horario(int id, String dia, LocalTime horaDesde, LocalTime horaHasta, int idActividad) {
        this.id = id;
        this.dia = dia;
        this.horaDesde = horaDesde;
        this.horaHasta = horaHasta;
        this.idActividad = idActividad;
    }

    public int getId() {
        return id;
    }

    public String getDia() {
        return dia;
    }

    public LocalTime getHoraDesde() {
        return horaDesde;
    }

    public LocalTime getHoraHasta() {
        return horaHasta;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public void setHoraDesde(LocalTime horaDesde) {
        this.horaDesde = horaDesde;
    }

    public void setHoraHasta(LocalTime horaHasta) {
        this.horaHasta = horaHasta;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    @Override
    public String toString() {
        return "Horario{" +
                "id=" + id +
                ", dia='" + dia + '\'' +
                ", horaDesde=" + (horaDesde != null ? horaDesde.format(TIME_FORMATTER) : null) +
                ", horaHasta=" + (horaHasta != null ? horaHasta.format(TIME_FORMATTER) : null) +
                ", idActividad=" + idActividad +
                '}';
    } 
}
