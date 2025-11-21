package logic;

import java.time.LocalDateTime;
import java.util.UUID;
import data.DataRecuperacionPass;
import entities.RecuperacionPass;

public class LogicRecuperacionPass {
    private DataRecuperacionPass dr = new DataRecuperacionPass();

    public RecuperacionPass crearSolicitud(int idUsuario) {
        RecuperacionPass r = new RecuperacionPass();
        r.setIdUsuario(idUsuario);
        
        // Generar Token Único
        r.setToken(UUID.randomUUID().toString());
        
        // Expira en 60 minutos
        r.setExpiracion(LocalDateTime.now().plusMinutes(60));
        
        dr.add(r);
        return r;
    }

    public RecuperacionPass obtenerPorToken(String token) {
        return dr.getByToken(token);
    }

    public void eliminarToken(String token) {
        dr.deleteByToken(token);
    }
}