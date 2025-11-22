package logic;

import data.DataAlquiler_cancha;
import data.DataCancha;
import data.DataUsuario;
import entities.Alquiler_cancha;
import entities.Alquiler_salon;
import entities.Cancha;
import entities.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class LogicAlquiler_cancha {

    private DataAlquiler_cancha dac = new DataAlquiler_cancha();
    private DataCancha dc = new DataCancha();
    private DataUsuario du = new DataUsuario(); 

    public void addAlquiler(Alquiler_cancha a) throws Exception {

        if (a.getFechaAlquiler() == null) a.setFechaAlquiler(LocalDate.now());
        if (a.getFechaAlquiler().isBefore(LocalDate.now())) {
            throw new Exception("La fecha de alquiler no puede ser pasada.");
        }

        LocalTime desde = a.getHoraDesde();
        LocalTime hasta = a.getHoraHasta();

        if (!desde.isBefore(hasta)) {
            throw new Exception("La hora de inicio debe ser anterior a la hora de fin.");
        }

        Cancha cancha = dc.getOne(a.getId_cancha());
        if (cancha == null) throw new Exception("La cancha no existe.");
        Usuario usuario = du.getById(a.getId_usuario());
        if (usuario == null) throw new Exception("El usuario no existe.");

        LinkedList<Alquiler_cancha> alquileres = dac.getAlquileresByCancha(a.getId_cancha());
        for (Alquiler_cancha alq : alquileres) {
            if (alq.getFechaAlquiler().equals(a.getFechaAlquiler())) {
            	LocalTime existDesde = alq.getHoraDesde();
            	LocalTime existHasta = alq.getHoraHasta();
                if (desde.isBefore(existHasta) && hasta.isAfter(existDesde)) {
                    throw new Exception("La cancha ya está alquilada en ese horario.");
                }
            }
        }

        dac.add(a);
    }

    public void updateAlquiler(Alquiler_cancha a) throws Exception {

        if (a.getFechaAlquiler() == null) a.setFechaAlquiler(LocalDate.now());
        LocalTime desde = a.getHoraDesde();
        LocalTime hasta = a.getHoraHasta();

        if (!desde.isBefore(hasta)) {
            throw new Exception("La hora de inicio debe ser anterior a la hora de fin.");
        }

        Cancha cancha = dc.getOne(a.getId_cancha());
        if (cancha == null) throw new Exception("La cancha no existe.");
        Usuario usuario = du.getById(a.getId_usuario());
        if (usuario == null) throw new Exception("El usuario no existe.");

        LinkedList<Alquiler_cancha> alquileres = dac.getAlquileresByCancha(a.getId_cancha());
        for (Alquiler_cancha alq : alquileres) {
            if (alq.getId() != a.getId() && alq.getFechaAlquiler().equals(a.getFechaAlquiler())) {
            	LocalTime existDesde = alq.getHoraDesde();
            	LocalTime existHasta = alq.getHoraHasta();

                if (desde.isBefore(existHasta) && hasta.isAfter(existDesde)) {
                    throw new Exception("La cancha ya está alquilada en ese horario.");
                }
            }
        }

        dac.update(a);
    }

    public LinkedList<Alquiler_cancha> getAll() {
        return dac.getAll();
    }

    public Alquiler_cancha getById(int id) {
        return dac.getById(id);
    }

    public void delete(int id) {
        dac.delete(id);
    }
    
    public LinkedList<Alquiler_cancha> getByUsuarioFuturos(int idUsuario) {
        return dac.getByUsuarioFuturos(idUsuario);
    }
    
    public LinkedList<Alquiler_cancha> getByCancha(int idCancha) {
        return dac.getByCancha(idCancha);
    }
}
