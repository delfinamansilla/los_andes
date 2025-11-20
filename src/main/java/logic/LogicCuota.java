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

 // En LogicCuota.java

    public void add(Cuota c) throws Exception {
        // 1. Validaciones básicas (que ya tenías)
        validarCuota(c);

        // 2. NUEVA VALIDACIÓN: Verificar si ya existe el mes
        Cuota existente = dc.getByNroCuota(c.getNro_cuota());
        
        if (existente != null) {
            // Si entra aquí, es porque YA existe una cuota con ese número
            throw new Exception("⚠️ Ya existe una cuota registrada para el mes " + c.getNro_cuota());
        }

        // 3. Si pasa la validación, guarda
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
    }
}