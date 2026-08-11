package logic;

import data.DataActividad;
import data.DataCancha;
import data.DataPartido;
import data.DataHorario;
import data.DataAlquiler_cancha;
import entities.Partido;
import entities.Horario;
import entities.Alquiler_cancha;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;


public class LogicPartido {

	private final DataPartido dp;
    private final DataCancha dc;
    private final DataActividad da;
    private final DataHorario dh;
    private final DataAlquiler_cancha dac;

    public LogicPartido() {
        this.dp = new DataPartido();
        this.dc = new DataCancha();
        this.da = new DataActividad();
        this.dh = new DataHorario();
        this.dac = new DataAlquiler_cancha();
    }

    /**
     * Devuelve todos los partidos de la base de datos.
     * @return una lista de todos los partidos.
     */
    public LinkedList<Partido> getAll() {
        return dp.getAll();
    }

    /**
     * Busca un partido por su ID.
     * @param id el ID del partido a buscar.
     * @return el partido encontrado, o null si no existe.
     */
    public Partido getById(int id) {
        return dp.getById(id);
    }

    /**
     * Procesa la creación de un nuevo partido, aplicando todas las validaciones.
     * @param p El nuevo partido a registrar.
     * @throws Exception Si alguna validación de negocio falla.
     */
    public void add(Partido p) throws Exception {
        validarPartido(p);
        dp.add(p);
    }

    /**
     * Procesa la actualización de un partido existente.
     * @param p El partido con los datos a modificar.
     * @throws Exception Si alguna validación de negocio falla.
     */
    public void update(Partido p) throws Exception {
        validarPartido(p);
        dp.update(p);
    }

    /**
     * Elimina un partido por su ID.
     * @param id el ID del partido a eliminar.
     */
    public void delete(int id) {
        dp.delete(id);
    }

    /**
     * Método centralizado de validaciones para la entidad Partido.
     * Lanza una excepción si alguna regla no se cumple.
     * @param p El partido a validar.
     * @throws Exception con el mensaje del error de validación.
     */
    private void validarPartido(Partido p) throws Exception {
    	if (p.getFecha() == null || 
                p.getOponente() == null || p.getOponente().trim().isEmpty() ||
                p.getHora_desde() == null || p.getHora_hasta() == null ||
                p.getCategoria() == null || p.getCategoria().trim().isEmpty() ||
                p.getPrecio_entrada() == null ||
                p.getId_actividad() <= 0) {
                throw new Exception("Debe completar todos los campos");
            }

            if (p.getId() == 0 && p.getFecha().isBefore(LocalDate.now())) {
                throw new Exception("No se pueden registrar partidos en fechas pasadas");
            }

            if (!p.getHora_desde().isBefore(p.getHora_hasta())) {
                throw new Exception("La hora de finalización debe ser posterior a la de inicio");
            }
            if (p.getResultado() != null && !p.getResultado().trim().isEmpty()) {

                if (!p.getResultado().matches("\\d+\\s*-\\s*\\d+")) {
                    throw new Exception("Formato de resultado no reconocido");
                }
            }
            
            validarDisponibilidadCancha(p);
    }

    /**
     * Verifica que no exista otro partido en la misma cancha, fecha y hora.
     * @param partidoAValidar El partido que se quiere agendar o modificar.
     * @throws Exception si la cancha ya está ocupada en ese horario.
     */
    private void validarDisponibilidadCancha(Partido p) throws Exception {
        if (p.getId_cancha() == null) return; 

        LocalTime inicioN = p.getHora_desde();
        LocalTime finN = p.getHora_hasta();

        LinkedList<Partido> partidos = dp.getByCanchaAndFecha(p.getId_cancha(), p.getFecha());
        for (Partido existente : partidos) {
            if (existente.getId() != p.getId()) {
                if (inicioN.isBefore(existente.getHora_hasta()) && finN.isAfter(existente.getHora_desde())) {
                    throw new Exception("La cancha seleccionada ya tiene un evento asignado en ese horario");
                }
            }
        }

        LinkedList<Alquiler_cancha> alquileres = dac.getByCancha(p.getId_cancha());
        for (Alquiler_cancha alq : alquileres) {
            if (alq.getFechaAlquiler().equals(p.getFecha())) {
                if (inicioN.isBefore(alq.getHoraHasta()) && finN.isAfter(alq.getHoraDesde())) {
                    throw new Exception("La cancha seleccionada ya tiene un evento asignado en ese horario");
                }
            }
        }

        List<Horario> horariosClases = dh.getOcupadosCancha(p.getId_cancha());
        String diaSemana = getDiaNombre(p.getFecha());
        
        for (Horario h : horariosClases) {
            if (h.getDia().equalsIgnoreCase(diaSemana)) {
                if (inicioN.isBefore(h.getHoraHasta()) && finN.isAfter(h.getHoraDesde())) {
                    throw new Exception("La cancha seleccionada ya tiene un evento asignado en ese horario");
                }
            }
        }
    }

    private String getDiaNombre(LocalDate fecha) {
        switch (fecha.getDayOfWeek()) {
            case MONDAY:    return "Lunes";
            case TUESDAY:   return "Martes";
            case WEDNESDAY: return "Miercoles";
            case THURSDAY:  return "Jueves";
            case FRIDAY:    return "Viernes";
            case SATURDAY:  return "Sabado";
            case SUNDAY:    return "Domingo";
            default:        return "";
        }
    }
    
    public LinkedList<Partido> getByFechaRango(LocalDate desde, LocalDate hasta) {
        return dp.getByFechaRango(desde, hasta);
    }

}