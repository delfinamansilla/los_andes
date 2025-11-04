import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './ListadoProfesores.css'; // Opcional: para estilos

// Definimos una interfaz para el objeto Profesor, para tener un tipado fuerte
interface Profesor {
  id_profesor: number; // Asegúrate que los nombres coincidan con los de tu clase Java
  nombre_completo: string;
  telefono: string;
  mail: string;
}

const ListadoProfesores: React.FC = () => {
  const [profesores, setProfesores] = useState<Profesor[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);
  
  const navigate = useNavigate();

  // 1. Cargar el rol del usuario y la lista de profesores al montar el componente
  useEffect(() => {
    const role = localStorage.getItem('userRole');
    setUserRole(role);

    if (role === 'administrador') {
      fetchProfesores();
    } else {
      setLoading(false);
    }
  }, []);

  const fetchProfesores = async () => {
    try {
      const response = await fetch('http://localhost:8080/club/profesor?action=listar');
      if (!response.ok) {
        throw new Error('Error al obtener los datos del servidor.');
      }
      const data: Profesor[] = await response.json();
      setProfesores(data);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 2. Función para manejar la eliminación de un profesor
  const handleDelete = async (id: number) => {
    // Pedimos confirmación al usuario
    if (!window.confirm('¿Estás seguro de que quieres eliminar a este profesor?')) {
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/club/profesor?action=eliminar&id=${id}`);
      const data = await response.json();

      if (response.ok && data.status === 'ok') {
        // Si se borra en el backend, lo eliminamos de la lista en el frontend
        // para no tener que recargar la página.
        setProfesores(profesores.filter(p => p.id_profesor !== id));
        alert('✅ Profesor eliminado correctamente.');
      } else {
        throw new Error(data.message || 'No se pudo eliminar al profesor.');
      }
    } catch (err: any) {
      setError(err.message);
      alert(`❌ Error: ${err.message}`);
    }
  };

  // 3. Función para redirigir a la página de modificación
  const handleModify = (id: number) => {
    // Te llevará a una ruta como "/editar-profesor/5"
    // Deberás crear este componente y esta ruta más adelante.
    navigate(`/editar-profesor/${id}`);
  };

  // --- Renderizado del Componente ---

  if (loading) {
    return <div className="loading">Cargando profesores...</div>;
  }

  if (userRole !== 'administrador') {
    return (
      <div className="admin-page">
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
      <h2>Listado de Profesores</h2>
      {profesores.length === 0 ? (
        <p>No hay profesores registrados.</p>
      ) : (
        <table className="profesores-table">
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
              <tr key={profesor.id_profesor}>
                <td>{profesor.nombre_completo}</td>
                <td>{profesor.telefono}</td>
                <td>{profesor.mail}</td>
                <td>
                  <button className="btn-modify" onClick={() => handleModify(profesor.id_profesor)}>
                    Modificar
                  </button>
                  <button className="btn-delete" onClick={() => handleDelete(profesor.id_profesor)}>
                    Borrar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ListadoProfesores;