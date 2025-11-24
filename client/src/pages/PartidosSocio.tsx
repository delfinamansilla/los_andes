import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarSocio from './NavbarSocio';
import '../styles/AdminPartidos.css';

interface Partido {
  id: number;
  fecha: string;
  idActividad: number;
  idCancha: number | null;
  oponente: string;
  precio_entrada?: string;

  actividad?: {
    id: number;
    nombre: string;
  };

  cancha?: {
    id: number;
    descripcion: string;
  };
};

const PartidosSocio: React.FC = () => {
  const [partidos, setPartidos] = useState<Partido[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();
  const [filtro, setFiltro] = useState<"semana" | "todos">("semana");

  useEffect(() => {
    cargarPartidos();
  }, [filtro]);
  
  const obtenerFechasSemana = () => {
    const hoy = new Date();
    const diaSemana = hoy.getDay(); 

    const lunes = new Date(hoy);
    lunes.setDate(hoy.getDate() - (diaSemana === 0 ? 6 : diaSemana - 1));

    const domingo = new Date(lunes);
    domingo.setDate(lunes.getDate() + 6);

    const desde = lunes.toISOString().split("T")[0];
    const hasta = domingo.toISOString().split("T")[0];

    return { desde, hasta };
  };
  
  const cargarPartidos = async () => {
    setLoading(true);
    setError(null);

    try {
      let url = "";

      if (filtro === "semana") {
        const { desde, hasta } = obtenerFechasSemana();
        url = `https://losandesback-production.up.railway.app/partido?action=listar_por_rango&desde=${desde}&hasta=${hasta}`;
      } else {
        url = `https://losandesback-production.up.railway.app/partido?action=listar`;
      }

      const res = await fetch(url, { method: "GET", credentials: "include" });


      const texto = await res.text();
      const data = texto ? JSON.parse(texto) : [];

      if (!Array.isArray(data)) {
        setError("Error al cargar partidos");
        return;
      }

   
      const partidosConDatos = await Promise.all(
        data.map(async (p) => {
          let actividad = null;
          let cancha = null;

         
          try {
            const actRes = await fetch(
              `https://losandesback-production.up.railway.app/actividad?action=buscar&id=${p.idActividad ?? p.id_actividad}`
            );
            const actTxt = await actRes.text();
            actividad = actTxt ? JSON.parse(actTxt) : null;
          } catch (_) {
            actividad = null;
          }

          const idCancha = p.idCancha ?? p.id_cancha;
          if (idCancha && idCancha !== 0) {
            try {
              const canchaRes = await fetch(
                `https://losandesback-production.up.railway.app/cancha?action=buscar&id=${idCancha}`
              );
              const canchaTxt = await canchaRes.text();
              cancha = canchaTxt ? JSON.parse(canchaTxt) : null;
            } catch (_) {
              cancha = null;
            }
          }

          return {
            ...p,
            actividad,
            cancha,
          };
        })
      );

      setPartidos(partidosConDatos);

    } catch (err) {
      console.error(err);
      setError("Error de conexión con el servidor");
    } finally {
      setLoading(false);
    }
  };

  
  const handleVerDetalle = (partido: Partido) => {
    localStorage.setItem('partidoSeleccionado', JSON.stringify(partido));
    navigate('/partido-detalle-socio'); 
  };

 

  return (
      <div>
        <NavbarSocio />
        <div className="page-container">
          <h2>Lista de Partidos</h2>
		  
		  <div >
		    <button
		      className={filtro === "semana" ? "btn-activo" : ""}
		      onClick={() => setFiltro("semana")}
		    >
		      Esta semana
		    </button>

		    <button
		      className={filtro === "todos" ? "btn-activo" : ""}
		      onClick={() => setFiltro("todos")}
		    >
		      Todos los partidos
		    </button>
		  </div>

          {loading && <p>Cargando Partidos...</p>}
          {error && <p className="error-box">{error}</p>}

          {!loading && !error && (
            <>
              {partidos.length > 0 ? (
                <div className="partido-lista">

                  {partidos.map((p) => (
                    <button
                      key={p.id}
                      className="partido-btn"
                      onClick={() => handleVerDetalle(p)}
                    >
					<div className="partido-card-content">

					  
					  <h3 className="partido-titulo">
					    Los Andes <span className="vs">VS</span> {p.oponente}
					  </h3>

					 
					  <p className="partido-fecha">
					    <i className="fa-solid fa-calendar-days"></i>
					    {p.fecha}
					  </p>

					  
					  <p className="partido-actividad">
					    <i className="fa-solid fa-futbol"></i>
					    {p.actividad?.nombre}
					  </p>

					  
					  <p className="partido-cancha">
					    <i className="fa-solid fa-location-dot"></i>
					    {p.cancha ? p.cancha.descripcion : "Partido en cancha del oponente"}
					  </p>

					 
					  <p className="partido-precio">
					    <i className="fa-solid fa-ticket"></i>
					    {p.precio_entrada || "Sin precio"}
					  </p>

					</div>
                    </button>
                  ))}
                </div>
              ) : (
                <>
                  <p>No hay partidos registrados.</p>
                </>
              )}
            </>
          )}
        </div>
      </div>
    );
};

export default PartidosSocio;