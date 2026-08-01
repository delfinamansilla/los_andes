package logic;

import java.util.LinkedList;
import data.DataConfiguracion;
import entities.Configuracion;
import data.DataUsuario; 

public class LogicConfiguracion {
    private DataConfiguracion dc = new DataConfiguracion();
    private DataUsuario du = new DataUsuario();
    
    public LinkedList<Configuracion> getAll() { return dc.getAll(); }

    public int getCupoActual() {
        String valor = dc.getValorActual("CUPO_MAX_SOCIOS");
        return (valor != null) ? Integer.parseInt(valor) : 100; 
    }

    public void setCupo(int nuevoCupo) throws Exception {
        if (nuevoCupo < 0) throw new Exception("El cupo no puede ser negativo.");

        int sociosActivos = du.getCantSociosActivos();
        
        if (nuevoCupo < sociosActivos) {
            throw new Exception("No puedes bajar el cupo a " + nuevoCupo + 
                " porque actualmente hay " + sociosActivos + " socios activos. " +
                "Debes dar de baja socios antes de reducir el cupo a ese nivel.");
        }

        Configuracion c = new Configuracion();
        c.setClave("CUPO_MAX_SOCIOS");
        c.setValor(String.valueOf(nuevoCupo));
        dc.add(c);
    }

    public void delete(int id) { dc.delete(id); }
}