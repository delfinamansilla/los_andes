package entities;
import java.time.LocalDateTime;

public class Configuracion {
    private int id;
    private String clave;
    private String valor;
    private LocalDateTime fechaCambio;

    public Configuracion() {}
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }
}