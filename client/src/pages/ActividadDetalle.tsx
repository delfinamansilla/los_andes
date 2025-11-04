import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';

interface Actividad {
  idActividad: number;
  nombre: string;
  descripcion: string;
  cupo: number;
  inscripcion_desde: string;
  inscripcion_hasta: string;
  id_profesor: number;
  id_cancha: number;
}

const ActividadDetalle: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [actividad, setActividad] = useState<Actividad | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDetalle = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(`http://localhost:8080/club/actividad?action=detalle&id=${id}`);
        const text = await res.text();
        console.log('Respuesta detalle:', text);

        const data = JSON.parse(text);
        setActividad(data);
      } catch (err) {
        console.error(err);
        setError('No se pudo obtener el detalle de la actividad.');
      } finally {
        setLoading(false);
      }
    };
    fetchDetalle();
  }, [id]);

  return (
    <div>
      <NavbarAdmin />
      <div className="page-container">
        {loading && <p>Cargando detalles...</p>}
        {error && <p className="error-box">{error}</p>}

        {actividad && (
          <>
            <h2>📋 {actividad.nombre}</h2>
            <p><strong>Descripción:</strong> {actividad.descripcion}</p>
            <p><strong>Cupo:</strong> {actividad.cupo}</p>
            <p><strong>Inscripción desde:</strong> {actividad.inscripcion_desde}</p>
            <p><strong>Inscripción hasta:</strong> {actividad.inscripcion_hasta}</p>
            <p><strong>ID Profesor:</strong> {actividad.id_profesor}</p>
            <p><strong>ID Cancha:</strong> {actividad.id_cancha}</p>

            <button onClick={() => navigate('/actividades')}>⬅ Volver</button>
          </>
        )}
      </div>
    </div>
  );
};

export default ActividadDetalle;
