package logic;

import data.*;
import entities.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class LogicInformeOcupacion {
    private DataAlquiler_cancha dac = new DataAlquiler_cancha();
    private DataAlquiler_salon das = new DataAlquiler_salon();
    private DataCancha dc = new DataCancha();
    private DataSalon ds = new DataSalon();
    private DataUsuario du = new DataUsuario();

    public LinkedList<InformeOcupacion> generarInforme(LocalDate desde, LocalDate hasta) { 
        LinkedList<InformeOcupacion> listaFinal = new LinkedList<>();
        LinkedList<Alquiler_cancha> alquileresC = dac.getAll();
        for (Alquiler_cancha ac : alquileresC) {
        	LocalDate fecha = ac.getFechaAlquiler();

        	if (!fecha.isBefore(desde) && !fecha.isAfter(hasta)) {
                InformeOcupacion io = new InformeOcupacion();
                io.setTipoRecurso("Cancha");
                
                Cancha c = dc.getOne(ac.getId_cancha());
                io.setNombreRecurso(c != null ? "Cancha " + c.getNro_cancha() : "Cancha eliminada");
                
                Usuario u = du.getById(ac.getId_usuario());
                io.setNombreUsuario(u != null ? u.getNombreCompleto() : "Usuario desconocido");
                
                io.setFecha(ac.getFechaAlquiler().toString());
                io.setHorario(ac.getHoraDesde() + " - " + ac.getHoraHasta());
                
                listaFinal.add(io);
            }
        }

        LinkedList<Alquiler_salon> alquileresS = das.getAll();
        for (Alquiler_salon as : alquileresS) {
        	LocalDate fecha = as.getFecha();

        	if (!fecha.isBefore(desde) && !fecha.isAfter(hasta)) {
                InformeOcupacion io = new InformeOcupacion();
                io.setTipoRecurso("Salón");
                
                Salon s = ds.getById(as.getIdSalon());
                io.setNombreRecurso(s != null ? s.getNombre() : "Salón eliminado");
                
                Usuario u = du.getById(as.getIdUsuario());
                io.setNombreUsuario(u != null ? u.getNombreCompleto() : "Usuario desconocido");
                
                io.setFecha(as.getFecha().toString());
                io.setHorario(as.getHoraDesde() + " - " + as.getHoraHasta());
                
                listaFinal.add(io);
            }
        }

        return listaFinal;
    }
}