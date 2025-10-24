package test;

import data.DataCuota;
import entities.Cuota;
import java.time.LocalDate;
import java.util.LinkedList;

public class TestCuota {
    public static void main(String[] args) {
/*  
       DataCuota dc = new DataCuota();
 
        Cuota cuotaPrueba = null;

        System.out.println("--- Agregando nueva cuota ---");
        Cuota nuevaCuota = new Cuota(1, LocalDate.now(), LocalDate.now().plusMonths(1));
        dc.add(nuevaCuota);
        System.out.println("Cuota agregada con ID: " + nuevaCuota.getId());

        System.out.println("\n--- Listando todas las cuotas ---");
        LinkedList<Cuota> cuotas = dc.getAll();
        for (Cuota c : cuotas) {
            System.out.println("ID: " + c.getId() + ", Nro: " + c.getNro_cuota() + ", Vencimiento: " + c.getFecha_vencimiento());
        }

        System.out.println("\n--- Buscando cuota con ID: " + nuevaCuota.getId() + " ---");
        cuotaPrueba = dc.getById(nuevaCuota.getId());
        if (cuotaPrueba != null) {
            System.out.println("Encontrada: Cuota Nro " + cuotaPrueba.getNro_cuota());
        } else {
            System.out.println("Cuota no encontrada.");
        }

        if (cuotaPrueba != null) {
            System.out.println("\n--- Actualizando fecha de vencimiento ---");
            cuotaPrueba.setFecha_vencimiento(LocalDate.now().plusDays(45));
            dc.update(cuotaPrueba);
            
            Cuota actualizada = dc.getById(cuotaPrueba.getId());
            System.out.println("Fecha de vencimiento actualizada a: " + actualizada.getFecha_vencimiento());
        }

        if (cuotaPrueba != null) {
            System.out.println("\n--- Eliminando cuota ID: " + cuotaPrueba.getId() + " ---");
            dc.delete(cuotaPrueba.getId());
            
            Cuota eliminada = dc.getById(cuotaPrueba.getId());
            if (eliminada == null) {
                System.out.println("Cuota eliminada exitosamente.");
            } else {
                System.out.println("Error al eliminar la cuota.");
            }
        }*/
    }
}