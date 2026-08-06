import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NavbarSocio from "./NavbarSocio";
import Footer from './Footer';
import '../styles/PartidoDetalleSocio.css';
import { API_URL } from "../config";

interface Partido {
	id: number;
	fecha: string;
	oponente: string;
	hora_desde: string;
	hora_hasta: string;
	categoria: string;
	precio_entrada: number;
	id_cancha: number;
	id_actividad: number;
    resultado: string | null; 
}

const PartidoDetalleSocio: React.FC = () => {
  const [partido, setPartido] = useState<Partido | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [nombreActividad, setNombreActividad] = useState<string>("");
  const [nombreCancha, setNombreCancha] = useState<string>("");

  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    const storedPartido = localStorage.getItem("partidoSeleccionado");
    if (storedPartido) {
      setPartido(JSON.parse(storedPartido));
    } else {
      setError("No se encontró información del partido.");
    }
  }, []);

  useEffect(() => {
    const fetchDetalles = async () => {
      if (!partido) return;
      try {
        const resAct = await fetch(`${API_URL}/actividad?action=buscar&id=${partido.id_actividad}`);
        const dataAct = await resAct.json();
        setNombreActividad(dataAct?.nombre || "Actividad no encontrada");

        if (partido.id_cancha && partido.id_cancha !== 0) {
          const resCancha = await fetch(`${API_URL}/cancha?action=buscar&id=${partido.id_cancha}`);
          const dataCancha = await resCancha.json();
          setNombreCancha(dataCancha?.descripcion || "Cancha no encontrada");
        } else {
          setNombreCancha("Partido en cancha del oponente");
        }
      } catch (e) {
        console.error("Error cargando detalles", e);
      } finally {
        setLoading(false);
      }
    };
    fetchDetalles();
  }, [partido]);

  const handleVolver = () => {
    localStorage.removeItem("partidoSeleccionado");
    navigate("/partidos-socio");
  };

  return (
    <div className="admin-partidos-page"> 
      <NavbarSocio />

      <div className="page-container-detalle">
        {loading ? (
          <p className="loading-text">Cargando detalles...</p>
        ) : error ? (
          <p className="error-box">{error}</p>
        ) : (
          partido && (
            <div className="card-detalle-socio">
              
              <div className="badge-categoria">{partido.categoria}</div>
              
              <h2>Los Andes <span className="vs">VS</span> {partido.oponente}</h2>

              {/* LÓGICA DEL RESULTADO: Solo se muestra si tiene valor cargado */}
              {partido.resultado ? (
                <div className="resultado-final-box">
                  <span className="label-res">Resultado Final</span>
                  <div className="marcador">{partido.resultado}</div>
                </div>
              ) : (
                <div className="proximamente-aviso">
                  <i className="fa-solid fa-clock"></i> Partido en espera de resultado
                </div>
              )}

              <div className="info-grid-detalle">
                <div className="item-info">
                  <i className="fa-solid fa-person-running"></i>
                  <div>
                    <label>Actividad</label>
                    <span>{nombreActividad}</span>
                  </div>
                </div>

                <div className="item-info">
                  <i className="fa-solid fa-calendar-day"></i>
                  <div>
                    <label>Fecha del Encuentro</label>
                    <span>{partido.fecha}</span>
                  </div>
                </div>

                <div className="item-info">
                  <i className="fa-solid fa-clock"></i>
                  <div>
                    <label>Horario</label>
                    <span>{partido.hora_desde} hs — {partido.hora_hasta} hs</span>
                  </div>
                </div>

                <div className="item-info">
                  <i className="fa-solid fa-location-dot"></i>
                  <div>
                    <label>Ubicación / Cancha</label>
                    <span>{nombreCancha}</span>
                  </div>
                </div>

                <div className="item-info">
                  <i className="fa-solid fa-ticket"></i>
                  <div>
                    <label>Precio Entrada</label>
                    <span>${partido.precio_entrada}</span>
                  </div>
                </div>
              </div>

              <button className="btn-volver-estilizado" onClick={handleVolver}>
                <i className="fa-solid fa-chevron-left"></i> Volver a la cartelera
              </button>

            </div>
          )
        )}
      </div>
      <Footer />
    </div>
  );
};

export default PartidoDetalleSocio;