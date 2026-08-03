import { useEffect, useState } from "react";
import NavbarAdmin from "./NavbarAdmin";
import Footer from "./Footer";
import "../styles/InformeOcupacion.css";
import { API_URL } from "../config";

interface InformeOcupacionData {
  tipoRecurso: string;
  nombreRecurso: string;
  nombreUsuario: string;
  fecha: string;
  horario: string;
}

export default function InformeOcupacion() {
  const hoy = new Date().toISOString().split("T")[0];

  const [fechaDesde, setFechaDesde] = useState(hoy);
  const [fechaHasta, setFechaHasta] = useState(hoy);
  const [datos, setDatos] = useState<InformeOcupacionData[]>([]);
  const [loading, setLoading] = useState(false);

  const cargarInforme = () => {
    setLoading(true);
	fetch(
	  `${API_URL}/informe?action=ocupacion&desde=${fechaDesde}&hasta=${fechaHasta}`
	)      .then((r) => r.json())
      .then((data) => {
        setDatos(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
  };

  useEffect(() => {
    cargarInforme();
  }, []);

  return (
    <div className="ocupacion-page">
      <NavbarAdmin />

      <div className="contenido-ocupacion">
        <div className="bienvenida-header">
          <h2>Informe de Ocupación</h2>
          <p>Seguimiento de alquileres de canchas y salones del club.</p>
        </div>

        <div className="seccion-card">
          <div className="filtros">
		  <div className="campo-filtro">
		    <label>Fecha desde</label>

		    <input
		      type="date"
		      value={fechaDesde}
		      onChange={(e) => setFechaDesde(e.target.value)}
		    />
		  </div>

		  <div className="campo-filtro">
		    <label>Fecha hasta</label>

		    <input
		      type="date"
		      value={fechaHasta}
		      onChange={(e) => setFechaHasta(e.target.value)}
		    />
		  </div>

            <button onClick={cargarInforme}>Generar Informe</button>
          </div>

          <div className="total-card">
            Total de Alquileres: {datos.length}
          </div>

          <table className="tabla-informe">
            <thead>
              <tr>
                <th>Tipo</th>
                <th>Recurso</th>
                <th>Usuario</th>
                <th>Fecha</th>
                <th>Horario</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan={5}>Cargando...</td></tr>
              ) : (
                datos.map((d, i) => (
                  <tr key={i}>
                    <td>{d.tipoRecurso}</td>
                    <td>{d.nombreRecurso}</td>
                    <td>{d.nombreUsuario}</td>
                    <td>{d.fecha}</td>
                    <td>{d.horario}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
          
          {!loading && datos.length === 0 && (
            <p style={{textAlign:'center', marginTop:'20px', color:'#4a5c48'}}>
                No hay registros para este período.
            </p>
          )}
        </div>
      </div>
      <Footer />
    </div>
  );
}