import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';

interface Actividad {
  idActividad: number;
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
        const res = await fetch('http://localhost:8080/club/actividad?action=listar', {
          method: 'GET',
          credentials: 'include',
        });

        const text = await res.text();
        console.log('Respuesta cruda de /actividad:', text);

        let data: any = null;
        try {
          data = JSON.parse(text);
        } catch {
          console.warn('⚠ No se pudo parsear JSON');
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

  const handleVerDetalle = (id: number) => {
    navigate(`/actividad/${id}`); // 👈 lleva al detalle
  };

  const handleAgregar = () => {
    navigate('/actividades/nueva');
  };

  return (
    <div>
      <NavbarAdmin />
      <div className="page-container">
        <h2>🗂️ Lista de Actividades</h2>

        {loading && <p>Cargando actividades...</p>}
        {error && <p className="error-box">{error}</p>}

        {!loading && !error && (
          <>
            {actividades.length > 0 ? (
              <div className="actividad-lista">
                {actividades.map((act) => (
                  <button
                    key={act.idActividad}
                    className="actividad-btn"
                    onClick={() => handleVerDetalle(act.idActividad)}
                  >
                    <strong>{act.nombre}</strong>
                    <p style={{ fontSize: '0.9rem', color: '#555' }}>
                      {act.descripcion || 'Sin descripción'}
                    </p>
                  </button>
                ))}
              </div>
            ) : (
              <p>No hay actividades registradas.</p>
            )}

            <button className="btn_agregar" onClick={handleAgregar}>
              ➕ Agregar nueva actividad
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default ListaActividades;
