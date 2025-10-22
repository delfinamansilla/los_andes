package logic;

import data.DataActividad;
import data.DataCancha;
import data.DataPartido;
import entities.Partido;
import entities.Actividad; 

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

/**
 * Clase de lógica de negocio para la entidad Partido.
 * Encapsula las validaciones y reglas de negocio antes de interactuar
 * con la capa de datos.
 */
public class LogicPartido {

    private final DataPartido dp;
    private final DataCancha dc;
    private final DataActividad da;

    public LogicPartido() {
        this.dp = new DataPartido();
        this.dc = new DataCancha();
        this.da = new DataActividad();
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
        // 1. Validación de campos básicos no nulos o vacíos
        if (p.getFecha() == null) {
            throw new Exception("La fecha no puede estar vacía.");
        }
        if (p.getOponente() == null || p.getOponente().trim().isEmpty()) {
            throw new Exception("El oponente no puede estar vacío.");
        }
        if (p.getHora_desde() == null || p.getHora_hasta() == null) {
            throw new Exception("Las horas de inicio y fin no pueden estar vacías.");
        }
        if (p.getCategoria() == null || p.getCategoria().trim().isEmpty()) {
            throw new Exception("La categoría no puede estar vacía.");
        }
        if (p.getPrecio_entrada() == null || p.getPrecio_entrada() < 0) {
            throw new Exception("El precio de la entrada debe ser un valor positivo o cero.");
        }

        // 2. Validación de reglas de negocio temporales
        if (p.getId() == 0 && p.getFecha().isBefore(LocalDate.now())) {
            // Solo validamos la fecha pasada para partidos nuevos, no para ver el historial
            throw new Exception("La fecha del partido no puede ser en el pasado.");
        }
        if (p.getHora_desde().isAfter(p.getHora_hasta()) || p.getHora_desde().equals(p.getHora_hasta())) {
            throw new Exception("La hora de inicio debe ser anterior a la hora de finalización.");
        }
        
        // 3. Validación de existencia de entidades relacionadas (Foreign Keys)
        if (dc.getOne(p.getId_cancha()) == null) {
            throw new Exception("La cancha seleccionada no existe.");
        }
        if (da.getOne(p.getId_actividad()) == null) {
            throw new Exception("La actividad seleccionada no existe.");
        }

        validarDisponibilidadCancha(p);
    }

    /**
     * Verifica que no exista otro partido en la misma cancha, fecha y hora.
     * @param partidoAValidar El partido que se quiere agendar o modificar.
     * @throws Exception si la cancha ya está ocupada en ese horario.
     */
    private void validarDisponibilidadCancha(Partido partidoAValidar) throws Exception {
        LinkedList<Partido> partidosEnMismaCanchaYFecha = dp.getByCanchaAndFecha(
            partidoAValidar.getId_cancha(),
            partidoAValidar.getFecha()
        );

        LocalTime inicioNuevo = partidoAValidar.getHora_desde();
        LocalTime finNuevo = partidoAValidar.getHora_hasta();

        for (Partido partidoExistente : partidosEnMismaCanchaYFecha) {
            // Si estamos actualizando un partido, no debemos compararlo consigo mismo.
            if (partidoExistente.getId() == partidoAValidar.getId()) {
                continue; 
            }

            LocalTime inicioExistente = partidoExistente.getHora_desde();
            LocalTime finExistente = partidoExistente.getHora_hasta();

            if (inicioNuevo.isBefore(finExistente) && finNuevo.isAfter(inicioExistente)) {
                throw new Exception(
                    "Conflicto de horario. La cancha ya está reservada de " +
                    inicioExistente + " a " + finExistente + " en esa fecha."
                );
            }
        }
    }
}