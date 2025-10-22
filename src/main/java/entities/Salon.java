package entities;

public class Salon {
    private int id;
    private String nombre;
    private int capacidad;
    private String descripcion;
    private byte[] imagen; // opcional (puede ser null)

    // Constructor vacío (necesario para frameworks o carga dinámica)
    public Salon() {
    }

    // Constructor sin ID, sin imagen (imagen opcional)
    public Salon(String nombre, int capacidad, String descripcion) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.descripcion = descripcion;
        this.imagen = null;
    }

    // Constructor sin ID, con imagen (cuando se quiera agregar)
    public Salon(String nombre, int capacidad, String descripcion, byte[] imagen) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    // Constructor con ID, sin imagen (cuando la imagen no esté cargada)
    public Salon(int id, String nombre, int capacidad, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.descripcion = descripcion;
        this.imagen = null;
    }

    // Constructor con ID e imagen (para registros completos)
    public Salon(int id, String nombre, int capacidad, String descripcion, byte[] imagen) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public byte[] getImagen() {
        return imagen;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    // Opcional: para logs o depuración
    @Override
    public String toString() {
        return "Salon{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", capacidad=" + capacidad +
                ", descripcion='" + descripcion + '\'' +
                ", imagen=" + (imagen != null ? "[imagen cargada]" : "null") +
                '}';
    }
}
