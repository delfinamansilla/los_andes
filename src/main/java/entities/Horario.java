package entities;

public class Horario {
    private int id;
    private String dia;
    private String horaDesde;
    private String horaHasta;
    private int idActividad; // clave foránea hacia Actividad

    // Constructor vacío (necesario para frameworks o DAOs)
    public Horario() {
    }

    // Constructor sin ID (para nuevos registros)
    public Horario(String dia, String horaDesde, String horaHasta, int idActividad) {
        this.dia = dia;
        this.horaDesde = horaDesde;
        this.horaHasta = horaHasta;
        this.idActividad = idActividad;
    }

    // Constructor con ID (para registros ya existentes en la BD)
    public Horario(int id, String dia, String horaDesde, String horaHasta, int idActividad) {
        this.id = id;
        this.dia = dia;
        this.horaDesde = horaDesde;
        this.horaHasta = horaHasta;
        this.idActividad = idActividad;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDia() {
        return dia;
    }

    public String getHoraDesde() {
        return horaDesde;
    }

    public String getHoraHasta() {
        return horaHasta;
    }

    public int getIdActividad() {
        return idActividad;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public void setHoraDesde(String horaDesde) {
        this.horaDesde = horaDesde;
    }

    public void setHoraHasta(String horaHasta) {
        this.horaHasta = horaHasta;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    // Para depuración o logs
    @Override
    public String toString() {
        return "Horario{" +
                "id=" + id +
                ", dia='" + dia + '\'' +
                ", horaDesde='" + horaDesde + '\'' +
                ", horaHasta='" + horaHasta + '\'' +
                ", idActividad=" + idActividad +
                '}';
    }
}
