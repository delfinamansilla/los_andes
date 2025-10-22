package test;

import data.DataCuota;
import data.DataPagoCuota;
import data.DataUsuario;
import entities.Cuota;
import entities.PagoCuota;
import entities.Usuario;

import java.time.LocalDate;

public class TestPagoCuota {

    public static void main(String[] args) {
        DataUsuario du = new DataUsuario();
        DataCuota dc = new DataCuota();
        DataPagoCuota dpc = new DataPagoCuota();

        System.out.println("--- Preparando datos para el test ---");

        Usuario usuarioPrueba = new Usuario("Usuario de Prueba Pago", "00111222", "123456", "pago@test.com", LocalDate.now(), "pass", true, "socio", 999);
        du.add(usuarioPrueba);
        System.out.println("Usuario temporal creado con ID: " + usuarioPrueba.getIdUsuario());


        Cuota cuotaPrueba = new Cuota(1, LocalDate.now(), LocalDate.now().plusMonths(1));
        dc.add(cuotaPrueba);
        System.out.println("Cuota temporal creada con ID: " + cuotaPrueba.getId());



        System.out.println("\n--- Agregando nuevo PagoCuota ---");
        PagoCuota nuevoPago = new PagoCuota();
        nuevoPago.setFecha_pago(LocalDate.now());
        nuevoPago.setId_usuario(usuarioPrueba.getIdUsuario());
        nuevoPago.setId_cuota(cuotaPrueba.getId());           
        
        dpc.add(nuevoPago);
        System.out.println("Pago de cuota agregado con ID: " + nuevoPago.getId());

        System.out.println("\n--- Listando todos los pagos ---");
        for (PagoCuota p : dpc.getAll()) {
            System.out.println("ID Pago: " + p.getId() + ", ID Usuario: " + p.getId_usuario() + ", ID Cuota: " + p.getId_cuota());
        }

        System.out.println("\n--- Eliminando el pago de cuota ---");
        dpc.delete(nuevoPago.getId());
        System.out.println("Pago con ID " + nuevoPago.getId() + " eliminado.");

        System.out.println("\n--- Limpiando datos de prueba ---");
        du.delete(usuarioPrueba.getIdUsuario());
        System.out.println("Usuario temporal eliminado.");
        dc.delete(cuotaPrueba.getId());
        System.out.println("Cuota temporal eliminada.");

    }
}