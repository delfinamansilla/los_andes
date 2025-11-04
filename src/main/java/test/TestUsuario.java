package test;

import data.DataUsuario;
import entities.Usuario;
import java.time.LocalDate;
import java.util.LinkedList;

public class TestUsuario {
    public static void main(String[] args) {
       DataUsuario dataUsuario = new DataUsuario();

        /*// 🔹 1. Probar agregar un usuario nuevo
        Usuario nuevo = new Usuario(
                "Juan Pérez",
                "12345678",
                "1122334455",
                "juan@mail.com",
                LocalDate.of(1990, 5, 15),
                "1234",
                true,
                "socio",
                1
        );
        dataUsuario.add(nuevo);
        System.out.println("Usuario agregado con ID: " + nuevo.getIdUsuario());
*/
        // 🔹 2. Obtener todos los usuarios
        LinkedList<Usuario> lista = dataUsuario.getAll();
        System.out.println("Usuarios en BD:");
        for (Usuario u : lista) {
            System.out.println(u.getIdUsuario() + " - " + u.getNombreCompleto());
        }

        /*// 🔹 3. Buscar un usuario por mail y clave
        Usuario login = new Usuario();
        login.setMail("juan@mail.com");
        login.setContrasenia("1234");
        Usuario encontrado = dataUsuario.getByMailAndContrasenia(login);
        if (encontrado != null) {
            System.out.println("Login exitoso: " + encontrado.getNombreCompleto());
        } else {
            System.out.println("Login fallido");
        }

        // 🔹 4. Actualizar usuario
        if (encontrado != null) {
            encontrado.setTelefono("1199887766");
            dataUsuario.update(encontrado);
            System.out.println("Usuario actualizado");
        }*/
    }
}