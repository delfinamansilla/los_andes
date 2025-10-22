package test;

import data.DataSalon;
import entities.Salon;
import java.util.LinkedList;

public class TestSalon {
    public static void main(String[] args) {
        DataSalon dataSalon = new DataSalon();

        // 🔹 1. Agregar un nuevo salón
        Salon nuevo = new Salon(
                "Auditorio Principal",
                150,
                "Salón grande para conferencias"
        );
        dataSalon.add(nuevo);
        System.out.println("Salón agregado con ID: " + nuevo.getId());

        // 🔹 2. Obtener todos los salones
        LinkedList<Salon> lista = dataSalon.getAll();
        System.out.println("Salones en BD:");
        for (Salon s : lista) {
            System.out.println(s.getId() + " - " + s.getNombre() + " (Capacidad: " + s.getCapacidad() + ")");
        }

        // 🔹 3. Buscar un salón por ID
        int idBuscado = nuevo.getId(); // usamos el ID recién agregado
        Salon encontrado = dataSalon.getById(idBuscado);
        if (encontrado != null) {
            System.out.println("Salón encontrado: " + encontrado.getNombre() + " - " + encontrado.getDescripcion());
        } else {
            System.out.println("No se encontró el salón con ID: " + idBuscado);
        }

        // 🔹 4. Actualizar salón
        if (encontrado != null) {
            encontrado.setCapacidad(180);
            encontrado.setDescripcion("Salón grande para conferencias y talleres");
            dataSalon.update(encontrado);
            System.out.println("Salón actualizado: capacidad ahora " + encontrado.getCapacidad());
        }

        // 🔹 5. Eliminar salón
        //dataSalon.delete(encontrado.getId());
        //System.out.println("Salón eliminado con ID: " + encontrado.getId());

        // 🔹 6. Verificar lista final
        lista = dataSalon.getAll();
        System.out.println("Salones en BD después de eliminar:");
        for (Salon s : lista) {
            System.out.println(s.getId() + " - " + s.getNombre());
        }
    }
}
