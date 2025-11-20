import React, { useState, useEffect } from 'react';
import NavbarAdmin from './NavbarAdmin';
import '../styles/SalonesAdmin.css';

interface Salon {
  id: number;
  nombre: string;
  capacidad: number;
  descripcion: string;
  imagen: string | null; // base64 o null
}

const SalonesAdmin = () => {
  const [salones, setSalones] = useState<Salon[]>([]);
  const [mensajeError, setMensajeError] = useState('');
  const [showModalEliminar, setShowModalEliminar] = useState(false);
  const [salonAEliminar, setSalonAEliminar] = useState<Salon | null>(null);

  const fetchSalones = async () => {
    try {
      const res = await fetch('http://localhost:8080/club/salon?action=listar');
      if (!res.ok) throw new Error('Error al traer los salones');
      const data: Salon[] = await res.json();
      setSalones(data);
    } catch (err) {
      if (err instanceof Error) setMensajeError(err.message);
      else setMensajeError(String(err));
    }
  };

  useEffect(() => {
    fetchSalones();
  }, []);

  const handleEliminar = (salon: Salon) => {
    setSalonAEliminar(salon);
    setShowModalEliminar(true);
  };

  const confirmarEliminar = async () => {
    if (!salonAEliminar) return;

    try {
      const res = await fetch(
        `http://localhost:8080/club/salon?action=eliminar&id=${salonAEliminar.id}`
      );
      if (!res.ok) throw new Error('Error al eliminar salón');

      setShowModalEliminar(false);
      setSalonAEliminar(null);
      fetchSalones();
    } catch (err) {
      if (err instanceof Error) setMensajeError(err.message);
      else setMensajeError(String(err));
      setShowModalEliminar(false);
      setSalonAEliminar(null);
    }
  };

  const handleModificar = (salon: Salon) => {
    localStorage.setItem('salonSeleccionado', JSON.stringify(salon));
    window.location.href = '/modificar-salon';
  };


  return (
    <div className="salones-admin-page">
      <NavbarAdmin />

      <div className="contenido-admin-salon">
        <h2>Todos los Salones</h2>

        {mensajeError && <p style={{ color: 'red' }}>{mensajeError}</p>}

        <ul>
          {salones.map((s) => (
            <li key={s.id}>
              <h3>{s.nombre}</h3>

              {/* Imagen */}
              {s.imagen ? (
                <img
                  src={s.imagen}
                  alt="Imagen salón"
                  className="salon-img"
                />
              ) : (
                <div className="salon-img-placeholder">
                  <p>Sin imagen</p>
                </div>
              )}

              <p><strong>Capacidad:</strong> {s.capacidad} personas</p>
              <p><strong>Descripción:</strong> {s.descripcion}</p>

              <div className="salon-buttons">
                <button onClick={() => handleModificar(s)} className="btn-modificar">
                  Modificar datos
                </button>

                <button onClick={() => handleEliminar(s)} className="btn-eliminar">
                  Eliminar
                </button>
              </div>
            </li>
          ))}
        </ul>
      </div>

      {/* MODAL ELIMINAR */}
      {showModalEliminar && salonAEliminar && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>
              ¿Estás seguro que deseas eliminar el salón "{salonAEliminar.nombre}"?
            </h3>
            <div className="modal-buttons">
              <button onClick={confirmarEliminar} className="btn-confirm">
                Sí
              </button>
              <button
                onClick={() => setShowModalEliminar(false)}
                className="btn-cancel"
              >
                No
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SalonesAdmin;
