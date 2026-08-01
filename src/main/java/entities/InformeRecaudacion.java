package entities;



public class InformeRecaudacion {

    private String nombreUsuario;
    private int cuota;
    private double monto;
    private String fechaPago;
    private String nroTransaccion;


    public InformeRecaudacion() {
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }


    public int getCuota() {
        return cuota;
    }

    public void setCuota(int cuota) {
        this.cuota = cuota;
    }


    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }


    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }


    public String getNroTransaccion() {
        return nroTransaccion;
    }

    public void setNroTransaccion(String nroTransaccion) {
        this.nroTransaccion = nroTransaccion;
    }
}