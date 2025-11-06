import React, { useEffect, useState } from 'react';
import NavbarSocio from './NavbarSocio';
import '../styles/MisActividades.css'

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

    const url = `http://localhost:8080/club/inscripcion?action=porusuario&id_usuario=${usuario.id}`;

    try {
      const res = await fetch(url);

      if (!res.ok) {
        throw new Error(`Error del servidor: ${res.status}`);
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
    if (!window.confirm(`¿Estás seguro de que deseas eliminar la inscripción a "${actividadNombre}"?`)) {
      return;
    }

    try {
      const url = `http://localhost:8080/club/inscripcion?action=eliminar&id=${inscripcionId}`;
      const res = await fetch(url, {
        method: 'GET'
      });

      if (!res.ok) {
        throw new Error(`Error al eliminar: ${res.status}`);
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

  if (loading) return <div className="mis-actividades"><p className="status-message">Cargando actividades...</p></div>;
    if (error) return <div className="mis-actividades"><p className="status-message error">{error}</p></div>;

return (
    <div className="mis-actividades-page">
      <NavbarSocio />
      <div className="mis-actividades-content">
        <h2>Mis Actividades</h2>

        {inscripciones.length === 0 ? (
          <p className="status-message">No estás inscripto a ninguna actividad.</p>
        ) : (
          <table className="activities-table">
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
                  <td data-label="Actividad">{i.actividad_nombre}</td>
                  <td data-label="Descripción">{i.actividad_descripcion}</td>
                  <td data-label="Profesor">{i.profesor_nombre || 'Sin asignar'}</td>
                  <td data-label="Cancha">{i.cancha_descripcion || 'Sin asignar'}</td>
                  <td data-label="Horario">
                    {i.dia && i.hora_desde && i.hora_hasta
                      ? `${i.dia} ${i.hora_desde} - ${i.hora_hasta}`
                      : 'Sin horario'}
                  </td>
                  <td data-label="Fecha Inscripción">{i.fecha_inscripcion}</td>
                  <td data-label="Acciones">
                    <button
                      onClick={() => handleEliminar(i.inscripcion_id, i.actividad_nombre)}
                      className="btn-eliminar" 
                    >
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default MisActividades;