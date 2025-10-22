package logic;

import java.util.LinkedList;
import data.DataHorario;
import entities.Horario;

public class LogicHorario {
    private DataHorario dh;

    public LogicHorario() {
        dh = new DataHorario();
    }

    public LinkedList<Horario> getAll() {
        return dh.getAll();
    }

    public Horario getById(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("El ID del horario no es válido.");
        }

        Horario h = dh.getById(id);
        if (h == null) {
            throw new Exception("No se encontró el horario.");
        }

        return h;
    }

    public void add(Horario h) throws Exception {
        validarHorario(h);
        dh.add(h);
    }

    public void update(Horario h) throws Exception {
        if (h.getId() <= 0) {
            throw new Exception("El ID del horario no es válido para actualizar.");
        }

        validarHorario(h);
        dh.update(h);
    }

    public void delete(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("El ID del horario no es válido.");
        }

        Horario existente = dh.getById(id);
        if (existente == null) {
            throw new Exception("No se encontró el horario a eliminar.");
        }

        dh.delete(id);
    }

    private void validarHorario(Horario h) throws Exception {
        if (h == null) {
            throw new Exception("El horario no puede ser nulo.");
        }
        if (h.getDia() == null || h.getDia().isEmpty()) {
            throw new Exception("Debe ingresar un día válido.");
        }
        if (h.getHoraDesde() == null || h.getHoraDesde().isEmpty()) {
            throw new Exception("Debe ingresar una hora de inicio válida.");
        }
        if (h.getHoraHasta() == null || h.getHoraHasta().isEmpty()) {
            throw new Exception("Debe ingresar una hora de fin válida.");
        }
        if (h.getIdActividad() <= 0) {
            throw new Exception("Debe seleccionar una actividad válida.");
        }
    }
}
