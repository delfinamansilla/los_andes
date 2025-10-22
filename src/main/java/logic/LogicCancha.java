package logic;

import data.DataCancha;
import entities.Cancha;
import entities.Alquiler_cancha;
import entities.Actividad;
import entities.Partido;

import java.util.LinkedList;

public class LogicCancha {

    private DataCancha dataCancha;

    public LogicCancha() {
        dataCancha = new DataCancha();
    }

    private void validarCancha(Cancha c) throws Exception {
        if (c == null) throw new Exception("La cancha no puede ser nula.");
        if (c.getNro_cancha() <= 0) throw new Exception("El número de cancha debe ser mayor a cero.");
        if (c.getUbicacion() == null || c.getUbicacion().trim().isEmpty())
            throw new Exception("La ubicación no puede estar vacía.");
        if (c.getDescripcion() == null || c.getDescripcion().trim().isEmpty())
            throw new Exception("La descripción no puede estar vacía.");
        if (c.getTamanio() <= 0)
            throw new Exception("El tamaño de la cancha debe ser un valor positivo.");
    }

    private void validarExistenciaNumero(int nro_cancha) throws Exception {
        LinkedList<Cancha> todas = dataCancha.getAll();
        for (Cancha cancha : todas) {
            if (cancha.getNro_cancha() == nro_cancha) {
                throw new Exception("Ya existe una cancha con ese número.");
            }
        }
    }


    public LinkedList<Cancha> getAll() {
        return dataCancha.getAll();
    }

    public Cancha getOne(int id) throws Exception {
        Cancha c = dataCancha.getOne(id);
        if (c == null) {
            throw new Exception("No se encontró ninguna cancha con ID: " + id);
        }
        return c;
    }

    public void add(Cancha c) throws Exception {
        validarCancha(c);
        validarExistenciaNumero(c.getNro_cancha());
        dataCancha.add(c);
    }

    public void update(Cancha c) throws Exception {
        validarCancha(c);
        Cancha existente = dataCancha.getOne(c.getId());
        if (existente == null) {
            throw new Exception("No existe la cancha a actualizar.");
        }
        dataCancha.update(c);
    }

    public void delete(int id) throws Exception {
        Cancha existente = dataCancha.getOne(id);
        if (existente == null) {
            throw new Exception("No se puede eliminar: la cancha no existe.");
        }
        if (!existente.getAlquileresCancha().isEmpty() || !existente.getPartidos().isEmpty()) {
            throw new Exception("No se puede eliminar la cancha: tiene registros asociados (alquileres o partidos).");
        }
        dataCancha.delete(id);
    }


    public LinkedList<Partido> getPartidosByCancha(int idCancha) throws Exception {
        if (idCancha <= 0) throw new Exception("ID de cancha inválido.");
        return dataCancha.getPartidosByCancha(idCancha);
    }


    public LinkedList<Actividad> getActividadesByCancha(int idCancha) throws Exception {
        if (idCancha <= 0) throw new Exception("ID de cancha inválido.");
        return dataCancha.getActividadesByCancha(idCancha);
    }



    public boolean canchaDisponible(int idCancha) throws Exception {
        Cancha c = dataCancha.getOne(idCancha);
        if (c == null) throw new Exception("La cancha no existe.");
        return c.isEstado(); // true si está habilitada
    }

    public void habilitarCancha(int idCancha, boolean estado) throws Exception {
        Cancha c = dataCancha.getOne(idCancha);
        if (c == null) throw new Exception("La cancha no existe.");
        c.setEstado(estado);
        dataCancha.update(c);
    }
}
