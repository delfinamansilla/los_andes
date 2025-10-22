package logic;

import java.time.LocalDate;
import java.util.LinkedList;

import data.DataMonto_cuota;
import entities.Monto_cuota;

public class LogicMonto_cuota {

    private DataMonto_cuota dataMontoCuota;

    public LogicMonto_cuota() {
        dataMontoCuota = new DataMonto_cuota();
    }

    public LinkedList<Monto_cuota> getAll() {
        return dataMontoCuota.getAll();
    }

    public Monto_cuota getByFecha(LocalDate fecha) throws Exception {
        if (fecha == null) {
            throw new Exception("La fecha no puede ser nula.");
        }

        Monto_cuota mc = dataMontoCuota.getByFecha(fecha);
        if (mc == null) {
            throw new Exception("No existe un monto registrado para la fecha: " + fecha);
        }

        return mc;
    }

    public void add(Monto_cuota mc) throws Exception {
        validarMontoCuota(mc);

        Monto_cuota existente = dataMontoCuota.getByFecha(mc.getFecha());
        if (existente != null) {
            throw new Exception("Ya existe un monto para la fecha indicada (" + mc.getFecha() + ").");
        }

        dataMontoCuota.add(mc);
    }

    public void update(Monto_cuota mc) throws Exception {
        validarMontoCuota(mc);

        Monto_cuota existente = dataMontoCuota.getByFecha(mc.getFecha());
        if (existente == null) {
            throw new Exception("No existe un monto registrado para la fecha " + mc.getFecha() + ", no se puede actualizar.");
        }

        dataMontoCuota.update(mc);
    }

    public void delete(LocalDate fecha) throws Exception {
        if (fecha == null) {
            throw new Exception("La fecha no puede ser nula.");
        }

        Monto_cuota existente = dataMontoCuota.getByFecha(fecha);
        if (existente == null) {
            throw new Exception("No existe un monto registrado para la fecha " + fecha + ".");
        }

        dataMontoCuota.delete(fecha);
    }

    private void validarMontoCuota(Monto_cuota mc) throws Exception {
        if (mc == null) {
            throw new Exception("El monto de cuota no puede ser nulo.");
        }

        if (mc.getMonto() <= 0) {
            throw new Exception("El monto debe ser mayor que cero.");
        }

        if (mc.getId_cuota() <= 0) {
            throw new Exception("El id de la cuota debe ser válido (mayor que 0).");
        }
    }
}
