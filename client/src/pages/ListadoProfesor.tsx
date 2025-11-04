import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from '../pages/NavbarAdmin';
// import './ListadoProfesores.css'; // Descomenta si creas un archivo de estilos

// Interfaz para definir la estructura de un profesor (mejora la seguridad del código)
interface Profesor {
  id: number;      // ¡IMPORTANTE! Asegúrate de que estos nombres
  nombre_completo: string;  // de propiedades coincidan exactamente con los
  telefono: string;         // que tienes en tu clase `Profesor` de Java.
  mail: string;
}

const ListadoProfesor: React.FC = () => {
  const [profesores, setProfesores] = useState<Profesor[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);
  
  const navigate = useNavigate();

  // Se ejecuta una sola vez cuando el componente se carga
  useEffect(() => {
    // 1. OBTENER EL USUARIO DE LOCALSTORAGE
    const usuarioJSON = localStorage.getItem('usuario');
    
    if (usuarioJSON) {
      // 2. CONVERTIR EL TEXTO A OBJETO
      const usuario = JSON.parse(usuarioJSON);
      const role = usuario.rol; // Extraemos la propiedad 'rol'
      setUserRole(role);

      // 3. VERIFICAR PERMISOS
      if (role === 'administrador') {
        fetchProfesores(); // Si es admin, buscamos los profesores
      } else {
        setLoading(false); // Si no es admin, dejamos de cargar
      }
    } else {
      // Si no hay usuario logueado, no tiene permisos
      setLoading(false);
      setUserRole(null);
    }
  }, []);

  // Función para obtener los profesores desde el backend
  const fetchProfesores = async () => {
    try {
      // NOTA: Tu servlet debe estar preparado para devolver un JSON en esta ruta
      const response = await fetch('http://localhost:8080/club/profesor?action=listar');
      
      if (!response.ok) {
        throw new Error('La respuesta del servidor no fue exitosa.');
      }
      
      const data: Profesor[] = await response.json();
      setProfesores(data);

    } catch (err: any) {
      setError('No se pudo cargar la lista de profesores. Asegúrate de que el backend esté funcionando y devuelva un JSON válido.');
      console.error(err);
    } finally {
      setLoading(false); // Oculta el mensaje de "Cargando..."
    }
  };

  // Función para manejar la eliminación de un profesor
  // En ListadoProfesor.tsx

  const handleDelete = async (id: number) => {
    if (!window.confirm('¿Estás seguro de que quieres eliminar a este profesor?')) {
      return;
    }

    try {
      // 1. Hacemos la llamada real al backend para eliminar
      const response = await fetch(`http://localhost:8080/club/profesor?action=eliminar&id=${id}`);
      
      // 2. Leemos la respuesta JSON que nos envía el servlet
      const data = await response.json();

      // 3. Verificamos si el backend respondió con un error
      if (!response.ok || data.status !== 'ok') {
        throw new Error(data.message || 'No se pudo eliminar al profesor.');
      }

      // 4. Si todo salió bien en el backend, actualizamos la lista en el frontend
      //    para que el profesor desaparecido desaparezca de la pantalla sin recargar.
      setProfesores(profesores.filter(p => p.id !== id));
      alert('✅ Profesor eliminado correctamente.');

    } catch (err: any) {
      // 5. Si algo falló, mostramos el mensaje de error que vino del backend
      alert(`❌ Error al eliminar: ${err.message}`);
      console.error("Error en handleDelete:", err);
    }
  };

  // Función para redirigir a la página de modificación
  const handleModify = (profesor: Profesor) => { // <-- CAMBIO 1: Recibe el objeto completo
    
    // CAMBIO 2: Guarda el objeto profesor en localStorage
    localStorage.setItem('profesorSeleccionado', JSON.stringify(profesor));
    
    // CAMBIO 3: Redirige a la página de modificar (sin pasar el ID en la URL)
    navigate('/modificar-profesor'); 
  };

  // --- Lógica de Renderizado ---

  // 1. Muestra "Cargando..." mientras se obtienen los datos
  if (loading) {
    return <div className="loading-container">Cargando...</div>;
  }

  // 2. Muestra "Acceso Denegado" si el rol no es el correcto
  if (userRole !== 'administrador') {
    return (
      <div className="access-denied-container">
        <h2>Acceso Denegado</h2>
        <p>No tienes permisos para ver esta sección.</p>
      </div>
    );
  }
  
  // 3. Muestra un mensaje de error si la petición falló
  if (error) {
    return <div className="error-box">Error: {error}</div>;
  }

  // 4. Si todo está bien, muestra la lista de profesores
  return (
    <div className="admin-page">
	<NavbarAdmin />
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
                <td>
				
				
				
				  <button onClick={() => handleModify(profesor)}>
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
  );
};

export default ListadoProfesor;
