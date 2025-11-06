import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NavbarAdmin from "./NavbarAdmin";
import "../styles/AgregarHorario.css";

interface Horario {
  id?: number;
  dia: string;
  horaDesde: string;
  horaHasta: string;
  id_actividad: number;
}

const AgregarHorario: React.FC = () => {
  const [horarios, setHorarios] = useState<Horario[]>([]);
  const [nuevoHorario, setNuevoHorario] = useState<Horario>({
    dia: "",
    horaDesde: "",
    horaHasta: "",
    id_actividad: 0,
  });
  const [editandoId, setEditandoId] = useState<number | null>(null);
  const [horarioEditado, setHorarioEditado] = useState<Horario | null>(null);
  const [mensaje, setMensaje] = useState<string>("");
  const navigate = useNavigate();

  useEffect(() => {
    const stored = localStorage.getItem("actividad");
    if (stored) {
      const actividad = JSON.parse(stored);
      setNuevoHorario((prev) => ({ ...prev, id_actividad: actividad.id || 0 }));
      cargarHorarios(actividad.id);
    }
  }, []);

  const cargarHorarios = async (id_actividad: number) => {
    try {
      const response = await fetch(
        http://localhost:8080/club/horario?action=buscar_por_actividad&id_actividad=${id_actividad}
      );
      if (!response.ok) throw new Error("Error al obtener horarios");
      const data = await response.json();
      setHorarios(data);
    } catch (error) {
      console.error("❌ Error cargando horarios:", error);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setNuevoHorario({ ...nuevoHorario, [e.target.name]: e.target.value });
  };

  const handleAgregar = async () => {
    try {
      const formData = new URLSearchParams();
      formData.append("action", "agregar");
      formData.append("dia", nuevoHorario.dia);
      formData.append("hora_desde", nuevoHorario.horaDesde);
      formData.append("hora_hasta", nuevoHorario.horaHasta);
      formData.append("id_actividad", nuevoHorario.id_actividad.toString());

      const response = await fetch("http://localhost:8080/club/horario", {
        method: "POST",
        body: formData,
      });

      if (!response.ok) throw new Error("Error al agregar horario");

      await cargarHorarios(nuevoHorario.id_actividad);
      setNuevoHorario({ ...nuevoHorario, dia: "", horaDesde: "", horaHasta: "" });
    } catch (error) {
      console.error("❌ Error agregando horario:", error);
    }
  };

  const handleEliminar = async (id: number) => {
    if (!window.confirm("¿Estás seguro de que querés eliminar este horario?")) return;
    try {
      await fetch(http://localhost:8080/club/horario?action=eliminar&id=${id}, {
        method: "GET",
      });
      setHorarios((prev) => prev.filter((h) => h.id !== id));
    } catch (error) {
      console.error("❌ Error eliminando horario:", error);
    }
  };

  const handleEditarClick = (horario: Horario) => {
    setEditandoId(horario.id!);
    setHorarioEditado({ ...horario });
    setMensaje("");
  };

  const handleEditarChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    if (horarioEditado) {
      setHorarioEditado({ ...horarioEditado, [e.target.name]: e.target.value });
    }
  };

  const handleGuardarCambios = async (horarioEditado: any) => {
    if (!horarioEditado?.id) {
      alert("Error: No se puede actualizar este horario (ID no encontrado).");
      return;
    }

    const stored = localStorage.getItem("actividad");
    const actividad = stored ? JSON.parse(stored) : null;
    const idActividad = actividad?.id || horarioEditado.id_actividad;

    try {
      const response = await fetch(http://localhost:8080/club/horario?action=actualizar, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          id: horarioEditado.id,
          dia: horarioEditado.dia,
          hora_desde: horarioEditado.horaDesde,
          hora_hasta: horarioEditado.horaHasta,
          id_actividad: idActividad,
        }),
      });

      if (!response.ok) throw new Error("Error al actualizar horario");

      setMensaje("✅ Horario actualizado correctamente.");
      setEditandoId(null);
      await cargarHorarios(idActividad);
      setTimeout(() => setMensaje(""), 3000);
    } catch (error) {
      console.error("❌ Error actualizando horario:", error);
      alert("Error al actualizar el horario.");
    }
  };

  return (
    <div>
      <NavbarAdmin />

      <div className="page-container-horario">
        <h2>Horarios de la Actividad</h2>

        {mensaje && <div className="mensaje-exito">{mensaje}</div>}

        {/* 🔹 Contenedor general de todas las tarjetas */}
        <div className="horario-contenedor">
          {/* 💚 Tarjeta de agregar horario */}
          <div className="horario-card horario-form">
            <h3 style={{ color: "#20321E", marginBottom: "10px" }}>Agregar horario</h3>

            <select name="dia" value={nuevoHorario.dia} onChange={handleChange}>
              <option value="">Seleccionar día</option>
              <option value="Lunes">Lunes</option>
              <option value="Martes">Martes</option>
              <option value="Miércoles">Miércoles</option>
              <option value="Jueves">Jueves</option>
              <option value="Viernes">Viernes</option>
              <option value="Sábado">Sábado</option>
              <option value="Domingo">Domingo</option>
            </select>

            <input
              type="time"
              name="horaDesde"
              value={nuevoHorario.horaDesde}
              onChange={handleChange}
            />

            <input
              type="time"
              name="horaHasta"
              value={nuevoHorario.horaHasta}
              onChange={handleChange}
            />

            <button onClick={handleAgregar}>Agregar horario</button>
          </div>

          {/* 💛 Lista de horarios existentes */}
          {horarios.map((h) => (
            <div key={h.id} className="horario-card horario-item">
              {editandoId === h.id ? (
                <div className="flex flex-col gap-2">
                  <select
                    name="dia"
                    value={horarioEditado?.dia || ""}
                    onChange={handleEditarChange}
                  >
                    <option value="Lunes">Lunes</option>
                    <option value="Martes">Martes</option>
                    <option value="Miércoles">Miércoles</option>
                    <option value="Jueves">Jueves</option>
                    <option value="Viernes">Viernes</option>
                    <option value="Sábado">Sábado</option>
                    <option value="Domingo">Domingo</option>
                  </select>

                  <input
                    type="time"
                    name="horaDesde"
                    value={horarioEditado?.horaDesde || ""}
                    onChange={handleEditarChange}
                  />
                  <input
                    type="time"
                    name="horaHasta"
                    value={horarioEditado?.horaHasta || ""}
                    onChange={handleEditarChange}
                  />
                </div>
              ) : (
                <div>
                  <p className="font-semibold">{h.dia}</p>
                  <p>
                    {h.horaDesde} - {h.horaHasta}
                  </p>
                </div>
              )}

              <div className="flex gap-2">
                {editandoId === h.id ? (
                  <button
                    onClick={() => handleGuardarCambios(horarioEditado)}
                    className="modificar"
                  >
                    Guardar cambios
                  </button>
                ) : (
                  <button
                    onClick={() => handleEditarClick(h)}
                    className="modificar"
                  >
                    Modificar
                  </button>
                )}
                <button
                  onClick={() => h.id && handleEliminar(h.id)}
                  className="eliminar"
                >
                  Eliminar
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* 🔻 Botón volver al final */}
        <button onClick={() => navigate("/actividades")} className="btn-volver">
          ← Volver
        </button>
      </div>
    </div>
  );


};

export default AgregarHorario;