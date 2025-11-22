package logic;

import data.DataCuota;
import data.DataPagoCuota;
import data.DataUsuario;
import entities.Cuota;
import entities.PagoCuota;
import entities.Usuario;

import java.time.LocalDate;
import java.util.LinkedList;

/**
 * Clase de lógica de negocio para la entidad PagoCuota.
 * Encapsula las validaciones y reglas de negocio.
 */
public class LogicPagoCuota {

    private final DataPagoCuota dpc;
    private final DataUsuario du;
    private final DataCuota dc;

    public LogicPagoCuota() {
        this.dpc = new DataPagoCuota();
        this.du = new DataUsuario();
        this.dc = new DataCuota();
    }

    public LinkedList<PagoCuota> getAll() {
        return dpc.getAll();
    }

    /**
     * Procesa el registro de un nuevo pago de cuota, aplicando todas las validaciones.
     * @param pc El nuevo pago a registrar.
     * @throws Exception Si alguna validación de negocio falla.
     */
    public void add(PagoCuota pc) throws Exception {
        validarPago(pc);
        dpc.add(pc);
    }

    /**
     * Elimina un registro de pago.
     * @param id el ID del pago a eliminar.
     */
    public void delete(int id) {
        dpc.delete(id);
    }

    /**
     * Método centralizado para validar las reglas de negocio de un pago de cuota.
     * @param pc El pago a validar.
     * @throws Exception Si alguna regla no se cumple.
     */
    private void validarPago(PagoCuota pc) throws Exception {
        // 1. Validación de campos obligatorios y lógicos
        if (pc.getFecha_pago() == null) {
            throw new Exception("La fecha de pago no puede estar vacía.");
        }
        if (pc.getFecha_pago().isAfter(LocalDate.now())) {
            throw new Exception("La fecha de pago no puede ser una fecha futura.");
        }

        // 2. Validación de existencia de las entidades asociadas
        Usuario usuario = du.getById(pc.getId_usuario());
        if (usuario == null) {
            throw new Exception("El usuario con ID " + pc.getId_usuario() + " no existe.");
        }

        Cuota cuota = dc.getById(pc.getId_cuota());
        if (cuota == null) {
            throw new Exception("La cuota con ID " + pc.getId_cuota() + " no existe.");
        }


        // 4. Validación de Duplicados: La cuota no debe haber sido pagada antes
        if (dpc.getByCuotaIdAndUsuarioId(pc.getId_cuota(), pc.getId_usuario()) != null) {
            throw new Exception("Esta cuota ya ha sido pagada previamente.");
        }
    }
}