package entities;

import java.time.LocalDate;

public class Monto_cuota {
    private LocalDate fecha; 
    private double monto;
    private int id_cuota;  

    public Monto_cuota() {
    	this.fecha = LocalDate.now();
    }


    public Monto_cuota(double monto, int id_cuota) {
    	this.fecha = LocalDate.now();
        this.monto = monto;
        this.id_cuota = id_cuota;
    }

    public LocalDate getFecha() { return fecha; }
    public double getMonto() { return monto; }
    public int getId_cuota() { return id_cuota; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setMonto(double monto) { this.monto = monto; }
    public void setId_cuota(int id_cuota) { this.id_cuota = id_cuota; }
}