import React, { useEffect, useState } from 'react';
import NavbarSocio from './NavbarSocio';

interface Actividad {
  id: number;
  nombre: string;
  descripcion: string;
  inscripcion_desde: string;
  inscripcion_hasta: string;
  cupo: number;
  id_profesor: number;
  id_cancha: number;
  profesor_nombre?: string;
  cancha_descripcion?: string;
  dia?: string;
  hora_desde?: string;
  hora_hasta?: string;
}

const InscripcionActividad: React.FC = () => {
  const [actividades, setActividades] = useState<Actividad[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchActividades = async () => {
    const url = 'http://localhost:8080/club/actividad?action=listarcondetalles&format=json';

    try {
      const res = await fetch(url);

      if (!res.ok) {
        throw new Error('Error del servidor: ${res.status}');
      }
 
      const data: Actividad[] = await res.json();
      setActividades(data);
    } catch (err: any) {
      console.error('💥 Error en el fetch:', err);
      setError('No se pudieron cargar las actividades.');
    } finally {
      setLoading(false);
    }
  };

  const handleInscribir = async (actividadId: number, actividadNombre: string) => {
    const rawUsuario = localStorage.getItem('usuario');
    const usuario = JSON.parse(rawUsuario || '{}');

    if (!usuario.id) {
      alert('No hay usuario logueado.');
      return;
    }

    if (!window.confirm('¿Deseas inscribirte a "${actividadNombre}"?')) {
      return;
    }

    try {
      const fechaHoy = new Date().toISOString().split('T')[0];
      const url = 'http://localhost:8080/club/inscripcion?action=crear&fecha_inscripcion=${fechaHoy}&id_usuario=${usuario.id}&id_actividad=${actividadId}';
      
      const res = await fetch(url, {
        method: 'POST'
      });

      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.error || 'Error al inscribirse');
      }

      const result = await res.json();
      alert(result.mensaje || 'Inscripción creada correctamente');
      
      // Opcional: recargar actividades para actualizar cupos
      // fetchActividades();
      
    } catch (err: any) {
      console.error('💥 Error al inscribir:', err);
      alert(err.message || 'No se pudo completar la inscripción. Intenta nuevamente.');
    }
  };

  useEffect(() => {
    fetchActividades();
  }, []);

  if (loading) return <p>Cargando actividades...</p>;
  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (actividades.length === 0) return <p>No hay actividades disponibles.</p>;

  return (
    <div className="inscripcion-actividad">
	<NavbarSocio />
      <h2>Actividades Disponibles</h2>
      <table>
        <thead>
          <tr>
            <th>Actividad</th>
            <th>Descripción</th>
            <th>Profesor</th>
            <th>Cancha</th>
            <th>Horario</th>
            <th>Período Inscripción</th>
            <th>Cupo</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {actividades.map((a) => (
            <tr key={a.id}>
              <td>{a.nombre}</td>
              <td>{a.descripcion}</td>
              <td>{a.profesor_nombre || 'Sin asignar'}</td>
              <td>{a.cancha_descripcion || 'Sin asignar'}</td>
              <td>
                {a.dia && a.hora_desde && a.hora_hasta
                  ? `${a.dia} ${a.hora_desde} - ${a.hora_hasta}`
                  : 'Sin horario'}
              </td>
              <td>
                {a.inscripcion_desde && a.inscripcion_hasta
                  ? `${a.inscripcion_desde} - ${a.inscripcion_hasta}`
                  : 'Sin período'}
              </td>
              <td>{a.cupo}</td>
              <td>
                <button
                  onClick={() => handleInscribir(a.id, a.nombre)}
                  style={{
                    backgroundColor: '#28a745',
                    color: 'white',
                    border: 'none',
                    padding: '8px 16px',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    fontSize: '14px'
                  }}
                  onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#218838'}
                  onMouseOut={(e) => e.currentTarget.style.backgroundColor = '#28a745'}
                >
                  Inscribirse
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default InscripcionActividad;