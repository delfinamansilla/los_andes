import React, { useState, useEffect } from 'react';
import NavbarSocio from './NavbarSocio';
import { useNavigate } from 'react-router-dom';

interface Cancha {
  id: number;
  nro_cancha: number;
  ubicacion: string;
  descripcion: string;
  tamanio: number;
  estado: boolean;
}

const Canchas = () => {
  const [canchas, setCanchas] = useState<Cancha[]>([]);
  const [mensajeError, setMensajeError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCanchas = async () => {
      try {
        const res = await fetch('http://localhost:8080/club/cancha?action=listar');
        if (!res.ok) throw new Error('Error al traer las canchas');
        const data: Cancha[] = await res.json();
        setCanchas(data);
      } catch (err) {
        if (err instanceof Error) {
          setMensajeError(err.message);
        } else {
          setMensajeError(String(err));
        }
      }
    };
    fetchCanchas();
  }, []);

  return (
    <div>
      <NavbarSocio />
      <div className="contenido-socio">
        <h2>Todas las Canchas</h2>
        {mensajeError && <p>{mensajeError}</p>}
        <ul>
          {canchas.map((c) => (
            <li key={c.id}>
              <h3>Cancha {c.nro_cancha}</h3>
              <p>Ubicación: {c.ubicacion}</p>
              <p>Descripción: {c.descripcion}</p>
              <p>Tamaño: {c.tamanio} m²</p>
              <p>Estado: {c.estado ? 'Disponible' : 'No disponible'}</p>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Canchas;

