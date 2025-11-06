import React, { useEffect, useState } from 'react';
import NavbarSocio from './NavbarSocio';
import Modal from './Modal'; 
import '../styles/MisActividades.css';

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

  const [mostrarModal, setMostrarModal] = useState(false);
  const [inscripcionAEliminar, setInscripcionAEliminar] = useState<Inscripcion | null>(null);

  const [showInfoModal, setShowInfoModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');

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
      if (!res.ok) throw new Error(`Error del servidor: ${res.status}`);
      const data: Inscripcion[] = await res.json();
      setInscripciones(data);
    } catch (err: any) {
      console.error('Error en el fetch:', err);
      setError('No se pudieron cargar las actividades.');
    } finally {
      setLoading(false);
    }
  };

  const handleEliminar = (inscripcion: Inscripcion) => {
    setInscripcionAEliminar(inscripcion);
    setMostrarModal(true);
  };

  const confirmarEliminar = async () => {
    if (!inscripcionAEliminar) return;

    try {
      const url = `http://localhost:8080/club/inscripcion?action=eliminar&id=${inscripcionAEliminar.inscripcion_id}`;
      const res = await fetch(url, { method: 'GET' });
      if (!res.ok) throw new Error('Error al eliminar');

      const result = await res.json();
      setInscripciones(inscripciones.filter(i => i.inscripcion_id !== inscripcionAEliminar.inscripcion_id));
      
      setModalMessage(result.mensaje || 'Inscripción eliminada correctamente');
      setShowInfoModal(true);

    } catch (err: any) {
      console.error('Error al eliminar:', err);
      setModalMessage('No se pudo eliminar la inscripción.');
      setShowInfoModal(true);
    } finally {
      setMostrarModal(false);
      setInscripcionAEliminar(null);
    }
  };

  const cancelarEliminar = () => {
    setMostrarModal(false);
    setInscripcionAEliminar(null);
  };

  useEffect(() => {
    fetchInscripciones();
  }, []);

  if (loading) return <div className="mis-actividades-page"><p className="status-message">Cargando...</p></div>;
  if (error) return <div className="mis-actividades-page"><p className="status-message error">{error}</p></div>;

  return (
    <>
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
                        onClick={() => handleEliminar(i)}
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

      {mostrarModal && inscripcionAEliminar && (
        <Modal
          titulo="Eliminar Inscripción"
          mensaje={`Estás seguro de que deseas anular tu inscripción a "${inscripcionAEliminar.actividad_nombre}"?`}
          textoConfirmar="Sí, anular"
          textoCancelar="Cancelar"
          onConfirmar={confirmarEliminar}
          onCancelar={cancelarEliminar}
        />
      )}

      {showInfoModal && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>Aviso</h3>
            <p>{modalMessage}</p>
            <div className="modal-buttons">
              <button onClick={() => setShowInfoModal(false)} className="btn-confirm">Aceptar</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default MisActividades;