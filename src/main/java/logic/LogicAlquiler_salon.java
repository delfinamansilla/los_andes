package logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

import data.DataAlquiler_salon;
import entities.Alquiler_salon;

public class LogicAlquiler_salon {
    private DataAlquiler_salon da;

    public LogicAlquiler_salon() {
        da = new DataAlquiler_salon();
    }

    public LinkedList<Alquiler_salon> getAll() {
        return da.getAll();
    }

    public Alquiler_salon getById(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("El ID de alquiler no es válido.");
        }

        Alquiler_salon a = da.getById(id);
        if (a == null) {
            throw new Exception("No se encontró el alquiler.");
        }

        return a;
    }

    public void add(Alquiler_salon a) throws Exception {
        LinkedList<Alquiler_salon> existentes =
            da.getBySalonYFecha(a.getIdSalon(), a.getFecha());

        for (Alquiler_salon ex : existentes) {
        	boolean solapa = 
        	        a.getHoraDesde().isBefore(ex.getHoraHasta()) &&
        	        a.getHoraHasta().isAfter(ex.getHoraDesde());


            if (solapa) {
                throw new Exception("El horario solicitado ya está reservado");
            }
        }

        da.add(a);
    }


    public void update(Alquiler_salon a) throws Exception {
        if (a.getId() <= 0) {
            throw new Exception("El ID del alquiler no es válido para actualizar.");
        }

        validarAlquiler(a);

        if (existeConflictoHorario(a)) {
            throw new Exception("Ya existe otra reserva en ese horario para el mismo salón.");
        }

        da.update(a);
    }

    public void delete(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("El ID del alquiler no es válido.");
        }

        Alquiler_salon existente = da.getById(id);
        if (existente == null) {
            throw new Exception("No se encontró el alquiler a eliminar.");
        }

        da.delete(id);
    }

    /**
     * Verifica conflicto por HORA (no solo fecha).
     */
    private boolean existeConflictoHorario(Alquiler_salon nuevo) {
        LinkedList<Alquiler_salon> existentes =
            da.getBySalonYFecha(nuevo.getIdSalon(), nuevo.getFecha());

        for (Alquiler_salon a : existentes) {
            if (a.getId() == nuevo.getId()) continue;

            boolean seSuperponen =
                !(nuevo.getHoraHasta().isBefore(a.getHoraDesde()) ||
                  nuevo.getHoraDesde().isAfter(a.getHoraHasta()));

            if (seSuperponen) return true;
        }

        return false;
    }
 // En LogicAlquiler_salon.java, agrega este método:
    public LinkedList<Alquiler_salon> getBySalon(int idSalon) {
        return da.getBySalon(idSalon);
    }
    
 // Agrega este método en LogicAlquiler_salon.java

    public LinkedList<Alquiler_salon> getByUsuarioFuturos(int idUsuario) {
        return da.getByUsuarioFuturos(idUsuario);
    }

    /**
     * Validaciones completas del alquiler.
     */
    private void validarAlquiler(Alquiler_salon a) throws Exception {
        if (a == null) throw new Exception("El alquiler no puede ser nulo.");

        if (a.getFecha() == null)
            throw new Exception("Debe ingresar una fecha válida.");

        if (a.getFecha().isBefore(LocalDate.now()))
            throw new Exception("La fecha no puede ser anterior a hoy.");

        if (a.getHoraDesde() == null || a.getHoraHasta() == null)
            throw new Exception("Debe ingresar ambos horarios.");

        if (!a.getHoraDesde().isBefore(a.getHoraHasta()))
            throw new Exception("La hora de inicio debe ser anterior a la de fin.");

        // Validar rangos de 4 horas (opcional)
     // Dif exacta en minutos
        long minutos = java.time.Duration.between(a.getHoraDesde(), a.getHoraHasta()).toMinutes();

        // Permitimos 4 horas exactas = 240 min
        // y permitimos 239 min en caso de usar 23:59 (tolerancia)
        if (minutos < 239 || minutos > 240) {
            throw new Exception("Los alquileres deben ser de exactamente 4 horas.");
        }


        if (a.getIdSalon() <= 0)
            throw new Exception("Debe seleccionar un salón válido.");

        if (a.getIdUsuario() <= 0)
            throw new Exception("Debe seleccionar un usuario válido.");
    }
}
