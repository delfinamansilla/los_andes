package logic;

import data.DataProfesor;
import entities.Profesor;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicProfesor {

    private DataProfesor dp;

    public LogicProfesor() {
        this.dp = new DataProfesor();
    }

   
    public LinkedList<Profesor> getAll() {
        return dp.getAll();
    }

 
    public void add(Profesor p) throws Exception {
        validarProfesor(p);
        p.setMail(p.getMail().toLowerCase());

        dp.add(p);
    }

    public void update(Profesor p) throws Exception {
        validarProfesor(p);
        p.setMail(p.getMail().toLowerCase());
        dp.update(p);
    }

  
    public void delete(int id) {
        if (id > 0) {
            dp.delete(id);
        }
    }

 
    public Profesor getOne(int id) {
        return dp.getOne(id);
    }

  
    private void validarProfesor(Profesor p) throws Exception {

        // Nombre no vacío ni con números
        if (p.getNombreCompleto() == null || p.getNombreCompleto().trim().isEmpty()) {
            throw new Exception("El nombre completo no puede estar vacío.");
        }
        if (!p.getNombreCompleto().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new Exception("El nombre no puede contener números ni caracteres especiales.");
        }

        // Validar teléfono
        if (p.getTelefono() == null || p.getTelefono().trim().isEmpty()) {
            throw new Exception("El teléfono no puede estar vacío.");
        }
        if (!p.getTelefono().matches("\\d{8,15}")) {
            throw new Exception("El teléfono debe contener solo números y tener entre 8 y 15 dígitos.");
        }

        // Validar email
        if (p.getMail() == null || p.getMail().trim().isEmpty()) {
            throw new Exception("El email no puede estar vacío.");
        }

        String regexEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regexEmail);
        Matcher matcher = pattern.matcher(p.getMail());
        if (!matcher.matches()) {
            throw new Exception("El formato del email no es válido.");
        }

        // Validar unicidad de email
        LinkedList<Profesor> profesores = dp.getAll();
        for (Profesor existente : profesores) {
            if (existente.getMail().equalsIgnoreCase(p.getMail()) &&
                existente.getIdProfesor() != p.getIdProfesor()) {
                throw new Exception("Ya existe un profesor con el mismo email.");
            }
        }
        

    }
}
