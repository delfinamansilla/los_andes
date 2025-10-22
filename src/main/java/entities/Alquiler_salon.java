package entities;

import java.time.LocalDate;

public class Alquiler_salon {
    private int id;
    private LocalDate fecha;
    private String horaDesde;
    private String horaHasta;
    private int idSalon;
    private int idUsuario;

    public Alquiler_salon() {
    }

    public Alquiler_salon(LocalDate fecha, String horaDesde, String horaHasta, int idSalon, int idUsuario) {
        this.fecha = fecha;
        this.horaDesde = horaDesde;
        this.horaHasta = horaHasta;
        this.idSalon = idSalon;
        this.idUsuario = idUsuario;
    }

    public Alquiler_salon(int id, LocalDate fecha, String horaDesde, String horaHasta, int idSalon, int idUsuario) {
        this.id = id;
        this.fecha = fecha;
        this.horaDesde = horaDesde;
        this.horaHasta = horaHasta;
        this.idSalon = idSalon;
        this.idUsuario = idUsuario;
    }

    public int getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getHoraDesde() {
        return horaDesde;
    }

    public String getHoraHasta() {
        return horaHasta;
    }

    public int getIdSalon() {
        return idSalon;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHoraDesde(String horaDesde) {
        this.horaDesde = horaDesde;
    }

    public void setHoraHasta(String horaHasta) {
        this.horaHasta = horaHasta;
    }

    public void setIdSalon(int idSalon) {
        this.idSalon = idSalon;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "AlquilerSalon{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", horaDesde='" + horaDesde + '\'' +
                ", horaHasta='" + horaHasta + '\'' +
                ", idSalon=" + idSalon +
                ", idUsuario=" + idUsuario +
                '}';
    }
}
