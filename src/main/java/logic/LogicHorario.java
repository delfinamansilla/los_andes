package logic;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import data.DataActividad;
import data.DataHorario;
import entities.Actividad;
import entities.Horario;

public class LogicHorario {
    private DataHorario dh;
    private DataActividad da;

    public LogicHorario() {
        dh = new DataHorario();
        da = new DataActividad();
    }

    public LinkedList<Horario> getAll() {
        return dh.getAll();
    }
    
    public List<Horario> getByActividad(int idActividad) throws SQLException {
        return dh.getByActividad(idActividad);
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
        
        Actividad act = da.getOne(h.getIdActividad());
        int idProfesor = act.getIdProfesor();
        int idCancha = act.getIdCancha();


        List<String> conflictos = dh.verificarConflictos(h, idProfesor, idCancha);

        if (!conflictos.isEmpty()) {
            throw new Exception(String.join("\n", conflictos));
        }
        
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

        if (h.getDia() == null || h.getDia().trim().isEmpty()) {
            throw new Exception("Debe ingresar un día válido.");
        }

        String[] diasValidos = {
            "lunes", "martes", "miércoles", "miercoles", "jueves", "viernes", "sábado", "sabado", "domingo"
        };

        boolean diaValido = false;
        for (String dia : diasValidos) {
            if (dia.equalsIgnoreCase(h.getDia().trim())) {
                diaValido = true;
                break;
            }
        }
        if (!diaValido) {
            throw new Exception("El día ingresado no es válido. Debe ser un día de la semana.");
        }

        if (h.getHoraDesde() == null) {
            throw new Exception("Debe ingresar una hora de inicio válida.");
        }
        if (h.getHoraHasta() == null) {
            throw new Exception("Debe ingresar una hora de fin válida.");
        }

        if (!h.getHoraDesde().isBefore(h.getHoraHasta())) {
            throw new Exception("La hora de inicio debe ser anterior a la hora de fin.");
        }

        if (h.getIdActividad() <= 0) {
            throw new Exception("Debe seleccionar una actividad válida.");
        }
    }
    
    
    public List<Horario> getOcupadosProfesor(int idProfesor) throws Exception {
        return dh.getOcupadosProfesor(idProfesor);
    }

    public List<Horario> getOcupadosCancha(int idCancha) throws Exception {
        return dh.getOcupadosCancha(idCancha);
    }

    }