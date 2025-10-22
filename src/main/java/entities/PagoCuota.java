package entities;

import java.time.LocalDate;

public class PagoCuota {
    private int id;
    private LocalDate fecha_pago;
    private int id_usuario;
    private int id_cuota;

    public PagoCuota() {}

    public PagoCuota(LocalDate fecha_pago, int id_usuario, int id_cuota) {
        this.fecha_pago = fecha_pago;
        this.id_usuario = id_usuario;
        this.id_cuota = id_cuota;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(LocalDate fecha_pago) {
        this.fecha_pago = fecha_pago;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_cuota() {
        return id_cuota;
    }

    public void setId_cuota(int id_cuota) {
        this.id_cuota = id_cuota;
    }
}