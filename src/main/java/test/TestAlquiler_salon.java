package test;

import data.DataAlquiler_salon;
import entities.Alquiler_salon;
import java.time.LocalDate;
import java.util.LinkedList;

public class TestAlquiler_salon {
    public static void main(String[] args) {
        DataAlquiler_salon dataAlquiler = new DataAlquiler_salon();

        // 🔹 1. Agregar un nuevo alquiler
        Alquiler_salon nuevo = new Alquiler_salon(
                LocalDate.of(2025, 10, 25), // fecha de ejemplo
                "09:00",
                "11:00",
                2, // idSalon (asegurate que exista en BD)
                2  // idUsuario (ya existe según tu comentario)
        );
        dataAlquiler.add(nuevo);
        System.out.println("Alquiler agregado con ID: " + nuevo.getId());

        // 🔹 2. Listar todos los alquileres
        LinkedList<Alquiler_salon> lista = dataAlquiler.getAll();
        System.out.println("Alquileres en BD:");
        for (Alquiler_salon a : lista) {
            System.out.println(a.getId() + " - " + a.getFecha() + " de " + a.getHoraDesde() + " a " + a.getHoraHasta() +
                    " (Salon: " + a.getIdSalon() + ", Usuario: " + a.getIdUsuario() + ")");
        }

        // 🔹 3. Buscar un alquiler por ID
        int idBuscado = nuevo.getId();
        Alquiler_salon encontrado = dataAlquiler.getById(idBuscado);
        if (encontrado != null) {
            System.out.println("Alquiler encontrado: " + encontrado.getFecha() + " de " + encontrado.getHoraDesde() +
                    " a " + encontrado.getHoraHasta());
        } else {
            System.out.println("No se encontró el alquiler con ID: " + idBuscado);
        }

        // 🔹 4. Actualizar alquiler
        if (encontrado != null) {
            encontrado.setHoraHasta("12:00");
            dataAlquiler.update(encontrado);
            System.out.println("Alquiler actualizado: horaHasta ahora " + encontrado.getHoraHasta());
        }

        // 🔹 5. Listar alquileres de un salón específico
        LinkedList<Alquiler_salon> delSalon = dataAlquiler.getBySalon(1);
        System.out.println("Alquileres del salón 1:");
        for (Alquiler_salon a : delSalon) {
            System.out.println(a.getId() + " - " + a.getFecha() + " de " + a.getHoraDesde() + " a " + a.getHoraHasta());
        }

        // 🔹 6. Eliminar alquiler
        //dataAlquiler.delete(encontrado.getId());
        //System.out.println("Alquiler eliminado con ID: " + encontrado.getId());

        // 🔹 7. Verificar lista final
        lista = dataAlquiler.getAll();
        System.out.println("Alquileres en BD después de eliminar:");
        for (Alquiler_salon a : lista) {
            System.out.println(a.getId() + " - " + a.getFecha() + " de " + a.getHoraDesde() + " a " + a.getHoraHasta());
        }
    }
}
