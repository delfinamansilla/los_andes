package test;

import data.DataProfesor;
import entities.Profesor;
import java.util.LinkedList;

public class TestProfesor {

    public static void main(String[] args) {
        DataProfesor dataProfesor = new DataProfesor();

        // 🔹 1. Probar agregar un profesor nuevo
        Profesor nuevo = new Profesor(
                "Juan Pérez",
                "1122334455",
                "juan@mail.com"
        );
        dataProfesor.add(nuevo);
        System.out.println("Profesor agregado con ID: " + nuevo.getIdProfesor());

        // 🔹 2. Obtener todos los profesores
        LinkedList<Profesor> lista = dataProfesor.getAll();
        System.out.println("Profesores en BD:");
        for (Profesor p : lista) {
            System.out.println(p.getIdProfesor() + " - " + p.getNombreCompleto());
        }

        // 🔹 3. Buscar un profesor por ID
        Profesor encontrado = dataProfesor.getOne(nuevo.getIdProfesor());
        if (encontrado != null) {
            System.out.println("Profesor encontrado: " + encontrado.getNombreCompleto());
        } else {
            System.out.println("Profesor no encontrado");
        }

        // 🔹 4. Actualizar profesor
        if (encontrado != null) {
            encontrado.setTelefono("1199887766");
            encontrado.setMail("juan.nuevo@mail.com");
            dataProfesor.update(encontrado);
            System.out.println("Profesor actualizado");
        }

        // 🔹 5. Eliminar profesor (opcional)
        // dataProfesor.delete(encontrado.getId());
        // System.out.println("Profesor eliminado");
    }
}
