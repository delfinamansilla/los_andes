package test;

import data.DataHorario;
import entities.Horario;
import java.util.LinkedList;

public class TestHorario {
    public static void main(String[] args) {
        DataHorario dataHorario = new DataHorario();

        // 🔹 1. Probar agregar un horario nuevo
        Horario nuevo = new Horario(
                "Lunes",
                "08:00",
                "10:00",
                1 // idActividad de ejemplo, asegúrate que exista en la tabla actividad
        );
        dataHorario.add(nuevo);
        System.out.println("Horario agregado con ID: " + nuevo.getId());

        // 🔹 2. Obtener todos los horarios
        LinkedList<Horario> lista = dataHorario.getAll();
        System.out.println("Horarios en BD:");
        for (Horario h : lista) {
            System.out.println(h.getId() + " - " + h.getDia() + " de " + h.getHoraDesde() + " a " + h.getHoraHasta() + " (Actividad ID: " + h.getIdActividad() + ")");
        }

        // 🔹 3. Buscar un horario por ID
        int idBuscado = nuevo.getId(); // usamos el ID recién agregado
        Horario encontrado = dataHorario.getById(idBuscado);
        if (encontrado != null) {
            System.out.println("Horario encontrado: " + encontrado.getDia() + " de " + encontrado.getHoraDesde() + " a " + encontrado.getHoraHasta());
        } else {
            System.out.println("No se encontró el horario con ID: " + idBuscado);
        }

        // 🔹 4. Actualizar horario
        if (encontrado != null) {
            encontrado.setHoraHasta("11:00");
            dataHorario.update(encontrado);
            System.out.println("Horario actualizado: ahora termina a " + encontrado.getHoraHasta());
        }

        // 🔹 5. (Opcional) Eliminar horario
        // dataHorario.delete(encontrado.getId());
        // System.out.println("Horario eliminado");
    }
}
