import React, { useState, useEffect } from 'react';
import NavbarAdmin from './NavbarAdmin';
import '../styles/CanchasAdmin.css';

interface Cancha {
  id: number;
  nro_cancha: number;
  ubicacion: string;
  descripcion: string;
  tamanio: number;
  estado: boolean;
}

const CanchasAdmin = () => {
  const [canchas, setCanchas] = useState<Cancha[]>([]);
  const [mensajeError, setMensajeError] = useState('');
  const [showModalEliminar, setShowModalEliminar] = useState(false);
  const [canchaAEliminar, setCanchaAEliminar] = useState<Cancha | null>(null);

  const fetchCanchas = async () => {
    try {
      const res = await fetch('https://losandesback-production.up.railway.app/cancha?action=listar');
      if (!res.ok) throw new Error('Error al traer las canchas');
      const data: Cancha[] = await res.json();
      setCanchas(data);
    } catch (err) {
      if (err instanceof Error) setMensajeError(err.message);
      else setMensajeError(String(err));
    }
  };

  useEffect(() => {
    fetchCanchas();
  }, []);

  const handleEliminar = (cancha: Cancha) => {
    setCanchaAEliminar(cancha);
    setShowModalEliminar(true);
  };

  const confirmarEliminar = async () => {
    if (!canchaAEliminar) return;
    try {
      const res = await fetch(
        `https://losandesback-production.up.railway.app/cancha?action=eliminar&id=${canchaAEliminar.id}`
      );
      if (!res.ok) throw new Error('Error al eliminar cancha');
      setShowModalEliminar(false);
      setCanchaAEliminar(null);
      fetchCanchas();
    } catch (err) {
      if (err instanceof Error) setMensajeError(err.message);
      else setMensajeError(String(err));
      setShowModalEliminar(false);
      setCanchaAEliminar(null);
    }
  };

  const handleModificar = (cancha: Cancha) => {
    localStorage.setItem('canchaSeleccionada', JSON.stringify(cancha));
    window.location.href = '/canchas-admin/modificar';
  };
  
  const handleVerAlquileres = (cancha: Cancha) => {
    localStorage.setItem('canchaVerAlquileres', JSON.stringify(cancha));
    window.location.href = '/alquileres-admin';
  };

  return (
    <div className="canchas-admin-page">
      <NavbarAdmin />
      <div className="contenido-admin">
        <h2>Todas las Canchas</h2>
        {mensajeError && <p style={{ color: 'red' }}>{mensajeError}</p>}
        <ul>
          {canchas.map((c) => (
            <li key={c.id}>
              <h3>Cancha {c.nro_cancha}</h3>

              <p>Ubicación: {c.ubicacion}</p>
              <p>Descripción: {c.descripcion}</p>
              <p>Tamaño: {c.tamanio} m²</p>
              <p>Estado: {c.estado ? 'Disponible' : 'No disponible'}</p>

              <div className="cancha-buttons">
			  <button onClick={() => handleVerAlquileres(c)} className="btn-ver-alquileres">
			    Ver alquileres
			  </button>
                <button onClick={() => handleModificar(c)} className="btn-modificar">
                  Modificar datos
                </button>
                <button onClick={() => handleEliminar(c)} className="btn-eliminar">
                  Eliminar
                </button>
              </div>
            </li>
          ))}
        </ul>
      </div>

      {showModalEliminar && canchaAEliminar && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>¿Estás seguro que quieres eliminar la cancha {canchaAEliminar.nro_cancha}?</h3>
            <div className="modal-buttons">
              <button onClick={confirmarEliminar} className="btn-confirm">Sí</button>
              <button onClick={() => setShowModalEliminar(false)} className="btn-cancel">No</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CanchasAdmin;

