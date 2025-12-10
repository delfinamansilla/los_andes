import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NavbarAdmin from "./NavbarAdmin";
import Modal from "./Modal";
import '../styles/PartidoDetalle.css';


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

const PartidoDetalle: React.FC = () => {
  const [partido, setPartido] = useState<Partido | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [editando, setEditando] = useState(false);
  const [mensajeExito, setMensajeExito] = useState<string | null>(null);
  const [actividades, setactividades] = useState<any[]>([]);
  const [canchas, setCanchas] = useState<any[]>([]);
  const [nombreActividad, setNombreActividad] = useState<string>("");
  const [nombreCancha, setNombreCancha] = useState<string>("");
  const [modalAbierto, setModalAbierto] = useState(false);
  const [modalMensaje, setModalMensaje] = useState("");
  const [modalAccion, setModalAccion] = useState<(() => void) | null>(null);

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
    const fetchData = async () => {
      try {
        const [actRes, canchaRes] = await Promise.all([
          fetch("http://localhost:8080/club/actividad?action=listar"),
          fetch("http://localhost:8080/club/cancha?action=listar"),
        ]);
        const actividadesData = await actRes.json();
        const canchasData = await canchaRes.json();
        setactividades(actividadesData);
        setCanchas(canchasData);
      } catch {
        console.error("Error al cargar listas");
      } finally {
        setLoading(false);
      }
    };
    fetchData();
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
    navigate("/admin-partidos");
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    if (!partido) return;
    const { name, value } = e.target;
    setPartido({ ...partido, [name]: value });
  };

  const handleGuardarCambios = async () => {
    if (!partido) return;
    try {
      const res = await fetch(`http://localhost:8080/club/partido?action=actualizar`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          id: partido.id,
		  fecha: partido.fecha.slice(0, 10),
		  oponente: partido.oponente,
		  hora_desde: partido.hora_desde,
		  hora_hasta: partido.hora_hasta,
		  categoria: partido.categoria,
		  precio_entrada: Number(partido.precio_entrada),
		  id_cancha: partido.id_cancha === 0 ? null : Number(partido.id_cancha),
		  id_actividad:Number(partido.id_actividad)
        }),
      });
      const text = await res.text();
      const data = JSON.parse(text);
      if (data.status === "ok") {
        setMensajeExito("✅ Partido actualizado correctamente");
        localStorage.setItem("partidoSeleccionada", JSON.stringify(partido));
        setEditando(false);
      } else {
        setModalMensaje(data.error || "Error al actualizar el partido");
        setModalAbierto(true);
      }
    } catch {
      setModalMensaje("Error al actualizar el partido");
      setModalAbierto(true);
    }
  };

  const handleEliminar = () => {
    if (!partido) return;
    setModalMensaje(`¿Estás seguro de que querés eliminar el partido "${partido.fecha}" Los andes VS "${partido.oponente},"?`);
    setModalAccion(() => eliminarConfirmado);
    setModalAbierto(true);
  };

  const eliminarConfirmado = async () => {
    if (!partido) return;
    try {
      const res = await fetch(
        `http://localhost:8080/club/partido?action=eliminar&id=${partido.id}`,
        { method: "GET", credentials: "include" }
      );
      const text = await res.text();
      const data = JSON.parse(text);
      if (data.status === "ok") {
        navigate("/admin-partidos");
      } else {
        setModalMensaje(data.error || "Error al eliminar el partido");
        setModalAccion(null);
        setModalAbierto(true);
      }
    } catch {
      setModalMensaje("Error al eliminar el partido");
      setModalAccion(null);
      setModalAbierto(true);
    }
  };



  return (
    <div>
      <NavbarAdmin />

      <div className="page-container">

        {loading && <p>Cargando detalles...</p>}
        {error && <p className="error-box">{error}</p>}
        {mensajeExito && <div className="mensaje-exito">{mensajeExito}</div>}

        {partido && (
          <>
            <h2>
              {editando
                ? "Editando partido"
                : `${partido.fecha} — Los Andes VS ${partido.oponente}`}
            </h2>

            <div className="detalle-actividad">

              <label>
                Fecha:
                <input
                  type="date"
                  name="fecha"
                  value={partido.fecha.slice(0, 10)}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Oponente:
                <input
                  type="text"
                  name="oponente"
                  value={partido.oponente}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Hora desde:
                <input
                  type="time"
                  name="hora_desde"
                  value={partido.hora_desde}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Hora hasta:
                <input
                  type="time"
                  name="hora_hasta"
                  value={partido.hora_hasta}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Categoría:
                <input
                  type="text"
                  name="categoria"
                  value={partido.categoria}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Precio entrada:
                <input
                  type="number"
                  name="precio_entrada"
                  value={partido.precio_entrada}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Actividad:
                {editando ? (
                  <select
                    name="id_actividad"
                    value={partido.id_actividad}
                    onChange={handleChange}
                  >
                    <option value="">Seleccionar actividad</option>
                    {actividades.map((a) => (
                      <option key={a.id} value={a.id}>
                        {a.nombre}
                      </option>
                    ))}
                  </select>
                ) : (
                  <span>{nombreActividad}</span>
                )}
              </label>

              <label>
                Cancha:
                {editando ? (
                  <select
                    name="id_cancha"
                    value={partido.id_cancha}
                    onChange={handleChange}
                  >
                    <option value="0">Oponente (sin cancha)</option>
                    {canchas.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.descripcion} (N° {c.nro_cancha})
                      </option>
                    ))}
                  </select>
                ) : (
                  <span>{nombreCancha}</span>
                )}
              </label>

            </div>

            <div>
              <button onClick={handleVolver}>
                <i className="fa-solid fa-right-left" /> Volver
              </button>

              {!editando ? (
                <>
                  <button onClick={() => setEditando(true)}>
                    <i className="fa-solid fa-pen" /> Modificar
                  </button>

                  <button className="eliminar" onClick={handleEliminar}>
                    <i className="fa-solid fa-trash" /> Eliminar
                  </button>
                </>
              ) : (
                <button className="guardar" onClick={handleGuardarCambios}>
                  <i className="fa-solid fa-floppy-disk" /> Guardar cambios
                </button>
              )}
            </div>
          </>
        )}
      </div>

      {modalAbierto && (
        <Modal
          mensaje={modalMensaje}
          onCancelar={() => setModalAbierto(false)}
          onConfirmar={modalAccion || (() => setModalAbierto(false))}
        />
      )}
    </div>
  );

};

export default PartidoDetalle;