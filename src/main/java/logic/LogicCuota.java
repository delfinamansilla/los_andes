package logic;

import data.DataCuota;
import data.DataUsuario; // Necesitamos esto para verificar que el usuario exista
import entities.Cuota;
import java.time.LocalDate;
import java.util.LinkedList;

public class LogicCuota {

    private final DataCuota dc;
    private final DataUsuario du; 

    public LogicCuota() {
        this.dc = new DataCuota();
        this.du = new DataUsuario();
    }

    public LinkedList<Cuota> getAll() {
        return dc.getAll();
    }

    public Cuota getById(int id) {
        return dc.getById(id);
    }

    public void add(Cuota c) throws Exception {
        validarCuota(c);

        int anioActual = LocalDate.now().getYear();
        int periodoCompleto = anioActual * 100 + c.getNro_cuota();
        c.setNro_cuota(periodoCompleto); 

        Cuota existente = dc.getByNroCuota(c.getNro_cuota());
        
        if (existente != null) {
            throw new Exception("⚠️ Ya existe una cuota registrada para este período.");
        }

        dc.add(c);
    }

    public void update(Cuota c) throws Exception {
        int anioActual = LocalDate.now().getYear();
        int periodoCompleto = anioActual * 100 + c.getNro_cuota();
        c.setNro_cuota(periodoCompleto);

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
        if (c.getNro_cuota() <= 0) {
            throw new Exception("El número de cuota debe ser un entero positivo.");
        }
        if (c.getFecha_cuota() == null) {
            throw new Exception("La fecha de la cuota no puede estar vacía.");
        }
        if (c.getFecha_vencimiento() == null) {
            throw new Exception("La fecha de vencimiento no puede estar vacía.");
        }
        
        if (c.getFecha_vencimiento().isBefore(c.getFecha_cuota())) {
            throw new Exception("La fecha de vencimiento no puede ser anterior a la fecha de la cuota.");
        }
    }
}