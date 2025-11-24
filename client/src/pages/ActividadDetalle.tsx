import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NavbarAdmin from "./NavbarAdmin";
import Modal from "./Modal";
import "../styles/ActividadDetalle.css";

interface Actividad {
  id: number;
  nombre: string;
  descripcion: string;
  cupo: number;
  inscripcion_desde: string;
  inscripcion_hasta: string;
  id_profesor: number;
  id_cancha: number;
}

const ActividadDetalle: React.FC = () => {
  const [actividad, setActividad] = useState<Actividad | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [editando, setEditando] = useState(false);
  const [mensajeExito, setMensajeExito] = useState<string | null>(null);
  const [profesores, setProfesores] = useState<any[]>([]);
  const [canchas, setCanchas] = useState<any[]>([]);
  const [nombreProfesor, setNombreProfesor] = useState<string>("");
  const [nombreCancha, setNombreCancha] = useState<string>("");
  const [modalAbierto, setModalAbierto] = useState(false);
  const [modalMensaje, setModalMensaje] = useState("");
  const [modalAccion, setModalAccion] = useState<(() => void) | null>(null);

  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    setError(null);
    const storedActividad = localStorage.getItem("actividadSeleccionada");
    if (storedActividad) {
      try {
        const parsed = JSON.parse(storedActividad);
        setActividad(parsed);
      } catch {
        setError("Error al leer los datos de la actividad.");
      }
    } else {
      setError("No se encontró información de la actividad.");
    }
    const fetchData = async () => {
      try {
        const [profRes, canchaRes] = await Promise.all([
          fetch("https://losandesback-production.up.railway.app/profesor?action=listar"),
          fetch("https://losandesback-production.up.railway.app/cancha?action=listar"),
        ]);
        const profesoresData = await profRes.json();
        const canchasData = await canchaRes.json();
        setProfesores(profesoresData);
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
      if (!actividad) return;
      try {
        if (actividad.id_profesor) {
          const resProf = await fetch(
            `https://losandesback-production.up.railway.app/profesor?action=buscar&id=${actividad.id_profesor}`
          );
          const dataProf = await resProf.json();
          setNombreProfesor(dataProf?.nombre_completo || "Profesor no encontrado");
        }
        if (actividad.id_cancha) {
          const resCancha = await fetch(
            `https://losandesback-production.up.railway.app/cancha?action=buscar&id=${actividad.id_cancha}`
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
  }, [actividad]);

  useEffect(() => {
    if (mensajeExito) {
      const timer = setTimeout(() => setMensajeExito(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [mensajeExito]);

  const handleVolver = () => {
    localStorage.removeItem("actividadSeleccionada");
    navigate("/actividades");
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    if (!actividad) return;
    const { name, value } = e.target;
    setActividad({ ...actividad, [name]: value });
  };

  const handleGuardarCambios = async () => {
    if (!actividad) return;
    try {
      const res = await fetch(`https://losandesback-production.up.railway.app/actividad?action=actualizar`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          id: actividad.id,
          nombre: actividad.nombre,
          descripcion: actividad.descripcion,
          cupo: Number(actividad.cupo),
          inscripcion_desde: actividad.inscripcion_desde.slice(0, 10),
          inscripcion_hasta: actividad.inscripcion_hasta.slice(0, 10),
          id_profesor: Number(actividad.id_profesor),
          id_cancha: Number(actividad.id_cancha),
        }),
      });
      const text = await res.text();
      const data = JSON.parse(text);
      if (data.status === "ok") {
        setMensajeExito("✅ Actividad actualizada correctamente");
        localStorage.setItem("actividadSeleccionada", JSON.stringify(actividad));
        setEditando(false);
      } else {
        setModalMensaje(data.error || "Error al actualizar la actividad");
        setModalAbierto(true);
      }
    } catch {
      setModalMensaje("Error al actualizar la actividad");
      setModalAbierto(true);
    }
  };

  const handleEliminar = () => {
    if (!actividad) return;
    setModalMensaje(`¿Estás seguro de que querés eliminar la actividad "${actividad.nombre}"?`);
    setModalAccion(() => eliminarConfirmado);
    setModalAbierto(true);
  };

  const eliminarConfirmado = async () => {
    if (!actividad) return;
    try {
      const res = await fetch(
        `https://losandesback-production.up.railway.app/actividad?action=eliminar&id=${actividad.id}`,
        { method: "GET", credentials: "include" }
      );
      const text = await res.text();
      const data = JSON.parse(text);
      if (data.status === "ok") {
        navigate("/actividades");
      } else {
        setModalMensaje(data.error || "Error al eliminar la actividad");
        setModalAccion(null);
        setModalAbierto(true);
      }
    } catch {
      setModalMensaje("Error al eliminar la actividad");
      setModalAccion(null);
      setModalAbierto(true);
    }
  };

  const handleVerHorarios = () => {
    if (!actividad) return;
    localStorage.setItem("actividad", JSON.stringify(actividad));
    navigate("/agregar-horario");
  };

  return (
    <div>
      <NavbarAdmin />
      <div className="page-container">
        {loading && <p>Cargando detalles...</p>}
        {error && <p className="error-box">{error}</p>}
        {mensajeExito && <div className="mensaje-exito">{mensajeExito}</div>}
        {actividad && (
          <>
            <h2>{editando ? "Editando Actividad" : actividad.nombre}</h2>
            <div className="detalle-actividad">
              <label>
                Nombre:
                <input type="text" name="nombre" value={actividad.nombre} onChange={handleChange} disabled={!editando} />
              </label>
              <label>
                Descripción:
                <textarea name="descripcion" value={actividad.descripcion} onChange={handleChange} disabled={!editando} />
              </label>
              <label>
                Cupo:
                <input type="number" name="cupo" value={actividad.cupo} onChange={handleChange} disabled={!editando} />
              </label>
              <label>
                Inscripción desde:
                <input type="date" name="inscripcion_desde" value={actividad.inscripcion_desde.slice(0, 10)} onChange={handleChange} disabled={!editando} />
              </label>
              <label>
                Inscripción hasta:
                <input type="date" name="inscripcion_hasta" value={actividad.inscripcion_hasta.slice(0, 10)} onChange={handleChange} disabled={!editando} />
              </label>
              <label>
                Profesor:
                {editando ? (
                  <select name="id_profesor" value={actividad.id_profesor} onChange={handleChange}>
                    <option value="">Seleccionar profesor</option>
                    {profesores.map((p) => (
                      <option key={p.id} value={p.id}>{p.nombre_completo}</option>
                    ))}
                  </select>
                ) : (
                  <span>{nombreProfesor}</span>
                )}
              </label>
              <label>
                Cancha:
                {editando ? (
                  <select name="id_cancha" value={actividad.id_cancha} onChange={handleChange}>
                    <option value="">Seleccionar cancha</option>
                    {canchas.map((c) => (
                      <option key={c.id} value={c.id}>{c.descripcion} (N° {c.nro_cancha})</option>
                    ))}
                  </select>
                ) : (
                  <span>{nombreCancha}</span>
                )}
              </label>
            </div>
            <div>
              <button onClick={handleVolver}><i className="fa-solid fa-right-left"></i> Volver</button>
              <button className="horarios" onClick={handleVerHorarios}><i className="fa-solid fa-calendar-days"></i> Ver horarios</button>
              {!editando ? (
                <>
                  <button onClick={() => setEditando(true)}><i className="fa-solid fa-pen"></i> Modificar</button>
                  <button className="eliminar" onClick={handleEliminar}><i className="fa-solid fa-trash"></i> Eliminar</button>
                </>
              ) : (
                <button className="guardar" onClick={handleGuardarCambios}><i className="fa-solid fa-floppy-disk"></i> Guardar cambios</button>
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

export default ActividadDetalle;