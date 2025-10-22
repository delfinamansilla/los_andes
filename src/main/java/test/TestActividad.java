package test;

import data.DataActividad;
import entities.Actividad;
import java.time.LocalDate;
import java.util.LinkedList;

public class TestActividad {

    public static void main(String[] args) {
        DataActividad dataActividad = new DataActividad();

        // 🔹 1. Agregar nueva actividad usando IDs directos
        Actividad nueva = new Actividad(
                "Tenis avanzado",
                10,
                "Clase para nivel avanzado",
                LocalDate.of(2025, 10, 23),
                LocalDate.of(2025, 11, 23),
                1,  // id_profesor
                1   // id_cancha
        );
        dataActividad.add(nueva);
        System.out.println("Actividad agregada con ID: " + nueva.getIdActividad());

        // 🔹 2. Obtener todas las actividades
        LinkedList<Actividad> lista = dataActividad.getAll();
        System.out.println("Actividades en BD:");
        for (Actividad a : lista) {
            System.out.println(a.getIdActividad() + " - " + a.getNombre());
        }

        // 🔹 3. Buscar actividad por ID
        Actividad encontrada = dataActividad.getOne(nueva.getIdActividad());
        if (encontrada != null) {
            System.out.println("Actividad encontrada: " + encontrada.getNombre());
        }

        // 🔹 4. Actualizar actividad
        encontrada.setCupo(12);
        dataActividad.update(encontrada);
        System.out.println("Actividad actualizada con nuevo cupo: " + encontrada.getCupo());

        // 🔹 5. Eliminar actividad (opcional)
        // dataActividad.delete(encontrada.getId());
        // System.out.println("Actividad eliminada");
    }
}
