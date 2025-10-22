package entities;

import java.util.LinkedList;

public class Cancha {
    private int id;
    private int nro_cancha;
    private String ubicacion;
    private String descripcion;
    private float tamanio;
    private boolean estado;


    private LinkedList<Partido> partidos;
    private LinkedList<Alquiler_cancha> alquileresCancha;
    private LinkedList <Actividad> actividades; 


    public Cancha() {
        this.partidos = new LinkedList<>();
        this.alquileresCancha = new LinkedList<>();
        this.actividades = new LinkedList<>();
    }


    public Cancha(int nro_cancha, String ubicacion, String descripcion, float tamanio, boolean estado) {
        this.nro_cancha = nro_cancha;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.tamanio = tamanio;
        this.estado = estado;
        this.partidos = new LinkedList<>();
        this.alquileresCancha = new LinkedList<>();
        this.actividades = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNro_cancha() {
        return nro_cancha;
    }

    public void setNro_cancha(int nro_cancha) {
        this.nro_cancha = nro_cancha;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getTamanio() {
        return tamanio;
    }

    public void setTamanio(float tamanio) {
        this.tamanio = tamanio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public LinkedList<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(LinkedList<Partido> partidos) {
        this.partidos = partidos;
    }


	public LinkedList<Alquiler_cancha> getAlquileresCancha() {
		return alquileresCancha;
	}


	public void setAlquileresCancha(LinkedList<Alquiler_cancha> alquileresCancha) {
		this.alquileresCancha = alquileresCancha;
	}


	public LinkedList<Actividad> getActividades() {
		return actividades;
	}


	public void setActividades(LinkedList<Actividad> actividades) {
		this.actividades = actividades;
	}

    
}