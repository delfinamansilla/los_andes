package entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Partido {
    private int id;
    private LocalDate fecha;
    private String oponente;
    private LocalTime hora_desde;
    private LocalTime hora_hasta;
    private String categoria;
    private Double precio_entrada;
    private Integer id_cancha;
    private int id_actividad;

    public Partido() {}

    public Partido(LocalDate fecha, String oponente, LocalTime hora_desde, LocalTime hora_hasta,
                   String categoria, Double precio_entrada, int id_cancha, int id_actividad) {
        this.fecha = fecha;
        this.oponente = oponente;
        this.hora_desde = hora_desde;
        this.hora_hasta = hora_hasta;
        this.categoria = categoria;
        this.precio_entrada = precio_entrada;
        this.id_cancha = id_cancha;
        this.id_actividad = id_actividad;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getOponente() {
        return oponente;
    }

    public void setOponente(String oponente) {
        this.oponente = oponente;
    }

    public LocalTime getHora_desde() {
        return hora_desde;
    }

    public void setHora_desde(LocalTime hora_desde) {
        this.hora_desde = hora_desde;
    }

    public LocalTime getHora_hasta() {
        return hora_hasta;
    }

    public void setHora_hasta(LocalTime hora_hasta) {
        this.hora_hasta = hora_hasta;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecio_entrada() {
        return precio_entrada;
    }

    public void setPrecio_entrada(Double precio_entrada) {
        this.precio_entrada = precio_entrada;
    }

    public Integer getId_cancha() {
        return id_cancha;
    }

    public void setId_cancha(Integer id_cancha) {
        this.id_cancha = id_cancha;
    }

    public int getId_actividad() {
        return id_actividad;
    }

    public void setId_actividad(int id_actividad) {
        this.id_actividad = id_actividad;
    }
}