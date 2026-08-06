import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';
import Footer from './Footer';
import '../styles/AdminPartidos.css';
import { API_URL } from "../config";

interface Partido {
  id: number;
  fecha: string;
  idActividad: number;
  idCancha: number | null;
  oponente: string;
  precio_entrada: number;
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

const AdminPartidos: React.FC = () => {
  const [partidos, setPartidos] = useState<Partido[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();
  const [filtro, setFiltro] = useState<"proximos" | "pasados">("proximos");

  useEffect(() => {
    cargarPartidos();
  }, [filtro]);

  const obtenerRangoFechas = () => {
    const hoy = new Date();
    const desde = new Date();
    const hasta = new Date();

    if (filtro === "proximos") {
      hasta.setDate(hoy.getDate() + 15);
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
            const actRes = await fetch(`${API_URL}/actividad?action=buscar&id=${p.id_actividad || p.idActividad}`);
            actividad = await actRes.json();

            if (p.id_cancha || p.idCancha) {
              const canchaRes = await fetch(`${API_URL}/cancha?action=buscar&id=${p.id_cancha || p.idCancha}`);
              cancha = await canchaRes.json();
            }
          } catch (e) {
            console.error("Error cargando detalles del partido", e);
          }

          return { ...p, actividad, cancha };
        })
      );

      const ordenados = partidosConDatos.sort((a, b) => {
        return filtro === "proximos" 
          ? new Date(a.fecha).getTime() - new Date(b.fecha).getTime()
          : new Date(b.fecha).getTime() - new Date(a.fecha).getTime();
      });

      setPartidos(ordenados);
    } catch (err) {
      console.error(err);
      setError("Error de conexión con el servidor");
    } finally {
      setLoading(false);
    }
  };

  const handleVerDetalle = (partido: Partido) => {
    localStorage.setItem('partidoSeleccionado', JSON.stringify(partido));
    navigate('/partido-detalle'); // Aquí es donde el admin podrá editar el resultado
  };

  return (
      <div className="admin-partidos-page">
        <NavbarAdmin />
        <div className="page-container">
          
          <div className="header-seccion">
            <h2>Gestión de Partidos</h2>

            <div className="controles-row">
              <button className="btn-agregar-partido" onClick={() => navigate('/agregar-partido')}>
                <i className="fa-solid fa-plus"></i> Nuevo Partido
              </button>

              <div className="tabs-container">
                <button 
                  className={`tab-btn ${filtro === "proximos" ? "active" : ""}`}
                  onClick={() => setFiltro("proximos")}
                >
                  Próximos Partidos
                </button>
                <button 
                  className={`tab-btn ${filtro === "pasados" ? "active" : ""}`}
                  onClick={() => setFiltro("pasados")}
                >
                  Resultados Recientes
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
                        <span className={`estado-tag ${filtro}`}>
                          {filtro === "proximos" ? "Programado" : "Finalizado"}
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
                        <p className="descripcion-cancha">
                          <i className="fa-solid fa-location-dot"></i> 
                          <span>{p.cancha ? p.cancha.descripcion : "Cancha Oponente"}</span>
                        </p>
                        <p><i className="fa-solid fa-ticket"></i> ${p.precio_entrada}</p>
                      </div>
                      
                      <div className="footer-card">
                         Gestionar {filtro === "pasados" ? "resultado" : "partido"}
                      </div>
                    </div>
                  </button>
                ))
              ) : (
                <div className="no-partidos">
                  <p>No hay partidos en este periodo.</p>
                </div>
              )}
            </div>
          )}
        </div>
        <Footer />
      </div>
    );
  };

  export default AdminPartidos;