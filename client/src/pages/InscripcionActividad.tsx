import React, { useEffect, useState } from 'react';
import NavbarSocio from './NavbarSocio';
import '../styles/InscripcionActividad.css'

interface Actividad {
  id: number;
  nombre: string;
  descripcion: string;
  inscripcion_desde: string;
  inscripcion_hasta: string;
  cupo: number;
  cupo_restante: number;
  yaInscripto: boolean;
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
	
	const rawUsuario = localStorage.getItem('usuario');
	const usuario = JSON.parse(rawUsuario || '{}');
	
	if (!usuario.id) {
	      setError('Debes iniciar sesión para ver las actividades.');
	      setLoading(false);
	      return;
	    }
    const url = `http://localhost:8080/club/actividad?action=listarcondetalles&format=json&id_usuario=${usuario.id}`;

    try {
      const res = await fetch(url);

      if (!res.ok) {
        throw new Error(`Error del servidor: ${res.status}`);
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

    if (!window.confirm(`¿Deseas inscribirte a "${actividadNombre}"?`)) {
      return;
    }

    try {
      const fechaHoy = new Date().toISOString().split('T')[0];
      const url = `http://localhost:8080/club/inscripcion?action=crear&fecha_inscripcion=${fechaHoy}&id_usuario=${usuario.id}&id_actividad=${actividadId}`;
      
      const res = await fetch(url, {
        method: 'POST'
      });

      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.error || 'Error al inscribirse');
      }

      const result = await res.json();
      alert(result.mensaje || 'Inscripción creada correctamente');
      
      fetchActividades();
      
    } catch (err: any) {
      console.error('Error al inscribir:', err);
      alert(err.message || 'No se pudo completar la inscripción. Intenta nuevamente.');
    }
  };

  useEffect(() => {
    fetchActividades();
  }, []);

  if (loading) return <div className="inscripcion-page"><p className="status-message">Cargando actividades...</p></div>;
    if (error) return <div className="inscripcion-page"><p className="status-message error">{error}</p></div>;

	return (
	    <div className="inscripcion-page">
	      <NavbarSocio />
	      <div className="inscripcion-content">
	        <h2>Actividades Disponibles</h2>
	        {actividades.length === 0 ? (
	          <p className="status-message">No hay actividades disponibles en este momento.</p>
	        ) : (
	          <table className="inscripcion-table">
	            <thead>
	              <tr>
	                <th>Actividad</th>
	                <th>Descripción</th>
	                <th>Profesor</th>
	                <th>Cancha</th>
	                <th>Horario</th>
	                <th>Período Inscripción</th>
	                <th>Lugares Disponibles</th> 
	                <th>Acciones</th>
	              </tr>
	            </thead>
	            <tbody>
	              {actividades.map((a) => (
	                <tr key={a.id}>
	                  <td data-label="Actividad">{a.nombre}</td>
	                  <td data-label="Descripción">{a.descripcion}</td>
	                  <td data-label="Profesor">{a.profesor_nombre || 'Sin asignar'}</td>
	                  <td data-label="Cancha">{a.cancha_descripcion || 'Sin asignar'}</td>
	                  <td data-label="Horario">
	                    {a.dia && a.hora_desde && a.hora_hasta
	                      ? `${a.dia} ${a.hora_desde} - ${a.hora_hasta}`
	                      : 'Sin horario'}
	                  </td>
	                  <td data-label="Período Inscripción">
	                    {a.inscripcion_desde && a.inscripcion_hasta
	                      ? `${new Date(a.inscripcion_desde).toLocaleDateString()} - ${new Date(a.inscripcion_hasta).toLocaleDateString()}`
	                      : 'Sin período'}
	                  </td>

	                  <td data-label="Lugares Disponibles">{a.cupo_restante}</td> 
	                  <td data-label="Acciones">

	                    {a.yaInscripto ? (
	                      // El usuario YA está inscripto
	                      <button className="btn-inscripto" disabled>
	                        Inscripto
	                      </button>
	                    ) : a.cupo_restante > 0 ? (
	                      // NO está inscripto Y HAY LUGAR
	                      <button
	                        onClick={() => handleInscribir(a.id, a.nombre)}
	                        className="btn-inscribir"
	                      >
	                        Inscribirse
	                      </button>
	                    ) : (
	                      // NO está inscripto Y NO HAY LUGAR
	                      <button className="btn-inscribir" disabled>
	                        Sin Lugares
	                      </button>
	                    )}
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

export default InscripcionActividad;