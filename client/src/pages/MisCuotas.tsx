import React, { useState, useEffect, useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import NavbarSocio from './NavbarSocio';
import Footer from './Footer';
import '../styles/MisCuotas.css';
import { API_URL } from "../config";

interface Cuota {
  id: number;
  nro_cuota: number;
  fecha_vencimiento: string;
}

interface Monto {
  id_cuota: number;
  monto: number;
}

interface Pago {
  id_cuota: number;
  fecha_pago: string;
  nro_transaccion: string;
}

interface CuotaProcesada {
  id_cuota: number;
  nro_cuota: number;
  fecha_vencimiento: string;
  monto: number;
  estaPagada: boolean;
  fecha_pago: string | null;
  nro_transaccion: string | null;
}

const MisCuotas: React.FC = () => {
  const [cuotasProcesadas, setCuotasProcesadas] = useState<CuotaProcesada[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [pagandoCuotaId, setPagandoCuotaId] = useState<number | null>(null);

  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [filtro, setFiltro] = useState("todas");

  const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
  const location = useLocation();
  const navigate = useNavigate();
  
  const cargarDatos = useCallback(() => {
    if (!usuario || !usuario.id) {
      setError('No se pudo identificar al usuario.');
      setLoading(false);
      return;
    }

    setLoading(true);

    Promise.all([
      fetch(`${API_URL}/cuota?action=listar`).then(res => res.json()),
      fetch(`${API_URL}/montocuota?action=listar`).then(res => res.json()),
      fetch(`${API_URL}/pagocuota?action=listar_por_usuario&id_usuario=${usuario.id}`).then(res => res.json())
    ])
    .then(([todasLasCuotas, todosLosMontos, misPagos]: [Cuota[], Monto[], Pago[]]) => {
      const datosCombinados = todasLasCuotas.map(cuota => {
        const montoEncontrado = todosLosMontos.find(m => m.id_cuota === cuota.id);
        const pagoEncontrado = misPagos.find(p => p.id_cuota === cuota.id);

        return {
          id_cuota: cuota.id,
          nro_cuota: cuota.nro_cuota,
          fecha_vencimiento: cuota.fecha_vencimiento,
          monto: montoEncontrado ? montoEncontrado.monto : 0,
          estaPagada: !!pagoEncontrado,
          fecha_pago: pagoEncontrado ? pagoEncontrado.fecha_pago : null,
          nro_transaccion: pagoEncontrado ? pagoEncontrado.nro_transaccion : null
        };
      });

      datosCombinados.sort((a, b) => b.nro_cuota - a.nro_cuota);
      setCuotasProcesadas(datosCombinados);
    })
    .catch(err => {
      console.error(err);
      setError("Error al cargar los datos de las cuotas.");
    })
    .finally(() => {
      setLoading(false);
    });
  }, [usuario.id]);
  
  useEffect(() => {
    cargarDatos();
  }, [cargarDatos]);
  
  // Al volver desde Mercado Pago tras pagar exitosamente
  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const status = queryParams.get('collection_status') || queryParams.get('status');
    const externalRef = queryParams.get('external_reference');
    const paymentId = queryParams.get('payment_id');
    
    if (status === 'approved' && externalRef) {
      const parts = externalRef.split('_');
      const idCuotaRecuperado = parts[1];
      const idUsuarioRecuperado = parts[3];

      const params = new URLSearchParams();
      params.append('action', 'pagar');
      params.append('id_cuota', idCuotaRecuperado);
      params.append('id_usuario', idUsuarioRecuperado);
      params.append('payment_id', paymentId || '');

      fetch(`${API_URL}/pagocuota`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
      })
      .then(res => res.json())
      .then(() => {
        setCuotasProcesadas(prevCuotas => prevCuotas.map(c => {
          if (c.id_cuota.toString() === idCuotaRecuperado) {
            return { 
              ...c, 
              estaPagada: true, 
              fecha_pago: new Date().toLocaleDateString(),
              nro_transaccion: paymentId
            };
          }
          return c;
        }));

        setShowSuccessModal(true);
        cargarDatos();
        navigate(location.pathname, { replace: true });
      })
      .catch(err => {
        console.error("Error al registrar el pago en el backend", err);
      });
    }
  }, [location, navigate, cargarDatos]);

  const formatearPeriodo = (nro: number) => {
    if (!nro) return "N/A";
    const anio = Math.floor(nro / 100);
    const mes = nro % 100;
    const meses = ["", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];
    return `${meses[mes]} ${anio}`;
  };

  // REDIRECCIÓN DIRECTA A MERCADO PAGO
  const handlePagar = (cuota: CuotaProcesada) => {
    if (!usuario.id) {
      alert("Error: No se pudo identificar al usuario.");
      return;
    }

    setPagandoCuotaId(cuota.id_cuota);

    const params = new URLSearchParams();
    params.append('action', 'crear_orden_pago');
    params.append('id_usuario', usuario.id);
    params.append('id_cuota', String(cuota.id_cuota));
    params.append('monto', String(cuota.monto));

    fetch(`${API_URL}/pagocuota`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params
    })
    .then(res => {
      if (!res.ok) throw new Error('El servidor respondió con un error');
      return res.json();
    })
    .then(data => {
      if (data.init_point) {
        // Redirige al checkout de Mercado Pago en la misma pestaña
        window.location.href = data.init_point;
      } else {
        throw new Error("No se recibió la URL de pago.");
      }
    })
    .catch(err => {
      console.error("Error al crear la orden de pago:", err);
      alert("Hubo un error al iniciar el pago. Inténtalo de nuevo.");
      setPagandoCuotaId(null);
    });
  };

  const cuotasFiltradas = cuotasProcesadas.filter(c => {
    if (filtro === "pagas") return c.estaPagada === true;
    if (filtro === "pendientes") return c.estaPagada === false;
    return true;
  });

  return (
    <div className="pagina-cuotas">
      <NavbarSocio />
      <div className="cuotas-contenido">
        <h1>Mis Cuotas</h1>

        {loading && <p className="status-message">Cargando...</p>}
        {error && <p className="status-message error">{error}</p>}

        {!loading && !error && (
          <>
            <div className="filtro-cuotas">
              <label>Filtrar por estado: </label>
              <select value={filtro} onChange={(e) => setFiltro(e.target.value)}>
                <option value="todas">Todas</option>
                <option value="pagas">Pagas</option>
                <option value="pendientes">Pendientes</option>
              </select>
            </div>

            <table className="tabla-cuotas">
              <thead>
                <tr>
                  <th>Período</th>
                  <th>Vencimiento</th>
                  <th>Monto</th>
                  <th>Estado</th>
                  <th>Fecha de Pago</th>
                  <th>Acción</th>
                  <th>Nro. Transacción</th>
                </tr>
              </thead>
              <tbody>
                {cuotasFiltradas.map(cuota => (
                  <tr key={cuota.id_cuota}>
                    <td>{formatearPeriodo(cuota.nro_cuota)}</td>
                    <td>{cuota.fecha_vencimiento}</td>
                    <td>${cuota.monto.toFixed(2)}</td>
                    <td>
                      <span className={`estado-badge ${cuota.estaPagada ? 'estado-pagado' : 'estado-pendiente'}`}>
                        {cuota.estaPagada ? 'Pagado' : 'Pendiente'}
                      </span>
                    </td>
                    <td>{cuota.fecha_pago || '-'}</td>
                    <td>
                      {!cuota.estaPagada && (
                        <button 
                          onClick={() => handlePagar(cuota)} 
                          className="btn-pagar"
                          disabled={pagandoCuotaId === cuota.id_cuota}
                        >
                          {pagandoCuotaId === cuota.id_cuota ? 'Redirigiendo...' : 'Pagar'}
                        </button>
                      )}
                    </td>
                    <td style={{fontSize: '0.8rem', color: '#666'}}>
                      {cuota.estaPagada ? (cuota.nro_transaccion || 'S/N') : '-'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        )}
      </div>

      <Footer />

      {showSuccessModal && (
        <div className="modal-qr-overlay">
          <div className="modal-qr-content" style={{borderTop: '5px solid #4CAF50'}}>
             <div style={{fontSize: '50px', color: '#4CAF50', marginBottom: '10px'}}>
               ✅
             </div>
             <h2 style={{color: '#20321E'}}>¡Pago Exitoso!</h2>
             <p>El pago se registró correctamente en el sistema.</p>
             
             <button 
               onClick={() => setShowSuccessModal(false)}
               className="btn-primary"
               style={{marginTop: '20px'}}
             >
               Aceptar
             </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default MisCuotas;