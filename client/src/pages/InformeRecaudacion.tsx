import { useEffect, useState } from "react";
import NavbarAdmin from "./NavbarAdmin";
import "../styles/InformeRecaudacion.css";

interface Informe {
  nombreUsuario: string;
  cuota: number;
  monto: number;
  fechaPago: string;
}
const meses = [
  "Enero",
  "Febrero",
  "Marzo",
  "Abril",
  "Mayo",
  "Junio",
  "Julio",
  "Agosto",
  "Septiembre",
  "Octubre",
  "Noviembre",
  "Diciembre",
];
export default function InformeRecaudacion() {
  const hoy = new Date();

  const [mes, setMes] = useState(hoy.getMonth() + 1);
  const [anio, setAnio] = useState(hoy.getFullYear());

  const [datos, setDatos] = useState<Informe[]>([]);

  const cargarInforme = () => {
    fetch(
      `http://localhost:8080/club/informe?action=recaudacion&mes=${mes}&anio=${anio}`
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
              <label>Mes</label>

              <select
                value={mes}
                onChange={(e) => setMes(Number(e.target.value))}
              >
                {meses.map((nombre, index) => (
                  <option key={index + 1} value={index + 1}>
                    {nombre}
                  </option>
                ))}
              </select>
            </div>

            <div className="campo-filtro">
              <label>Año</label>

              <input
                type="number"
                value={anio}
                onChange={(e) => setAnio(Number(e.target.value))}
              />
            </div>

            <button onClick={cargarInforme}>
              Generar Informe
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