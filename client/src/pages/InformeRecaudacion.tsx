import { useEffect, useState } from "react";
import NavbarAdmin from "./NavbarAdmin";
import "../styles/InformeRecaudacion.css";

interface Informe {
  nombreUsuario: string;
  cuota: number;
  monto: number;
  fechaPago: string;
}

export default function InformeRecaudacion() {
	const hoy = new Date().toISOString().split("T")[0];

	  const [fechaDesde, setFechaDesde] = useState(hoy);
	  const [fechaHasta, setFechaHasta] = useState(hoy);
	  const [datos, setDatos] = useState<Informe[]>([]);

	  const descargarPDF = () => {

	    window.open(
	      `http://localhost:8080/club/informe?action=pdf&desde=${fechaDesde}&hasta=${fechaHasta}`,
	      "_blank"
	    );

	  };
	  
	  
  const cargarInforme = () => {
    fetch(
		`http://localhost:8080/club/informe?action=recaudacion&desde=${fechaDesde}&hasta=${fechaHasta}`
    )
      .then((r) => r.json())
      .then((data) => setDatos(data))
      .catch(console.error);
  };

  useEffect(() => {
    cargarInforme();
  }, []);

  const total = datos.reduce((acc, d) => acc + d.monto, 0);

  return (
    <div className="recaudacion-page">

      <NavbarAdmin />

      <div className="contenido-recaudacion">

        <div className="bienvenida-header">
          <h2>Informe de Recaudación</h2>
          <p>Visualización de los pagos realizados por los socios.</p>
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

            <button onClick={cargarInforme}>
              Generar Informe
            </button>
			
			<button onClick={descargarPDF}>
			 Descargar informe PDF
			</button>

          </div>
		  

          <div className="total-card">
            Total Recaudado: $
            {total.toLocaleString("es-AR")}
          </div>

          <table className="tabla-informe">

            <thead>
              <tr>
                <th>Usuario</th>
                <th>Cuota</th>
                <th>Monto</th>
                <th>Fecha</th>
              </tr>
            </thead>

            <tbody>

              {datos.map((d, i) => (

                <tr key={i}>
                  <td>{d.nombreUsuario}</td>
                  <td>{d.cuota}</td>
                  <td>${d.monto.toLocaleString("es-AR")}</td>
                  <td>{d.fechaPago}</td>
                </tr>

              ))}

            </tbody>

          </table>

        </div>

      </div>
    </div>
  );
}