package logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import data.DataPrereservaCancha;
import entities.PrereservaCancha;

public class LogicPrereservaCancha {

	private DataPrereservaCancha dp = new DataPrereservaCancha();

	public PrereservaCancha crearPreReserva(
	        int idUsuario, 
	        int idCancha,
	        LocalDate fecha,
	        LocalTime desde,
	        LocalTime hasta
	) throws Exception {

	    PrereservaCancha p = new PrereservaCancha();

	    p.setIdUsuario(idUsuario);
	    p.setIdCancha(idCancha);
	    p.setFecha(fecha);
	    p.setHoraDesde(desde);
	    p.setHoraHasta(hasta);

	    String token = UUID.randomUUID().toString();
	    p.setToken(token);

	    LocalDateTime exp = LocalDateTime.now().plusMinutes(30);
	    p.setExpiracion(exp);

	    dp.add(p);  

	    return p;
	}

	public PrereservaCancha obtenerPorToken(String token) throws Exception {
	    return dp.getByToken(token);
	}

	public void eliminarPorToken(String token) throws Exception {
	    dp.deleteByToken(token);
	}

}
