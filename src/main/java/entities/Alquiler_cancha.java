package entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Alquiler_cancha {
    private int id;
    private LocalDate fecha_alquiler;
    private LocalTime hora_desde;
    private LocalTime hora_hasta;
    private int id_cancha;
    private int id_usuario;  

    
    public Alquiler_cancha() {}


    public Alquiler_cancha(LocalDate fecha_alquiler, LocalTime hora_desde, LocalTime hora_hasta, int id_cancha, int id_usuario) {
        this.fecha_alquiler = fecha_alquiler;
        this.hora_desde = hora_desde;
        this.hora_hasta = hora_hasta;
        this.id_cancha = id_cancha;
        this.id_usuario = id_usuario;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFechaAlquiler() {
        return fecha_alquiler;
    }

    public void setFechaAlquiler(LocalDate fecha_alquiler) {
        this.fecha_alquiler = fecha_alquiler;
    }

    public LocalTime getHoraDesde() {
        return hora_desde;
    }

    public void setHoraDesde(LocalTime horaDesde) {
        this.hora_desde = horaDesde;
    }

    public LocalTime getHoraHasta() {
        return hora_hasta;
    }

    public void setHoraHasta(LocalTime hora_hasta) {
        this.hora_hasta = hora_hasta;
    }

	public int getId_cancha() {
		return id_cancha;
	}

	public void setId_cancha(int id_cancha) {
		this.id_cancha = id_cancha;
	}

	public int getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}

    

}