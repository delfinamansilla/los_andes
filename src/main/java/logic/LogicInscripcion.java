package logic;

import java.time.LocalDate;

import java.util.LinkedList;
import java.util.List;
import data.*;
import entities.*;
import java.util.Map;

public class LogicInscripcion {
    private DataInscripcion dataInscripcion;
    private DataUsuario dataUsuario;
    private DataActividad dataActividad;

    public LogicInscripcion() {
        dataInscripcion = new DataInscripcion();
        dataUsuario = new DataUsuario();
        dataActividad = new DataActividad();
    }

    public void add(Inscripcion insc) throws Exception {
        validarInscripcion(insc);
        dataInscripcion.add(insc);
    }

    public void update(Inscripcion insc) throws Exception {
        validarInscripcion(insc);
        dataInscripcion.update(insc);
    }
    
    public LinkedList<Inscripcion> getByUsuario(int id_usuario) throws Exception {
        return dataInscripcion.getByUsuario(id_usuario);
    }

    public LinkedList<Map<String, Object>> getInscripcionesConDetalles(int idUsuario) throws Exception {
        return dataInscripcion.getInscripcionesConDetalles(idUsuario);
    }


    public Inscripcion getOne(int id) throws Exception {
        return dataInscripcion.getOne(id);
    }

    public List<Inscripcion> getAll() throws Exception {
        return dataInscripcion.getAll();
    }

    public void delete(int id) throws Exception {
        dataInscripcion.delete(id);
    }


    private void validarInscripcion(Inscripcion insc) throws Exception {
        // Validar fecha
        if (insc.getFechaInscripcion() == null) {
            throw new Exception("La fecha de inscripción no puede ser nula.");
        }

        // Validar usuario
        int idUsuario = insc.getIdUsuario();
        if (idUsuario <= 0) {
            throw new Exception("Debe especificarse un ID de usuario válido.");
        }

        Usuario usuario = dataUsuario.getById(idUsuario);
        if (usuario == null) {
            throw new Exception("No existe un usuario con ID " + idUsuario + ".");
        }

        if (usuario.getRol() == null || !usuario.getRol().equalsIgnoreCase("socio")) {
            throw new Exception("Solo los usuarios con rol 'socio' pueden inscribirse en actividades.");
        }

        // Validar actividad
        int idActividad = insc.getIdActividad();
        if (idActividad <= 0) {
            throw new Exception("Debe especificarse un ID de actividad válido.");
        }

        Actividad actividad = dataActividad.getOne(idActividad);
        if (actividad == null) {
            throw new Exception("No existe una actividad con ID " + idActividad + ".");
        }

        // Validar fecha de inscripción dentro del período
        LocalDate fecha = insc.getFechaInscripcion();
        if (fecha.isBefore(actividad.getInscripcionDesde()) || fecha.isAfter(actividad.getInscripcionHasta())) {
            throw new Exception("La fecha de inscripción debe estar dentro del período permitido para la actividad.");
        }

        // Validar cupo disponible
        List<Inscripcion> inscripcionesActuales = dataInscripcion.getByActividad(idActividad);
        int inscriptos = inscripcionesActuales.size();

        if (inscriptos >= actividad.getCupo()) {
            throw new Exception("No se puede inscribir: la actividad ya alcanzó su cupo máximo (" + actividad.getCupo() + ").");
        }
    }
}

