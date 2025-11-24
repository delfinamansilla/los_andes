import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';
import '../styles/ListaActividades.css';


interface Actividad {
  id: number;
  nombre: string;
  descripcion?: string;
  cupo?: number;
}

const ListaActividades: React.FC = () => {
  const [actividades, setActividades] = useState<Actividad[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchActividades = async () => {
      setLoading(true);
      setError(null);

      try {
        const res = await fetch('https://losandesback-production.up.railway.app/actividad?action=listar', {
          method: 'GET',
          credentials: 'include',
        });

        const text = await res.text();

        let data: any = null;
        try {
          data = JSON.parse(text);
        } catch {
          console.warn('No se pudo parsear JSON');
        }

        if (Array.isArray(data)) {
          setActividades(data);
          localStorage.setItem('actividades', JSON.stringify(data));
        } else if (res.status !== 200) {
          setError('⚠ Error al cargar actividades');
        }
      } catch (err) {
        console.error('Error al obtener actividades:', err);
        setError('🚫 Error de conexión con el servidor');
      } finally {
        setLoading(false);
      }
    };

    fetchActividades();
  }, []);

  const handleVerDetalle = (actividad: Actividad) => {
    localStorage.setItem('actividadSeleccionada', JSON.stringify(actividad));
    navigate('/actividad-detalle'); 
  };

  const handleAgregar = () => {
    navigate('/actividades/nueva');
  };

  return (
      <div>
        <NavbarAdmin />
        <div className="page-container">
          <h2>Lista de Actividades</h2>

          {loading && <p>Cargando actividades...</p>}
          {error && <p className="error-box">{error}</p>}

          {!loading && !error && (
            <>
              {actividades.length > 0 ? (
                <div className="actividad-lista">
                  <button className="actividad-btn agregar-btn" onClick={handleAgregar}>
                    <strong><i className="fa-solid fa-plus"></i> Agregar actividad</strong>
                  </button>

                  {actividades.map((act) => (
                    <button
                      key={act.id}
                      className="actividad-btn"
                      onClick={() => handleVerDetalle(act)}
                    >
                      <strong>{act.nombre}</strong>
                      <p style={{ fontSize: '0.9rem', color: '#555' }}>
                        {act.descripcion || 'Sin descripción'}
                      </p>
                    </button>
                  ))}
                </div>
              ) : (
                <>
                  <div className="actividad-lista">
                    <button className="actividad-btn agregar-btn" onClick={handleAgregar}>
                      <strong><i className="fa-solid fa-plus"></i> Agregar actividad</strong>
                    </button>
                  </div>
                  <p>No hay actividades registradas.</p>
                </>
              )}
            </>
          )}
        </div>
      </div>
    );
};

export default ListaActividades;
