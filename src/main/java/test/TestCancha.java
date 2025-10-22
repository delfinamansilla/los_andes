package test;

import data.DataCancha;
import entities.Cancha;
import java.util.LinkedList;

public class TestCancha {
	
	public static void main(String[] args) {

        DataCancha dc = new DataCancha();
        Cancha cancha = new Cancha(1, "Calle Falsa 123", "Cancha de prueba", 25.0f, true);
        
        // Add
        dc.add(cancha);
        System.out.println("Cancha agregada con ID: " + cancha.getId());

        // GetAll
        LinkedList<Cancha> canchas = dc.getAll();
        System.out.println("Total de canchas: " + canchas.size());

        // Update
        cancha.setDescripcion("Cancha actualizada");
        dc.update(cancha);
        System.out.println("Cancha actualizada.");

        // GetOne
        Cancha canchaRecuperada = dc.getOne(cancha.getId());
        System.out.println("Cancha recuperada: " + canchaRecuperada.getDescripcion());

        /* Delete
        dc.delete(cancha.getId());
        System.out.println("Cancha eliminada.");*/
        
	}

}
