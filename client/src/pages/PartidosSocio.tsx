import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Footer from './Footer';
import NavbarSocio from './NavbarSocio';
import '../styles/AdminPartidos.css';
import { API_URL } from "../config";

interface Partido {
  id: number;
  fecha: string;
  idActividad: number;
  idCancha: number | null;
  oponente: string;
  precio_entrada?: number;
  resultado: string | null; 
  categoria: string;        

  actividad?: {
    id: number;
    nombre: string;
  };
  cancha?: {
    id: number;
    descripcion: string;
  };
}

const PartidosSocio: React.FC = () => {
  const [partidos, setPartidos] = useState<Partido[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  const [filtro, setFiltro] = useState<"semana" | "proximos" | "resultados">("semana");

  useEffect(() => {
    cargarPartidos();
  }, [filtro]);
  
  const obtenerRangoFechas = () => {
    const hoy = new Date();
    const desde = new Date();
    const hasta = new Date(); 

    if (filtro === "semana") {
      const diaSemana = hoy.getDay();
      desde.setDate(hoy.getDate() - (diaSemana === 0 ? 6 : diaSemana - 1));
      hasta.setDate(desde.getDate() + 6);
    } else if (filtro === "proximos") {
      desde.setDate(hoy.getDate()); 
      hasta.setDate(hoy.getDate() + 30);
    } else {
      desde.setDate(hoy.getDate() - 30);
      hasta.setDate(hoy.getDate() - 1);
    }

    return { 
      desde: desde.toISOString().split("T")[0], 
      hasta: hasta.toISOString().split("T")[0] 
    };
  };

  const cargarPartidos = async () => {
    setLoading(true);
    setError(null);
    try {
      const { desde, hasta } = obtenerRangoFechas();
      const url = `${API_URL}/partido?action=listar_por_rango&desde=${desde}&hasta=${hasta}`;

      const res = await fetch(url);
      const data = await res.json();

      if (!Array.isArray(data)) {
        setPartidos([]);
        return;
      }

      const partidosConDatos = await Promise.all(
        data.map(async (p: any) => {
          let actividad = null;
          let cancha = null;
          try {
            const idAct = p.id_actividad || p.idActividad;
            const actRes = await fetch(`${API_URL}/actividad?action=buscar&id=${idAct}`);
            actividad = await actRes.json();

            const idCan = p.id_cancha || p.idCancha;
            if (idCan && idCan !== 0) {
              const canchaRes = await fetch(`${API_URL}/cancha?action=buscar&id=${idCan}`);
              cancha = await canchaRes.json();
            }
          } catch (e) { console.error(e); }
          return { ...p, actividad, cancha };
        })
      );

      setPartidos(partidosConDatos.sort((a, b) => {
          if (filtro === "proximos") return new Date(a.fecha).getTime() - new Date(b.fecha).getTime();
          return new Date(b.fecha).getTime() - new Date(a.fecha).getTime();
      }));

    } catch (err) {
      setError("Error de conexión");
    } finally {
      setLoading(false);
    }
  };

  const handleVerDetalle = (partido: Partido) => {
    localStorage.setItem('partidoSeleccionado', JSON.stringify(partido));
    navigate('/partido-detalle-socio'); 
  };

  return (
    <div className="admin-partidos-page">
      <NavbarSocio />
      <div className="page-container">
        <div className="header-seccion">
            <h2>Cartelera de Partidos</h2>
            
            <div className="controles-row">
                <div className="tabs-container">
                    <button 
                        className={`tab-btn ${filtro === "semana" ? "active" : ""}`}
                        onClick={() => setFiltro("semana")}
                    >
                        Esta semana
                    </button>
                    <button 
                        className={`tab-btn ${filtro === "proximos" ? "active" : ""}`}
                        onClick={() => setFiltro("proximos")}
                    >
                        Próximos 30 días
                    </button>
                    <button 
                        className={`tab-btn ${filtro === "resultados" ? "active" : ""}`}
                        onClick={() => setFiltro("resultados")}
                    >
                        Resultados
                    </button>
                </div>
            </div>
        </div>

        {loading ? (
          <p className="loading-text">Cargando partidos...</p>
        ) : (
          <div className="partido-lista">
            {partidos.length > 0 ? (
              partidos.map((p) => (
                <button key={p.id} className="partido-btn" onClick={() => handleVerDetalle(p)}>
                  <div className="partido-card-content">
                    
                    <div className="card-top">
                      <span className="categoria-tag">{p.categoria}</span>
                      <span className={`estado-tag ${new Date(p.fecha) < new Date() ? 'pasados' : 'proximos'}`}>
                        {new Date(p.fecha) < new Date() ? "Finalizado" : "Programado"}
                      </span>
                    </div>

                    <h3 className="partido-titulo">
                      Los Andes <span className="vs">VS</span> {p.oponente}
                    </h3>

                    {p.resultado && (
                      <div className="resultado-box">
                        {p.resultado}
                      </div>
                    )}

                    <div className="partido-info-grid">
                      <p><i className="fa-solid fa-calendar-days"></i> {p.fecha}</p>
                      <p><i className="fa-solid fa-futbol"></i> {p.actividad?.nombre}</p>
                      <p><i className="fa-solid fa-location-dot"></i> 
                         <span>{p.cancha ? p.cancha.descripcion : "Cancha Oponente"}</span>
                      </p>
                    </div>
                    
                    <div className="footer-card">
                       Ver detalles
                    </div>
                  </div>
                </button>
              ))
            ) : (
              <div className="no-partidos">
                <p style={{color: 'white', textAlign: 'center', marginTop: '20px'}}>
                    No hay partidos registrados en este periodo.
                </p>
              </div>
            )}
          </div>
        )}
      </div>
      <Footer />
    </div>
  );
};

export default PartidosSocio;