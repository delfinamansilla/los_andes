package entities;

import java.time.LocalDateTime;

public class RecuperacionPass {
    private int id;
    private int id_usuario;
    private String token;
    private LocalDateTime expiracion;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getIdUsuario() { return id_usuario; }
    public void setIdUsuario(int id_usuario) { this.id_usuario = id_usuario; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public LocalDateTime getExpiracion() { return expiracion; }
    public void setExpiracion(LocalDateTime expiracion) { this.expiracion = expiracion; }
}