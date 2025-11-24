import React, { useState, useEffect } from 'react';
import NavbarSocio from './NavbarSocio';
import Footer from './Footer';
import '../styles/Salones.css';

interface Salon {
  id: number;
  nombre: string;
  capacidad: number;
  descripcion: string;
  imagen: string | null;
}

const Salones: React.FC = () => {
  const [salones, setSalones] = useState<Salon[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchSalones = async () => {
      try {
        const res = await fetch('https://losandesback-production.up.railway.app/salon?action=listar');
        if (!res.ok) throw new Error('No se pudieron cargar los salones.');
        const data: Salon[] = await res.json();
        setSalones(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchSalones();
  }, []);

  if (loading)
    return <div className="salones-page"><p className="status-message">Cargando...</p></div>;

  if (error)
    return <div className="salones-page"><p className="status-message error">{error}</p></div>;

  return (
    <div className="salones-page">
      <NavbarSocio />

      <div className="salones-content">
        <h2>Todos los Salones</h2>

        {salones.length === 0 ? (
          <p className="status-message">No hay salones para mostrar.</p>
        ) : (
          <ul className="salones-lista">
            {salones.map((s) => (
              <li key={s.id} className="salones-lista-item">
                <h3>{s.nombre}</h3>

                {s.imagen && (
                  <img
                    src={s.imagen}
                    alt={s.nombre}
                    className="salon-img"
                  />
                )}

                <p><strong>Capacidad:</strong> {s.capacidad} personas</p>
                <p><strong>Descripción:</strong> {s.descripcion}</p>

                <button
                  className="btn-disponibilidad"
                  onClick={() => window.location.href = `/alquiler_salon/${s.id}`}
                >
                  Ver disponibilidad de alquiler
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>

      <Footer />
    </div>
  );
};

export default Salones;
