package logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import data.DataPreReserva;
import entities.PreReserva;

public class LogicPreReserva {

    private DataPreReserva dp = new DataPreReserva();

    public PreReserva crearPreReserva(int idUsuario, int idSalon,
                                      LocalDate fecha,
                                      LocalTime desde,
                                      LocalTime hasta) throws Exception {

        PreReserva p = new PreReserva();

        p.setIdUsuario(idUsuario);
        p.setIdSalon(idSalon);
        p.setFecha(fecha);
        p.setHoraDesde(desde);
        p.setHoraHasta(hasta);

        // Token único
        String token = UUID.randomUUID().toString();
        p.setToken(token);

        // Expira en 30 minutos
        LocalDateTime exp = LocalDateTime.now().plusMinutes(30);
        p.setExpiracion(exp);

        dp.add(p);  // inserta en la DB

        return p;
    }

    public PreReserva obtenerPorToken(String token) throws Exception {
        return dp.getByToken(token);
    }

    public void eliminarPorToken(String token) throws Exception {
        dp.deleteByToken(token);
    }
}
