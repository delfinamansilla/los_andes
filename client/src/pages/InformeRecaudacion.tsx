import { useEffect, useState, useMemo } from "react";
import NavbarAdmin from "./NavbarAdmin";
import "../styles/InformeRecaudacion.css";
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, 
  PieChart, Pie, Cell, Legend 
} from 'recharts';

interface Informe {
  nombreUsuario: string;
  cuota: number;
  monto: number;
  fechaPago: string;
}

export default function InformeRecaudacion() {
	const hoy = new Date().toISOString().split("T")[0];

	  const [fechaDesde, setFechaDesde] = useState("2024-01-01");
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
  const datosMes = useMemo(() => {
      const agrupado: Record<string, number> = {};
      datos.forEach(d => {
        const fecha = new Date(d.fechaPago);
        const etiqueta = fecha.toLocaleString('es-AR', { month: 'short', year: 'numeric' });
        agrupado[etiqueta] = (agrupado[etiqueta] || 0) + d.monto;
      });
      return Object.keys(agrupado).map(key => ({ nombre: key, total: agrupado[key] }));
    }, [datos]);

    // 2. Datos para Gráfico de Torta (Distribución por Cuota)
    const datosCuota = useMemo(() => {
      const agrupado: { [key: string]: number } = {};
      datos.forEach(d => {
        const etiqueta = `Cuota ${d.cuota}`;
        agrupado[etiqueta] = (agrupado[etiqueta] || 0) + d.monto;
      });
      return Object.keys(agrupado).map(key => ({ name: key, value: agrupado[key] }));
    }, [datos]);

    const COLORS = ['#20321E', '#466245', '#8C8578', '#A6A292', '#D9D5C7'];


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
		  {datos.length > 0 && (
		              <div className="graficos-container">
		                <div className="grafico-box">
		                  <h3>Recaudación por Mes</h3>
		                  <ResponsiveContainer width="100%" height={300}>
		                    <BarChart data={datosMes}>
		                      <CartesianGrid strokeDasharray="3 3" />
		                      <XAxis dataKey="nombre" />
		                      <YAxis />
		                      <Tooltip formatter={(value) => `$${value}`} />
		                      <Bar dataKey="total" fill="#20321E" radius={[4, 4, 0, 0]} />
		                    </BarChart>
		                  </ResponsiveContainer>
		                </div>

		                <div className="grafico-box">
		                  <h3>Distribución por Cuota</h3>
		                  <ResponsiveContainer width="100%" height={300}>
		                    <PieChart>
		                      <Pie
		                        data={datosCuota}
		                        innerRadius={60}
		                        outerRadius={80}
		                        paddingAngle={5}
		                        dataKey="value"
		                      >
		                        {datosCuota.map((_entry: any, index: number) => (
		                          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
		                        ))}
		                      </Pie>
		                      <Tooltip formatter={(value) => `$${value}`} />
		                      <Legend />
		                    </PieChart>
		                  </ResponsiveContainer>
		                </div>
		              </div>
		            )}

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