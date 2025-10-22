package test;

import data.DataAlquiler_cancha;
import entities.Alquiler_cancha;

import java.time.LocalDate;
import java.util.LinkedList;

public class TestAlquiler_cancha {
	
	public static void main(String[] args) {
	
		DataAlquiler_cancha dac = new DataAlquiler_cancha();
	    Alquiler_cancha alquiler = new Alquiler_cancha();
	    alquiler.setFechaAlquiler(LocalDate.now());
	    alquiler.setHoraDesde("10:00");
	    alquiler.setHoraHasta("12:00");
	    alquiler.setId_cancha(2);
	    alquiler.setId_usuario(2);
	
	    // Add
	    dac.add(alquiler);
	    System.out.println("Alquiler agregado con ID: " + alquiler.getId());
	
	    // GetAll
	    LinkedList<Alquiler_cancha> alquileres = dac.getAll();
	    System.out.println("Total de alquileres: " + alquileres.size());
	
	    // Update
	    alquiler.setHoraHasta("13:00");
	    dac.update(alquiler);
	    System.out.println("Alquiler actualizado.");
	
	    // GetById
	    Alquiler_cancha alquilerRec = dac.getById(alquiler.getId());
	    System.out.println("Alquiler recuperado, hora_hasta: " + alquilerRec.getHoraHasta());
	
	    /*// Delete
	    dac.delete(alquiler.getId());
	    System.out.println("Alquiler eliminado.");*/
    
	}

}