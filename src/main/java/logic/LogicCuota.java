package logic;

import data.DataCuota;
import data.DataUsuario; // Necesitamos esto para verificar que el usuario exista
import entities.Cuota;
import java.time.LocalDate;
import java.util.LinkedList;

/**
 * Clase de lógica de negocio para la entidad Cuota.
 * Encapsula las validaciones y reglas de negocio.
 */
public class LogicCuota {

    private final DataCuota dc;
    private final DataUsuario du; // Para validar que el usuario asociado exista

    public LogicCuota() {
        this.dc = new DataCuota();
        this.du = new DataUsuario(); // Asumimos que ya tienes esta clase
    }

    public LinkedList<Cuota> getAll() {
        return dc.getAll();
    }

    public Cuota getById(int id) {
        return dc.getById(id);
    }

    public void add(Cuota c) throws Exception {
        validarCuota(c);
        dc.add(c);
    }

    public void update(Cuota c) throws Exception {
        validarCuota(c);
        dc.update(c);
    }

    public void delete(int id) {
        dc.delete(id);
    }

    /**
     * Método centralizado para validar las reglas de negocio de una cuota.
     * @param c La cuota a validar.
     * @throws Exception Si alguna regla no se cumple.
     */
    private void validarCuota(Cuota c) throws Exception {
        // 1. Validación de campos obligatorios y formato
        if (c.getNro_cuota() <= 0) {
            throw new Exception("El número de cuota debe ser un entero positivo.");
        }
        if (c.getFecha_cuota() == null) {
            throw new Exception("La fecha de la cuota no puede estar vacía.");
        }
        if (c.getFecha_vencimiento() == null) {
            throw new Exception("La fecha de vencimiento no puede estar vacía.");
        }

        // 2. Validación de lógica de fechas
        if (c.getFecha_vencimiento().isBefore(c.getFecha_cuota())) {
            throw new Exception("La fecha de vencimiento no puede ser anterior a la fecha de la cuota.");
        }

        // 3. Validación de existencia del usuario asociado (tu requisito)
        // ASUNCIÓN: Tu DataUsuario tiene un método getById(id)
        if (du.getById(c.getId_usuario()) == null) {
            throw new Exception("El usuario con ID " + c.getId_usuario() + " no existe.");
        }
        
        // 4. Validación de duplicados: un usuario no puede tener dos veces la misma cuota
        Cuota cuotaExistente = dc.getByUserAndNroCuota(c.getId_usuario(), c.getNro_cuota());
        if (cuotaExistente != null && cuotaExistente.getId() != c.getId()) {
            throw new Exception("El usuario ya tiene registrada la cuota número " + c.getNro_cuota() + ".");
        }
    }
}