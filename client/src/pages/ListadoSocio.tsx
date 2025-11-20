import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';
import '../styles/ListadoProfesor.css';

interface Socio {
  id: number;
  nombre_completo: string;
  dni: string;
  mail: string;
  telefono: string;
  rol: string;
}

const ListadoSocios: React.FC = () => {
  const [socios, setSocios] = useState<Socio[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);

  const navigate = useNavigate();

  useEffect(() => {
    const usuarioJSON = localStorage.getItem('usuario');

    if (usuarioJSON) {
      const usuario = JSON.parse(usuarioJSON);
      const role = usuario.rol;
      setUserRole(role);

      if (role === 'administrador') {
        fetchSocios();
      } else {
        setLoading(false);
      }
    } else {
      setLoading(false);
      setUserRole(null);
    }
  }, []);

  const fetchSocios = async () => {
    try {
      const response = await fetch('http://localhost:8080/club/usuario?action=listar');

      if (!response.ok) {
        throw new Error('Error al conectar con el servidor.');
      }

      const data: Socio[] = await response.json();

      // Filtramos para mostrar SOLO los que son 'socio'
      const soloSocios = data.filter(u => u.rol && u.rol.toLowerCase() === 'socio');
      
      setSocios(soloSocios);

    } catch (err: any) {
      setError('No se pudo cargar la lista de socios.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // --- ESTA ES LA FUNCIÓN CORRECTA (ASEGÚRATE QUE SOLO HAYA UNA) ---
  const handleVerCuotas = (socio: Socio) => {
    // Guardamos en LocalStorage
    localStorage.setItem('usuarioSeleccionadoParaCuotas', JSON.stringify(socio));
    
    // Navegamos
    navigate('/cuotas-usuario');
  };
  // ----------------------------------------------------------------

  if (loading) {
    return (
      <div className="admin-page">
        <NavbarAdmin />
        <div className="loading-container">Cargando socios...</div>
      </div>
    );
  }

  if (userRole !== 'administrador') {
    return (
      <div className="access-denied-container">
        <h2>Acceso Denegado</h2>
        <p>No tienes permisos para ver esta sección.</p>
      </div>
    );
  }

  return (
    <div className="admin-page">
      <NavbarAdmin />
      <div className="content-area">
        <div className="list-container">
          <h2>Listado de Socios</h2>

          {error && <p className="error-box">{error}</p>}

          {!loading && !error && socios.length === 0 ? (
            <p>No se encontraron socios registrados.</p>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>DNI</th>
                  <th>Nombre Completo</th>
                  <th>Email</th>
                  <th>Teléfono</th>
                  <th style={{ textAlign: 'center' }}>Gestión</th>
                </tr>
              </thead>
              <tbody>
                {socios.map((socio) => (
                  <tr key={socio.id}>
                    <td>{socio.dni}</td>
                    <td>{socio.nombre_completo}</td>
                    <td>{socio.mail}</td>
                    <td>{socio.telefono}</td>
                    <td className="actions-cell" style={{ justifyContent: 'center' }}>
                      <button 
                        className="btn-cuotas"
                        onClick={() => handleVerCuotas(socio)}
                        title="Ver estado de cuenta"
                      >
                         Cuotas
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
};

export default ListadoSocios;