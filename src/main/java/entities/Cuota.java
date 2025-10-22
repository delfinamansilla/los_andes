package entities;

import java.time.LocalDate;

public class Cuota {
    private int id;
    private int nro_cuota;
    private LocalDate fecha_cuota;
    private LocalDate fecha_vencimiento;

    public Cuota() {}

    public Cuota(int nro_cuota, LocalDate fecha_cuota, LocalDate fecha_vencimiento) {
        this.nro_cuota = nro_cuota;
        this.fecha_cuota = fecha_cuota;
        this.fecha_vencimiento = fecha_vencimiento;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNro_cuota() {
        return nro_cuota;
    }

    public void setNro_cuota(int nro_cuota) {
        this.nro_cuota = nro_cuota;
    }

    public LocalDate getFecha_cuota() {
        return fecha_cuota;
    }

    public void setFecha_cuota(LocalDate fecha_cuota) {
        this.fecha_cuota = fecha_cuota;
    }

    public LocalDate getFecha_vencimiento() {
        return fecha_vencimiento;
    }

    public void setFecha_vencimiento(LocalDate fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }
}