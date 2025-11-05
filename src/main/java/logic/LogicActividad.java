package logic;

import java.time.LocalDate;

import java.util.Map;
import data.*;
import entities.*;
import java.util.LinkedList;

public class LogicActividad {
    private DataActividad dataActividad;
    private DataProfesor dataProfesor;
    private DataCancha dataCancha;

    public LogicActividad() {
        dataActividad = new DataActividad();
        dataProfesor = new DataProfesor();
        dataCancha = new DataCancha();
    }

    public void add(Actividad act) throws Exception {
        validarActividad(act);
        dataActividad.add(act);
    }
    
    public LinkedList<Map<String, Object>> getActividadesConDetalles() throws Exception {
        return dataActividad.getActividadesConDetalles();
    }


    public void update(Actividad act) throws Exception {
        validarActividad(act);
        dataActividad.update(act);
    }

    public Actividad getOne(int id) throws Exception {
        return dataActividad.getOne(id);
    }

    public java.util.List<Actividad> getAll() throws Exception {
        return dataActividad.getAll();
    }

    public void delete(int id) throws Exception {
        dataActividad.delete(id);
    }

    private void validarActividad(Actividad act) throws Exception {
        // Validar nombre
        if (act.getNombre() == null || act.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la actividad no puede estar vacío.");
        }

        // Validar cupo
        if (act.getCupo() <= 0) {
            throw new Exception("El cupo debe ser mayor que cero.");
        }

        // Validar fechas
        LocalDate desde = act.getInscripcionDesde();
        LocalDate hasta = act.getInscripcionHasta();
        if (desde == null || hasta == null) {
            throw new Exception("Las fechas de inscripción no pueden ser nulas.");
        }
        if (hasta.isBefore(desde)) {
            throw new Exception("La fecha 'hasta' debe ser posterior a la fecha 'desde'.");
        }

        // Validar ID de profesor
        int idProfesor = act.getIdProfesor();
        if (idProfesor <= 0) {
            throw new Exception("Debe especificarse un ID de profesor válido.");
        }
        if (dataProfesor.getOne(idProfesor) == null) {
            throw new Exception("No existe un profesor con ID " + idProfesor + ".");
        }

        // Validar ID de cancha
        int idCancha = act.getIdCancha();
        if (idCancha <= 0) {
            throw new Exception("Debe especificarse un ID de cancha válido.");
        }
        if (dataCancha.getOne(idCancha) == null) {
            throw new Exception("No existe una cancha con ID " + idCancha + ".");
        }
    }
}

