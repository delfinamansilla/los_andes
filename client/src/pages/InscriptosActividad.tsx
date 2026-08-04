import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import NavbarAdmin from "./NavbarAdmin";
import { API_URL } from "../config";
import "../styles/InscriptosActividad.css";
interface Socio {
  id: number;
  dni: string;
  nombre_completo: string;
  mail: string;
  telefono: string;
}

const InscriptosActividad: React.FC = () => {
  const [inscriptos, setInscriptos] = useState<Socio[]>([]);
  const [loading, setLoading] = useState(true);
  const [actividadNombre, setActividadNombre] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const storedAct = localStorage.getItem("actividad");
    if (!storedAct) {
      navigate("/actividades");
      return;
    }
    const actividad = JSON.parse(storedAct);
    setActividadNombre(actividad.nombre);

    fetch(`${API_URL}/inscripcion?action=listarinscriptos&idActividad=${actividad.id}`)
      .then(res => res.json())
      .then(data => {
        setInscriptos(data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setLoading(false);
      });
  }, [navigate]);

  return (
    <div className="admin-page">
      <NavbarAdmin />
      <div className="content-area">
	  <div className="list-container">
	            <button 
	              onClick={() => navigate("/actividad-detalle")} 
	              className="btn-volver-tabla"
	            >
	              <i className="fa-solid fa-arrow-left"></i> Volver al detalle
	            </button>

	            <h2>Socios Inscriptos: {actividadNombre}</h2>

	            {loading ? (
	              <p style={{ textAlign: 'center' }}>Cargando lista...</p>
	            ) : inscriptos.length > 0 ? (
	              <table className="data-table">
	                <thead>
	                  <tr>
	                    <th>DNI</th>
	                    <th>Nombre Completo</th>
	                    <th>Email</th>
	                    <th>Teléfono</th>
	                  </tr>
	                </thead>
	                <tbody>
	                  {inscriptos.map((socio) => (
	                    <tr key={socio.id}>
	                      <td>{socio.dni}</td>
	                      <td>{socio.nombre_completo}</td>
	                      <td>{socio.mail}</td>
	                      <td>{socio.telefono}</td>
	                    </tr>
	                  ))}
	                </tbody>
	              </table>
	            ) : (
	              <p className="mensaje-vacio">No hay socios inscriptos en esta actividad todavía.</p>
	            )}
	          </div>
      </div>
    </div>
  );
};

export default InscriptosActividad;