import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';
import '../styles/ListaActividades.css';
import { API_URL } from "../config";


interface Actividad {
  id: number;
  nombre: string;
  descripcion?: string;
  cupo?: number;
}

const ListaActividades: React.FC = () => {
  const [actividades, setActividades] = useState<Actividad[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchActividades = async () => {
      setLoading(true);
      setError(null);

      try {
        const res = await fetch(`${API_URL}/actividad?action=listar`, {
          method: 'GET'
        });

        const text = await res.text();

        let data: any = null;
        try {
          data = JSON.parse(text);
        } catch {
          console.warn('No se pudo parsear JSON');
        }

        if (Array.isArray(data)) {
          setActividades(data);
          localStorage.setItem('actividades', JSON.stringify(data));
        } else if (res.status !== 200) {
          setError('⚠ Error al cargar actividades');
        }
      } catch (err) {
        console.error('Error al obtener actividades:', err);
        setError('🚫 Error de conexión con el servidor');
      } finally {
        setLoading(false);
      }
    };

    fetchActividades();
  }, []);

  const handleVerDetalle = (actividad: Actividad) => {
    localStorage.setItem('actividadSeleccionada', JSON.stringify(actividad));
    navigate('/actividad-detalle'); 
  };

  const handleAgregar = () => {
    navigate('/actividades/nueva');
  };

  return (
    <div>
      <NavbarAdmin />

      <div className="page-container">
	  <div className="header-actividades">
	    <div>
	      <h2>Actividades</h2>
	      <p className="subtitulo">
	        Administrá las actividades deportivas del club.
	      </p>
	    </div>
	  </div>

	  {loading && <p className="estado">Cargando actividades...</p>}

	  {error && <p className="error-box">{error}</p>}

	  {!loading && !error && (
	    <>
	      {actividades.length > 0 ? (
	        <>
	          <div className="actividad-lista">
	            {actividades.map((act) => (
	              <button
	                key={act.id}
	                className="actividad-row"
	                onClick={() => handleVerDetalle(act)}
	              >
	                <div className="actividad-contenido">
	                  <h3>{act.nombre}</h3>
	                  <p>{act.descripcion || "Sin descripción"}</p>
	                </div>

	                <div className="actividad-accion">
	                  <span>Ver detalle</span>
	                  <i className="fa-solid fa-chevron-right"></i>
	                </div>
	              </button>
	            ))}
	          </div>

	          <div className="nueva-actividad-wrapper">
	            <button className="nueva-actividad-btn" onClick={handleAgregar}>
	              <i className="fa-solid fa-plus"></i>
	              Nueva actividad
	            </button>
	          </div>
	        </>
	      ) : (
	        <div className="sin-actividades">
	          <h3>No hay actividades registradas</h3>
	          <p>Comenzá creando la primera actividad del club.</p>
	          <button className="nueva-actividad-btn" onClick={handleAgregar}>
	            <i className="fa-solid fa-plus"></i>
	            Nueva actividad
	          </button>
	        </div>
	      )}
	    </>
	  )}
      </div>
    </div>
  );
};

export default ListaActividades;
