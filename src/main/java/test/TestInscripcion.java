package test;

import data.DataInscripcion;
import entities.Inscripcion;
import java.time.LocalDate;
import java.util.LinkedList;

public class TestInscripcion {

    public static void main(String[] args) {
        DataInscripcion dataInscripcion = new DataInscripcion();

        // 🔹 1. Agregar nueva inscripción usando IDs directos
        Inscripcion nueva = new Inscripcion(
                LocalDate.now(),
                2,  // id_usuario
                1   // id_actividad
        );
        dataInscripcion.add(nueva);
        System.out.println("Inscripción agregada con ID: " + nueva.getIdInscripcion());

        // 🔹 2. Obtener todas las inscripciones
        LinkedList<Inscripcion> lista = dataInscripcion.getAll();
        System.out.println("Inscripciones en BD:");
        for (Inscripcion i : lista) {
            System.out.println(i.getIdInscripcion() + " - Usuario ID: " + i.getIdUsuario() + ", Actividad ID: " + i.getIdActividad());
        }

        // 🔹 3. Buscar inscripción por ID
        Inscripcion encontrada = dataInscripcion.getOne(nueva.getIdInscripcion());
        if (encontrada != null) {
            System.out.println("Inscripción encontrada: Usuario ID " + encontrada.getIdUsuario() +
                               ", Actividad ID " + encontrada.getIdActividad());
        }

        // 🔹 4. Actualizar inscripción
        encontrada.setFechaInscripcion(LocalDate.now().plusDays(1));
        dataInscripcion.update(encontrada);
        System.out.println("Inscripción actualizada con nueva fecha: " + encontrada.getFechaInscripcion());

        // 🔹 5. Eliminar inscripción (opcional)
        // dataInscripcion.delete(encontrada.getId());
        // System.out.println("Inscripción eliminada");
    }
}
