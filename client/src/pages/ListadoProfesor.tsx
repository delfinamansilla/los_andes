import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from '../pages/NavbarAdmin';
import Modal from './Modal'; 
import '../styles/ListadoProfesor.css';

interface Profesor {
  id: number;
  nombre_completo: string;
  telefono: string;
  mail: string;
}

const ListadoProfesor: React.FC = () => {
  const [profesores, setProfesores] = useState<Profesor[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);

  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [profesorAEliminar, setProfesorAEliminar] = useState<number | null>(null);

  const [infoModal, setInfoModal] = useState<{ visible: boolean, mensaje: string }>({ visible: false, mensaje: '' });


  const navigate = useNavigate();

  useEffect(() => {
    const usuarioJSON = localStorage.getItem('usuario');

    if (usuarioJSON) {
      const usuario = JSON.parse(usuarioJSON);
      const role = usuario.rol;
      setUserRole(role);

      if (role === 'administrador') {
        fetchProfesores();
      } else {
        setLoading(false);
      }
    } else {
      setLoading(false);
      setUserRole(null);
    }
  }, []);

  const fetchProfesores = async () => {
    try {
      const response = await fetch('https://losandesback-production.up.railway.app/profesor?action=listar');

      if (!response.ok) {
        throw new Error('La respuesta del servidor no fue exitosa.');
      }

      const data: Profesor[] = await response.json();
      setProfesores(data);

    } catch (err: any) {
      setError('No se pudo cargar la lista de profesores. Asegúrate de que el backend esté funcionando y devuelva un JSON válido.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = (id: number) => {
    setProfesorAEliminar(id);
    setModalVisible(true);
  };

  const onCancelarDelete = () => {
    setModalVisible(false);
    setProfesorAEliminar(null);
  };

  const onConfirmarDelete = async () => {
    if (profesorAEliminar === null) return;

    try {
      const response = await fetch(`https://losandesback-production.up.railway.app/profesor?action=eliminar&id=${profesorAEliminar}`);
      const data = await response.json();

      if (!response.ok || data.status !== 'ok') {
        throw new Error(data.message || 'No se pudo eliminar al profesor.');
      }

      setProfesores(profesores.filter(p => p.id !== profesorAEliminar));
      setInfoModal({ visible: true, mensaje: 'Profesor eliminado correctamente.' });


    } catch (err: any) {
      setInfoModal({ visible: true, mensaje: `Error al eliminar: ${err.message}` });
      console.error("Error en onConfirmarDelete:", err);
    } finally {
      onCancelarDelete();
    }
  };


  const handleModify = (profesor: Profesor) => {
    localStorage.setItem('profesorSeleccionado', JSON.stringify(profesor));
    navigate('/modificar-profesor');
  };

  const cerrarInfoModal = () => {
    setInfoModal({ visible: false, mensaje: '' });
  };


  if (loading) {
    return <div className="loading-container">Cargando...</div>;
  }

  if (userRole !== 'administrador') {
    return (
      <div className="access-denied-container">
        <h2>Acceso Denegado</h2>
        <p>No tienes permisos para ver esta sección.</p>
      </div>
    );
  }

  if (error) {
    return <div className="error-box">Error: {error}</div>;
  }

  return (
    <div className="admin-page">
      <NavbarAdmin />
      <div className="content-area">
        <div className="list-container">
          <h2>Listado de Profesores</h2>
          {profesores.length === 0 ? (
            <p>No hay profesores registrados en la base de datos.</p>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Nombre Completo</th>
                  <th>Teléfono</th>
                  <th>Email</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {profesores.map((profesor) => (
                  <tr key={profesor.id}>
                    <td>{profesor.nombre_completo}</td>
                    <td>{profesor.telefono}</td>
                    <td>{profesor.mail}</td>
                    <td className="actions-cell">
                      <button className="btn-modify" onClick={() => handleModify(profesor)}>
                        Modificar
                      </button>
                      <button className="btn-delete" onClick={() => handleDelete(profesor.id)}>
                        Borrar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {modalVisible && (
        <Modal
          titulo="Confirmar Eliminación"
          mensaje="¿Estás seguro de que quieres eliminar a este profesor? Esta acción no se puede deshacer."
          textoConfirmar="Sí, eliminar"
          textoCancelar="No, cancelar"
          onConfirmar={onConfirmarDelete}
          onCancelar={onCancelarDelete}
        />
      )}

      {infoModal.visible && (
        <Modal
          titulo="Información"
          mensaje={infoModal.mensaje}
          textoConfirmar="Aceptar"
          onConfirmar={cerrarInfoModal}
          onCancelar={cerrarInfoModal} 
        />
      )}

    </div>
  );
};

export default ListadoProfesor;