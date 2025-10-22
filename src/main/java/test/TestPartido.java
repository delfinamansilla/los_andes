package test;

import data.DataPartido;
import entities.Partido;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class TestPartido {
    public static void main(String[] args) {
        DataPartido dp = new DataPartido();

        System.out.println("Agregando nuevo partido...");
        Partido nuevoPartido = new Partido(
            LocalDate.of(2025, 11, 22),
            "Equipo Rival",
            LocalTime.of(16, 0),
            LocalTime.of(18, 0),
            "Primera División",
            500.0,
            1, 
            1  
        );
        dp.add(nuevoPartido);
        System.out.println("Partido agregado con ID: " + nuevoPartido.getId());
        System.out.println("--------------------");

        System.out.println("Listando todos los partidos:");
        LinkedList<Partido> partidos = dp.getAll();
        for (Partido p : partidos) {
            System.out.println("ID: " + p.getId() + ", Oponente: " + p.getOponente() + ", Fecha: " + p.getFecha());
        }
        System.out.println("--------------------");

        System.out.println("Buscando partido con ID: " + nuevoPartido.getId());
        Partido encontrado = dp.getById(nuevoPartido.getId());
        if (encontrado != null) {
            System.out.println("Encontrado: " + encontrado.getOponente());
        } else {
            System.out.println("Partido no encontrado.");
        }
        System.out.println("--------------------");


        if (encontrado != null) {
            System.out.println("Actualizando oponente del partido ID: " + encontrado.getId());
            encontrado.setOponente("Nuevo Equipo Oponente");
            encontrado.setPrecio_entrada(650.0);
            dp.update(encontrado);
            

            Partido actualizado = dp.getById(encontrado.getId());
            System.out.println("Oponente actualizado: " + actualizado.getOponente());
            System.out.println("Precio actualizado: " + actualizado.getPrecio_entrada());
        }
        System.out.println("--------------------");


        if (encontrado != null) {
            System.out.println("Eliminando partido ID: " + encontrado.getId());
            dp.delete(encontrado.getId());
            

            Partido eliminado = dp.getById(encontrado.getId());
            if (eliminado == null) {
                System.out.println("Partido eliminado exitosamente.");
            } else {
                System.out.println("Error al eliminar el partido.");
            }
        }
    }
}