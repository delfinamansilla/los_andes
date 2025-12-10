import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NavbarAdmin from "./NavbarAdmin";
import Modal from "./Modal";
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
  const [actividad, setActividad] = useState<any>(null);


  const [mostrarModal, setMostrarModal] = useState(false);
  const [horarioAEliminar, setHorarioAEliminar] = useState<Horario | null>(null);

  const navigate = useNavigate();
  
  const [ocupadosProfesor, setOcupadosProfesor] = useState<Horario[]>([]);
  const [ocupadosCancha, setOcupadosCancha] = useState<Horario[]>([]);

  useEffect(() => {
    const stored = localStorage.getItem("actividad");
	if (stored) {
	  const actividadLS = JSON.parse(stored);  
	  setNuevoHorario((prev) => ({ ...prev, id_actividad: actividadLS.id || 0 }));
	  setActividad(actividadLS);     
	  cargarHorarios(actividadLS.id);
	  cargarDatos(actividadLS);
	}

  }, []);

  const cargarHorarios = async (id_actividad: number) => {
    try {
      const response = await fetch(
        `https://losandesback-production.up.railway.app/horario?action=buscar_por_actividad&id_actividad=${id_actividad}`
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
	
	setMensaje(""); 

	  const desde = nuevoHorario.horaDesde;
	  const hasta = nuevoHorario.horaHasta;
	  const dia = nuevoHorario.dia;


	  const choqueProfesor = ocupadosProfesor.some(h =>
	    h.dia === dia &&
	    desde < h.horaHasta &&
	    hasta > h.horaDesde
	  );

	  if (choqueProfesor) {
	    setMensaje("El profesor no está disponible en ese horario.");
	    return;
	  }


	  const choqueCancha = ocupadosCancha.some(h =>
	    h.dia === dia &&
	    desde < h.horaHasta &&
	    hasta > h.horaDesde
	  );

	  if (choqueCancha) {
	    setMensaje("La cancha no está disponible en ese horario.");
	    return;
	  }
    try {
      const formData = new URLSearchParams();
      formData.append("action", "agregar");
      formData.append("dia", nuevoHorario.dia);
      formData.append("hora_desde", nuevoHorario.horaDesde);
      formData.append("hora_hasta", nuevoHorario.horaHasta);
      formData.append("id_actividad", nuevoHorario.id_actividad.toString());

      const response = await fetch("https://losandesback-production.up.railway.app/horario", {
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

  const handleEliminar = (horario: Horario) => {
    setHorarioAEliminar(horario);
    setMostrarModal(true);
  };

  const confirmarEliminar = async () => {
    if (!horarioAEliminar?.id) return;
    try {
      await fetch(
        `https://losandesback-production.up.railway.app/horario?action=eliminar&id=${horarioAEliminar.id}`,
        { method: "GET" }
      );
      setHorarios((prev) => prev.filter((h) => h.id !== horarioAEliminar.id));
    } catch (error) {
      console.error("❌ Error eliminando horario:", error);
    } finally {
      setMostrarModal(false);
      setHorarioAEliminar(null);
    }
  };

  const cancelarEliminar = () => {
    setMostrarModal(false);
    setHorarioAEliminar(null);
  };

  const handleEditarClick = (horario: Horario) => {
    setEditandoId(horario.id!);
    setHorarioEditado({ ...horario });
  };

  const handleEditarChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    if (horarioEditado) {
      setHorarioEditado({ ...horarioEditado, [e.target.name]: e.target.value });
    }
  };

  const handleGuardarCambios = async (horarioEditado: Horario | null) => {
    if (!horarioEditado?.id) return;

    const payload = {
      id: horarioEditado.id,
      dia: horarioEditado.dia,
      hora_desde: horarioEditado.horaDesde,
      hora_hasta: horarioEditado.horaHasta,
      id_actividad: actividad.id
    };


    try {
      const response = await fetch("https://losandesback-production.up.railway.app/horario?action=actualizar", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!response.ok) throw new Error("Error al actualizar horario");

      await cargarHorarios(actividad.id);
      setEditandoId(null);
    } catch (error) {
      console.error("❌ Error actualizando horario:", error);
    }
  };


  
  const cargarDatos = async (actividad: any) => {
	cargarHorarios(actividad.id);

	const rProf = await fetch(
	  `https://losandesback-production.up.railway.app/horario?action=ocupados_profesor&id_profesor=${actividad.id_profesor}`
	);
	const dataProf = await rProf.json();
	setOcupadosProfesor(Array.isArray(dataProf) ? dataProf : []);

	const rCan = await fetch(
	  `https://losandesback-production.up.railway.app/horario?action=ocupados_cancha&id_cancha=${actividad.id_cancha}`
	);
	const dataCan = await rCan.json();
	setOcupadosCancha(Array.isArray(dataCan) ? dataCan : []);
  };

  
  return (
    <div>
      <NavbarAdmin />

      <div className="page-container-horario">
        <h2>Horarios de la Actividad</h2>
		
		<div className="ocupados-container">

		  <div className="ocupados-box">
		    <h3>Horarios ocupados del Profesor</h3>
		    {ocupadosProfesor.length === 0 ? (
		      <p>No tiene horarios ocupados.</p>
		    ) : (
		      ocupadosProfesor.map((h, i) => (
		        <p key={i}>{h.dia}: {h.horaDesde} - {h.horaHasta}</p>
		      ))
		    )}
		  </div>

		  <div className="ocupados-box">
		    <h3>Horarios ocupados de la Cancha</h3>
		    {ocupadosCancha.length === 0 ? (
		      <p>No tiene horarios ocupados.</p>
		    ) : (
		      ocupadosCancha.map((h, i) => (
		        <p key={i}>{h.dia}: {h.horaDesde} - {h.horaHasta}</p>
		      ))
		    )}
		  </div>

		</div>
		

        <div className="horario-contenedor">
          <div className="horario-card horario-form">
            <h3>Agregar horario</h3>
			
			{mensaje && <p className="mensaje-error">{mensaje}</p>}
			
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
            <input type="time" name="horaDesde" value={nuevoHorario.horaDesde} onChange={handleChange} />
            <input type="time" name="horaHasta" value={nuevoHorario.horaHasta} onChange={handleChange} />
            <button onClick={handleAgregar}>Agregar horario</button>
          </div>

          {horarios.map((h) => (
            <div key={h.id} className="horario-card horario-item">
              {editandoId === h.id ? (
                <>
                  <select name="dia" value={horarioEditado?.dia || ""} onChange={handleEditarChange}>
                    <option value="Lunes">Lunes</option>
                    <option value="Martes">Martes</option>
                    <option value="Miércoles">Miércoles</option>
                    <option value="Jueves">Jueves</option>
                    <option value="Viernes">Viernes</option>
                    <option value="Sábado">Sábado</option>
                    <option value="Domingo">Domingo</option>
                  </select>
                  <input type="time" name="horaDesde" value={horarioEditado?.horaDesde || ""} onChange={handleEditarChange} />
                  <input type="time" name="horaHasta" value={horarioEditado?.horaHasta || ""} onChange={handleEditarChange} />
                  <button onClick={() => handleGuardarCambios(horarioEditado)}>Guardar</button>
                </>
              ) : (
                <>
                  <p>{h.dia}</p>
                  <p>{h.horaDesde} - {h.horaHasta}</p>
                  <button onClick={() => handleEditarClick(h)}>Modificar</button>
                  <button onClick={() => handleEliminar(h)}>Eliminar</button>
                </>
              )}
            </div>
          ))}
        </div>

        <button onClick={() => navigate("/actividades")} className="btn-volver">← Volver</button>
      </div>
      {mostrarModal && (
        <Modal
          titulo="Confirmar eliminación"
          mensaje="¿Seguro que querés eliminar este horario?"
          textoConfirmar="Eliminar"
          textoCancelar="Cancelar"
          onConfirmar={confirmarEliminar}
          onCancelar={cancelarEliminar}
        />
      )}
    </div>
  );
};

export default AgregarHorario;