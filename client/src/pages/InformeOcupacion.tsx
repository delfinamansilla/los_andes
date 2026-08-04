import { useEffect, useState, useMemo } from "react";
import NavbarAdmin from "./NavbarAdmin";
import Footer from "./Footer";
import "../styles/InformeOcupacion.css";
import { API_URL } from "../config";
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, 
  PieChart, Pie, Cell, Legend 
} from 'recharts';

interface InformeOcupacionData {
  tipoRecurso: string;
  nombreRecurso: string;
  nombreUsuario: string;
  fecha: string;
  horario: string;
}

export default function InformeOcupacion() {
  const hoy = new Date().toISOString().split("T")[0];

  const [fechaDesde, setFechaDesde] = useState("2025-01-01");
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
  const datosPorRecurso = useMemo(() => {
      const agrupado: Record<string, number> = {};
      datos.forEach(d => {
        agrupado[d.nombreRecurso] = (agrupado[d.nombreRecurso] || 0) + 1;
      });
      return Object.keys(agrupado).map(key => ({
        nombre: key,
        cantidad: agrupado[key]
      })).sort((a, b) => b.cantidad - a.cantidad);
    }, [datos]);


    const datosCanchaVsSalon = useMemo(() => {
      let canchas = 0;
      let salones = 0;
      datos.forEach(d => {
        if (d.tipoRecurso.toLowerCase().includes("cancha")) canchas++;
        else salones++;
      });
      return [
        { name: "Canchas", value: canchas },
        { name: "Salones", value: salones }
      ];
    }, [datos]);

    const COLORS = ['#20321E', '#8C8578'];

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

		  <div style={{ display: "flex", gap: "10px" }}>
		    <button onClick={cargarInforme}>
		      Generar Informe
		    </button>

		    <button
		      onClick={() => {
		        window.open(
		          `${API_URL}/informe?action=pdfOcupacion&desde=${fechaDesde}&hasta=${fechaHasta}`,
		          "_blank"
		        );
		      }}
		    >
		      Descargar informe PDF
		    </button>
		  </div>
          </div>

          <div className="total-card">
            Total de Alquileres: {datos.length}
          </div>
		  {datos.length > 0 && (
		              <div className="graficos-container">
		                <div className="grafico-box">
		                  <h3>Uso por Recurso</h3>
		                  <ResponsiveContainer width="100%" height={300}>
		                    <BarChart data={datosPorRecurso} layout="vertical" margin={{ left: 20, right: 20 }}>
		                      <CartesianGrid strokeDasharray="3 3" />
		                      <XAxis type="number" />
		                      <YAxis dataKey="nombre" type="category" width={100} />
		                      <Tooltip />
		                      <Bar dataKey="cantidad" fill="#20321E" radius={[0, 4, 4, 0]} />
		                    </BarChart>
		                  </ResponsiveContainer>
		                </div>

		                <div className="grafico-box">
		                  <h3>Canchas vs Salones</h3>
		                  <ResponsiveContainer width="100%" height={300}>
		                    <PieChart>
		                      <Pie
		                        data={datosCanchaVsSalon}
		                        innerRadius={60}
		                        outerRadius={80}
		                        paddingAngle={5}
		                        dataKey="value"
		                      >
		                        {datosCanchaVsSalon.map((_entry, index) => (
		                          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
		                        ))}
		                      </Pie>
		                      <Tooltip />
		                      <Legend />
		                    </PieChart>
		                  </ResponsiveContainer>
		                </div>
		              </div>
		            )}

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