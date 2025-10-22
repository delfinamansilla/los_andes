package logic;

import java.time.LocalDate;
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
        validarAlquiler(a);

        if (existeConflictoPorFecha(a)) {
            throw new Exception("El salón ya tiene una reserva para esa fecha.");
        }

        da.add(a);
    }

    public void update(Alquiler_salon a) throws Exception {
        if (a.getId() <= 0) {
            throw new Exception("El ID del alquiler no es válido para actualizar.");
        }

        validarAlquiler(a);

        if (existeConflictoPorFecha(a)) {
            throw new Exception("Ya existe otra reserva en esa fecha para el mismo salón.");
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
     * Verifica conflicto SOLO por fecha.
     * Devuelve true si existe al menos otra reserva para el mismo salón
     * en la misma fecha (excluye el propio registro cuando se está actualizando).
     */
    private boolean existeConflictoPorFecha(Alquiler_salon nuevo) {
        LinkedList<Alquiler_salon> existentes = da.getBySalonYFecha(nuevo.getIdSalon(), nuevo.getFecha());

        for (Alquiler_salon a : existentes) {
            if (a.getId() != nuevo.getId()) { 
                return true;
            }
        }

        return false;
    }


    private void validarAlquiler(Alquiler_salon a) throws Exception {
        if (a == null) {
            throw new Exception("El alquiler no puede ser nulo.");
        }
        if (a.getFecha() == null) {
            throw new Exception("Debe ingresar una fecha válida.");
        }
        if (a.getFecha().isBefore(LocalDate.now())) {
            throw new Exception("La fecha no puede ser anterior a hoy.");
        }
        if (a.getIdSalon() <= 0) {
            throw new Exception("Debe seleccionar un salón válido.");
        }
        if (a.getIdUsuario() <= 0) {
            throw new Exception("Debe seleccionar un usuario válido.");
        }
    }
}


