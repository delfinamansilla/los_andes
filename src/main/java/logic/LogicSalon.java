package logic;

import data.DataSalon;
import entities.Salon;
import data.DataAlquiler_salon;

import java.util.LinkedList;

public class LogicSalon {

    private DataSalon ds;

    public LogicSalon() {
        this.ds = new DataSalon();
    }

    /**
     * Devuelve todos los salones registrados.
     * @return Lista de salones.
     */
    public LinkedList<Salon> getAll() {
        return ds.getAll();
    }

    /**
     * Devuelve un salón por su ID.
     * @param id ID del salón.
     * @return El salón correspondiente, o null si no existe.
     */
    public Salon getById(int id) {
        if (id <= 0) return null;
        return ds.getById(id);
    }

    /**
     * Agrega un nuevo salón validando todas las reglas de negocio.
     * @param s Objeto Salon con los datos a registrar.
     * @throws Exception Si alguna validación falla.
     */
    public void add(Salon s) throws Exception {
        validarSalon(s);

        if (existeSalonConNombre(s.getNombre())) {
            throw new Exception("Ya existe un salón con el nombre '" + s.getNombre() + "'.");
        }

        ds.add(s);
    }

    /**
     * Actualiza los datos de un salón existente.
     * @param s El salón con los datos actualizados.
     * @throws Exception Si alguna validación falla o no se encuentra el salón.
     */
    public void update(Salon s) throws Exception {
        if (s.getId() <= 0) {
            throw new Exception("El ID del salón no es válido.");
        }

        validarSalon(s);

        Salon existente = ds.getById(s.getId());
        if (existente == null) {
            throw new Exception("No se encontró el salón que se desea modificar.");
        }

        Salon otroSalon = getByNombre(s.getNombre());
        if (otroSalon != null && otroSalon.getId() != s.getId()) {
            throw new Exception("Ya existe otro salón con el nombre '" + s.getNombre() + "'.");
        }

        ds.update(s);
    }

    /**
     * Elimina un salón por su ID.
     * @param id ID del salón a eliminar.
     * @throws Exception Si el ID no es válido o el salón no existe o si tiene alquileres vigentes.
     */
    public void delete(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("El ID del salón no es válido.");
        }

        Salon existente = ds.getById(id);
        if (existente == null) {
            throw new Exception("No se encontró el salón que intenta eliminar.");
        }

        DataAlquiler_salon dataAlquiler = new DataAlquiler_salon();
        if (dataAlquiler.tieneReservasActivas(id)) {
            throw new Exception("No se puede eliminar el salón porque tiene reservas activas.");
        }

        ds.delete(id);
    }


    /**
     * Validaciones de negocio para la entidad Salon.
     * @param s El salón a validar.
     * @throws Exception Si alguna validación falla.
     */
    private void validarSalon(Salon s) throws Exception {
        if (s == null) {
            throw new Exception("El salón no puede ser nulo.");
        }

        if (s.getNombre() == null || s.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del salón no puede estar vacío.");
        }

        if (s.getNombre().length() > 100) {
            throw new Exception("El nombre del salón no puede superar los 100 caracteres.");
        }

        if (s.getCapacidad() <= 0) {
            throw new Exception("La capacidad del salón debe ser mayor que 0.");
        }

        if (s.getCapacidad() > 1000) {
            throw new Exception("La capacidad del salón no puede ser mayor a 1000 personas.");
        }

        if (s.getDescripcion() == null || s.getDescripcion().trim().isEmpty()) {
            throw new Exception("La descripción no puede estar vacía.");
        }

        if (s.getDescripcion().length() > 250) {
            throw new Exception("La descripción no puede tener más de 250 caracteres.");
        }

        // Validar que si hay imagen, no exceda cierto tamaño (por ejemplo 2 MB)
        if (s.getImagen() != null && s.getImagen().length > (2 * 1024 * 1024)) {
            throw new Exception("La imagen no puede superar los 2 MB de tamaño.");
        }
    }

    /**
     * Verifica si ya existe un salón con un nombre determinado.
     * @param nombre El nombre a buscar.
     * @return true si ya existe, false si no.
     */
    private boolean existeSalonConNombre(String nombre) {
        Salon existente = getByNombre(nombre);
        return existente != null;
    }

    /**
     * Obtiene un salón por su nombre (ayuda a validar unicidad).
     * @param nombre Nombre del salón.
     * @return El salón encontrado o null si no existe.
     */
    private Salon getByNombre(String nombre) {
        for (Salon s : ds.getAll()) {
            if (s.getNombre().equalsIgnoreCase(nombre.trim())) {
                return s;
            }
        }
        return null;
    }
}
