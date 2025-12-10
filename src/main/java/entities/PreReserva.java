package entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PreReserva {

    private int id;
    private int idSalon;
    private int idUsuario;
    private LocalDate fecha;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
    private String token;
    private LocalDateTime expiracion;


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdSalon() { return idSalon; }
    public void setIdSalon(int idSalon) { this.idSalon = idSalon; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraDesde() { return horaDesde; }
    public void setHoraDesde(LocalTime horaDesde) { this.horaDesde = horaDesde; }

    public LocalTime getHoraHasta() { return horaHasta; }
    public void setHoraHasta(LocalTime horaHasta) { this.horaHasta = horaHasta; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiracion() { return expiracion; }
    public void setExpiracion(LocalDateTime expiracion) { this.expiracion = expiracion; }
}
