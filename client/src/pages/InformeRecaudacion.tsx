import { useEffect, useState, useMemo } from "react";
import NavbarAdmin from "./NavbarAdmin";
import "../styles/InformeRecaudacion.css";
import Modal from "./Modal";
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
const hoy = new Date().toLocaleDateString('en-CA');
	  const [fechaDesde, setFechaDesde] = useState("");
	  const [fechaHasta, setFechaHasta] = useState("");
	  const [datos, setDatos] = useState<Informe[]>([]);
	  const [modalVisible, setModalVisible] = useState(false);
	    const [modalTitulo, setModalTitulo] = useState("");
	    const [modalMensaje, setModalMensaje] = useState("");

	    const abrirModal = (titulo: string, mensaje: string) => {
	      setModalTitulo(titulo);
	      setModalMensaje(mensaje);
	      setModalVisible(true);
	    };

	    const cerrarModal = () => {
	      setModalVisible(false);
	    };

		const descargarPDF = () => {
		    // 1. Si no hay fechas, mostramos modal y CORTAMOS con return
		    if (!fechaDesde || !fechaHasta) {
		      abrirModal("Atención", "Por favor seleccione un rango de fechas antes de descargar el PDF.");
		      return; // 👈 ¡ESTE return ES CLAVE!
		    }

		    // 2. Si las fechas son mayores a hoy, CORTAMOS con return
		    if (fechaDesde > hoy || fechaHasta > hoy) {
		      abrirModal("Error en fechas", "Las fechas seleccionadas no pueden ser posteriores al día de hoy.");
		      return; // 👈 OTRO return
		    }

		    // 3. Si Desde es mayor a Hasta, CORTAMOS con return
		    if (fechaDesde > fechaHasta) {
		      abrirModal("Error en fechas", "La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
		      return; // 👈 OTRO return
		    }
			// 👉 NUEVA VALIDACIÓN: Si ya consultó y no hay pagos
			    if (datos.length === 0) {
			      abrirModal("Información", "No existen pagos registrados en el período seleccionado para generar el documento.");
			      return;
			    }

		    // 4. Solo si pasó todas las validaciones abre el PDF
		    window.open(
		      `http://localhost:8080/club/informe?action=pdf&desde=${fechaDesde}&hasta=${fechaHasta}`,
		      "_blank"
		    );
		  };
	  
	  
  const cargarInforme = () => {
	if (!fechaDesde || !fechaHasta) {
		abrirModal("Atención", "Por favor seleccione fecha desde y fecha hasta.");
	     return;
	   }
	   if (fechaDesde > hoy || fechaHasta > hoy) {
	         abrirModal("Error en fechas", "Las fechas seleccionadas no pueden ser posteriores al día de hoy.");
	         return;
	       }
	 if (fechaDesde > fechaHasta) {
	     abrirModal("Error en fechas", "La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
	         return;
	       }
    fetch(
		`http://localhost:8080/club/informe?action=recaudacion&desde=${fechaDesde}&hasta=${fechaHasta}`
    )
	.then((r) => r.json())
	      .then((data) => {
	        setDatos(data);
	        if (data.length === 0) {
	          abrirModal("Información", "No se encontraron pagos registrados en el período seleccionado.");
	        }
	      })
	      .catch((err) => {
	        console.error(err);
	        abrirModal("Error", "Ocurrió un error al conectar con el servidor.");
	      });
  };

 /* useEffect(() => {
    cargarInforme();
  }, []);*/
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
			  max={hoy}

			  value={fechaDesde}
			  onChange={(e) => setFechaDesde(e.target.value)}
			/>
            </div>

            <div className="campo-filtro">
			<label>Fecha hasta</label>

			<input
			  type="date"
			  max={hoy}

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
	  {modalVisible && (
	          <Modal
	            titulo={modalTitulo}
	            mensaje={modalMensaje}
	            textoConfirmar="Aceptar"
	            onConfirmar={cerrarModal}
	            onCancelar={cerrarModal}
	          />
	        )}
	      </div>
	    );
	  
}