import React, { useEffect, useState } from 'react';
import NavbarSocio from './NavbarSocio';

interface Inscripcion {
  inscripcion_id: number;
  fecha_inscripcion: string;
  actividad_id: number;
  actividad_nombre: string;
  actividad_descripcion: string;
  profesor_nombre: string;
  cancha_descripcion: string;
  dia: string;
  hora_desde: string;
  hora_hasta: string;
}

const MisActividades: React.FC = () => {
  const [inscripciones, setInscripciones] = useState<Inscripcion[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchInscripciones = async () => {
    const rawUsuario = localStorage.getItem('usuario');
    const usuario = JSON.parse(rawUsuario || '{}');

    if (!usuario.id) {
      setError('No hay usuario logueado.');
      setLoading(false);
      return;
    }

    const url = 'http://localhost:8080/club/inscripcion?action=porusuario&id_usuario=${usuario.id}';

    try {
      const res = await fetch(url);

      if (!res.ok) {
        throw new Error('Error del servidor: ${res.status}');
      }

      const data: Inscripcion[] = await res.json();
      setInscripciones(data);
    } catch (err: any) {
      console.error('💥 Error en el fetch:', err);
      setError('No se pudieron cargar las actividades.');
    } finally {
      setLoading(false);
    }
  };

  const handleEliminar = async (inscripcionId: number, actividadNombre: string) => {
    if (!window.confirm('¿Estás seguro de que deseas eliminar la inscripción a "${actividadNombre}"?')) {
      return;
    }

    try {
      const url = 'http://localhost:8080/club/inscripcion?action=eliminar&id=${inscripcionId}';
      const res = await fetch(url, {
        method: 'GET'
      });

      if (!res.ok) {
        throw new Error('Error al eliminar: ${res.status}');
      }

      const result = await res.json();
      
      setInscripciones(inscripciones.filter(i => i.inscripcion_id !== inscripcionId));
      
      alert(result.mensaje || 'Inscripción eliminada correctamente');
    } catch (err: any) {
      console.error('💥 Error al eliminar:', err);
      alert('No se pudo eliminar la inscripción. Intenta nuevamente.');
    }
  };

  useEffect(() => {
    fetchInscripciones();
  }, []);

  if (loading) return <p>Cargando actividades...</p>;
  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (inscripciones.length === 0) return <p>No hay actividades inscriptas.</p>;

  return (
    <div className="mis-actividades">
	<NavbarSocio />
      <h2>Mis Actividades</h2>
      <table>
        <thead>
          <tr>
            <th>Actividad</th>
            <th>Descripción</th>
            <th>Profesor</th>
            <th>Cancha</th>
            <th>Horario</th>
            <th>Fecha Inscripción</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {inscripciones.map((i) => (
            <tr key={i.inscripcion_id}>
              <td>{i.actividad_nombre}</td>
              <td>{i.actividad_descripcion}</td>
              <td>{i.profesor_nombre || 'Sin asignar'}</td>
              <td>{i.cancha_descripcion || 'Sin asignar'}</td>
              <td>
                {i.dia && i.hora_desde && i.hora_hasta
                  ? `${i.dia} ${i.hora_desde} - ${i.hora_hasta}`
                  : 'Sin horario'}
              </td>
              <td>{i.fecha_inscripcion}</td>
              <td>
                <button
                  onClick={() => handleEliminar(i.inscripcion_id, i.actividad_nombre)}
                  style={{
                    backgroundColor: '#dc3545',
                    color: 'white',
                    border: 'none',
                    padding: '8px 16px',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    fontSize: '14px'
                  }}
                  onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#c82333'}
                  onMouseOut={(e) => e.currentTarget.style.backgroundColor = '#dc3545'}
                >
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default MisActividades;