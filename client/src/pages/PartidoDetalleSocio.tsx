import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NavbarSocio from "./NavbarSocio";
import '../styles/PartidoDetalleSocio.css';


interface Partido {
	id: number;
	fecha: string;
	oponente: string;
	hora_desde: string;
	hora_hasta: string;
	categoria: string;
	precio_entrada: number;
	id_cancha: number;
	id_actividad:number;
}

const PartidoDetalleSocio: React.FC = () => {
  const [partido, setPartido] = useState<Partido | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [editando, setEditando] = useState(false);
  const [mensajeExito, setMensajeExito] = useState<string | null>(null);
  const [actividades, setactividades] = useState<any[]>([]);
  const [canchas, setCanchas] = useState<any[]>([]);
  const [nombreActividad, setNombreActividad] = useState<string>("");
  const [nombreCancha, setNombreCancha] = useState<string>("");


  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    setError(null);
    const storedPartido = localStorage.getItem("partidoSeleccionado");
    if (storedPartido) {
      try {
        const parsed = JSON.parse(storedPartido);
        setPartido(parsed);
      } catch {
        setError("Error al leer los datos del partido.");
      }
    } else {
      setError("No se encontró información del partido.");
    }
  }, []);

  useEffect(() => {
    const fetchDetalles = async () => {
      if (!partido
	  ) return;
      try {
        if (partido.id_actividad) {
          const resAct = await fetch(
            `http://localhost:8080/club/actividad?action=buscar&id=${partido.id_actividad}`
          );
          const dataAct = await resAct.json();
          setNombreActividad(dataAct?.nombre|| "Actividad no encontrada");
        }
		
		setNombreCancha("Partido en cancha del oponente");
        if (partido.id_cancha && partido.id_cancha !== 0) {
          const resCancha = await fetch(
            `http://localhost:8080/club/cancha?action=buscar&id=${partido.id_cancha}`
          );
          const text = await resCancha.text();
          if (text) {
            const dataCancha = JSON.parse(text);
            setNombreCancha(dataCancha?.descripcion || "Cancha no encontrada");
          }
        }
      } catch {
        console.error("Error al obtener profesor/cancha");
      }
    };
    fetchDetalles();
  }, [partido]);

  useEffect(() => {
    if (mensajeExito) {
      const timer = setTimeout(() => setMensajeExito(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [mensajeExito]);

  const handleVolver = () => {
    localStorage.removeItem("partidoSeleccionado");
    navigate("/partidos-socio");
  };


  return (
    <div>
      <NavbarSocio />

      <div className="page-container">

        {loading && <p>Cargando detalles...</p>}
        {error && <p className="error-box">{error}</p>}

        {partido && (
          <div className="detalle-partido-solo-vista">

            <h2 >
              Los Andes VS {partido.oponente}
            </h2>

            <p>
              <i className="fa-solid fa-calendar-days"></i> Fecha: {partido.fecha}
            </p>

            <p>
              <i className="fa-solid fa-clock"></i> Hora: {partido.hora_desde} — {partido.hora_hasta}
            </p>

            <p>
              <i className="fa-solid fa-location-dot"></i> Cancha:{" "}
              {partido.id_cancha === 0 || partido.id_cancha === null
                ? "Partido en cancha del oponente"
                : nombreCancha}
            </p>

            <p>
              <i className="fa-solid fa-ticket"></i> Precio entrada: ${partido.precio_entrada}
            </p>

            <button onClick={handleVolver}>
              <i className="fa-solid fa-right-left" /> Volver
            </button>

          </div>
        )}
      </div>
    </div>
  );


};

export default PartidoDetalleSocio;