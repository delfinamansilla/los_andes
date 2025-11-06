import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NavbarAdmin from "./NavbarAdmin";
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
          fetch("http://localhost:8080/club/profesor?action=listar"),
          fetch("http://localhost:8080/club/cancha?action=listar"),
        ]);

        const profesoresData = await profRes.json();
        const canchasData = await canchaRes.json();

        setProfesores(profesoresData);
        setCanchas(canchasData);
      } catch (err) {
        console.error("❌ Error al cargar listas:", err);
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
            http://localhost:8080/club/profesor?action=buscar&id=${actividad.id_profesor}
          );
          const dataProf = await resProf.json();
          setNombreProfesor(dataProf?.nombre_completo || "Profesor no encontrado");
        }

        if (actividad.id_cancha) {
          const resCancha = await fetch(
            http://localhost:8080/club/cancha?action=buscar&id=${actividad.id_cancha}
          );
          if (!resCancha.ok) throw new Error(Error HTTP ${resCancha.status});
          const text = await resCancha.text();
          if (!text) throw new Error("Respuesta vacía");
          const dataCancha = JSON.parse(text);
          setNombreCancha(dataCancha?.descripcion || "Cancha no encontrada");
        }
      } catch (err) {
        console.error("❌ Error al obtener profesor/cancha:", err);
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
      const res = await fetch(http://localhost:8080/club/actividad?action=actualizar, {
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
      console.log("Respuesta actualización:", text);

      const data = JSON.parse(text);
      if (data.status === "ok") {
        setMensajeExito("✅ Actividad actualizada correctamente");
        localStorage.setItem("actividadSeleccionada", JSON.stringify(actividad));
        setEditando(false);
      } else {
        setError(data.error || "Error desconocido al actualizar.");
      }
    } catch (err) {
      console.error(err);
      setError("Error al actualizar la actividad.");
    }
  };

  const handleEliminar = async () => {
    if (!actividad) return;

    const confirmar = window.confirm(
      ¿Estás seguro de que querés eliminar la actividad "${actividad.nombre}"?
    );
    if (!confirmar) return;

    try {
      const res = await fetch(
        http://localhost:8080/club/actividad?action=eliminar&id=${actividad.id},
        { method: "GET", credentials: "include" }
      );

      const text = await res.text();
      const data = JSON.parse(text);
      if (data.status === "ok") {
        navigate("/actividades");
      } else {
        alert("⚠ " + (data.error || "Error al eliminar la actividad"));
      }
    } catch (err) {
      console.error("Error al eliminar:", err);
      alert("🚫 Error al eliminar la actividad");
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
                <input
                  type="text"
                  name="nombre"
                  value={actividad.nombre}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Descripción:
                <textarea
                  name="descripcion"
                  value={actividad.descripcion}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Cupo:
                <input
                  type="number"
                  name="cupo"
                  value={actividad.cupo}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Inscripción desde:
                <input
                  type="date"
                  name="inscripcion_desde"
                  value={actividad.inscripcion_desde}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Inscripción hasta:
                <input
                  type="date"
                  name="inscripcion_hasta"
                  value={actividad.inscripcion_hasta}
                  onChange={handleChange}
                  disabled={!editando}
                />
              </label>

              <label>
                Profesor:
                {editando ? (
                  <select
                    name="id_profesor"
                    value={actividad.id_profesor}
                    onChange={handleChange}
                  >
                    <option value="">Seleccionar profesor</option>
                    {profesores.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.nombre_completo}
                      </option>
                    ))}
                  </select>
                ) : (
                  <span>{nombreProfesor}</span>
                )}
              </label>

              <label>
                Cancha:
                {editando ? (
                  <select
                    name="id_cancha"
                    value={actividad.id_cancha}
                    onChange={handleChange}
                  >
                    <option value="">Seleccionar cancha</option>
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

            <div className="form-actions">
              <button onClick={handleVolver}>⬅ Volver</button>
              <button className="horarios" onClick={handleVerHorarios}>
                📅 Ver horarios
              </button>

              {!editando ? (
                <>
                  <button onClick={() => setEditando(true)}>✏ Modificar</button>
                  <button className="eliminar" onClick={handleEliminar}>
                    🗑 Eliminar
                  </button>
                </>
              ) : (
                <button className="guardar" onClick={handleGuardarCambios}>
                  💾 Guardar cambios
                </button>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default ActividadDetalle;