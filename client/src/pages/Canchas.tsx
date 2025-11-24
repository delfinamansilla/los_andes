import React, { useState, useEffect } from 'react';
import NavbarSocio from './NavbarSocio';
import '../styles/Canchas.css';
import { useNavigate } from 'react-router-dom';
import Footer from './Footer';

interface Cancha {
  id: number;
  nro_cancha: number;
  ubicacion: string;
  descripcion: string;
  tamanio: number;
  estado: boolean;
}

const Canchas: React.FC = () => {
  const [canchas, setCanchas] = useState<Cancha[]>([]);
  const [loading, setLoading] = useState(true); 
  const [error, setError] = useState<string | null>(null); 
  
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCanchas = async () => {
      try {
        const res = await fetch('https://losandesback-production.up.railway.app/cancha?action=listar');
        if (!res.ok) throw new Error('No se pudieron cargar las canchas.');
        const data: Cancha[] = await res.json();
        setCanchas(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchCanchas();
  }, []);
  
  const alquilarCancha = (cancha: Cancha) => {
    navigate(`/alquilar-cancha/${cancha.id}`);
  };

  if (loading) return <div className="canchas-socio-page"><p className="status-message">Cargando...</p></div>;
  if (error) return <div className="canchas-socio-page"><p className="status-message error">{error}</p></div>;

  return (
    <div className="canchas-socio-page">
      <NavbarSocio />
      <div className="canchas-socio-content">
        <h2>Todas las canchas</h2>

        {canchas.length === 0 ? (
          <p className="status-message">No hay canchas para mostrar.</p>
        ) : (
          <ul className="canchas-lista">
            {canchas.map((c) => (
              <li key={c.id} className="canchas-lista-item">
                <h3>Cancha {c.nro_cancha}</h3>
                <p><strong>Ubicación:</strong> {c.ubicacion}</p>
                <p><strong>Descripción:</strong> {c.descripcion}</p>
                <p><strong>Tamaño:</strong> {c.tamanio} m²</p>
                <p>
                  <span className="estado-label">Estado:</span>{' '}
                  {c.estado ? 'Disponible' : 'No disponible'}
                </p>
				
				<button
				  className="btn-alquilar-cancha"
				  disabled={!c.estado}
				  onClick={() => alquilarCancha(c)}
				>
				  Alquilar
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

export default Canchas;