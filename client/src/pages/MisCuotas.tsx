import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import NavbarSocio from './NavbarSocio';
import Footer from './Footer';
import '../styles/MisCuotas.css';
import { QRCodeSVG } from 'qrcode.react';

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
}

interface CuotaProcesada {
  id_cuota: number;
  nro_cuota: number;
  fecha_vencimiento: string;
  monto: number;
  estaPagada: boolean;
  fecha_pago: string | null;
}

const MisCuotas: React.FC = () => {
  const [cuotasProcesadas, setCuotasProcesadas] = useState<CuotaProcesada[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [qrData, setQrData] = useState<string | null>(null);
  const [paymentId, setPaymentId] = useState<string | null>(null);
  const [isPagarLoading, setIsPagarLoading] = useState(false);

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
      fetch('https://losandesback-production.up.railway.app/cuota?action=listar').then(res => res.json()),
      fetch('https://losandesback-production.up.railway.app/montocuota?action=listar').then(res => res.json()),
      fetch(`https://losandesback-production.up.railway.app/pagocuota?action=listar_por_usuario&id_usuario=${usuario.id}`).then(res => res.json())
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
          fecha_pago: pagoEncontrado ? pagoEncontrado.fecha_pago : null
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
  
	useEffect(() => {
	    const queryParams = new URLSearchParams(location.search);
	    const status = queryParams.get('collection_status');
	    
	    if (status === 'approved') {
	      setShowSuccessModal(true);
	      
	      navigate(location.pathname, { replace: true });
	      
	      cargarDatos();
	    }
	  }, [location, navigate, cargarDatos]);

  const formatearPeriodo = (nro: number) => {
    if (!nro) return "N/A";
    const anio = Math.floor(nro / 100);
    const mes = nro % 100;
    const meses = ["", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];
    return `${meses[mes]} ${anio}`;
  };

  const handlePagar = (cuota: CuotaProcesada) => {
    if (!usuario.id) {
      alert("Error: No se pudo identificar al usuario.");
      return;
    }

    setIsPagarLoading(true);
    setQrData(null);

    const params = new URLSearchParams();
    params.append('action', 'crear_orden_pago');
    params.append('id_usuario', usuario.id);
    params.append('id_cuota', String(cuota.id_cuota));
    params.append('monto', String(cuota.monto));

    fetch('https://losandesback-production.up.railway.app/pagocuota', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params
    })
    .then(res => {
      if (!res.ok) throw new Error('El servidor respondió con un error');
      return res.json();
    })
    .then(data => {
      setQrData(data.qr_data);
      setPaymentId(data.payment_id);
    })
    .catch(err => {
      console.error("Error al crear la orden de pago:", err);
      alert("Hubo un error al generar el código QR. Inténtalo de nuevo.");
    })
    .finally(() => {
      setIsPagarLoading(false);
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
                          disabled={isPagarLoading}
                        >
                          {isPagarLoading ? '...' : 'Pagar'}
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        )}
      </div>

      <Footer />

      {(qrData || isPagarLoading) && (
        <div className="modal-qr-overlay">
          <div className="modal-qr-content">
            {isPagarLoading ? (
              <p>Generando código QR...</p>
            ) : (
              <>
                <h3>Escaneá con la app de Mercado Pago</h3>
                <QRCodeSVG value={qrData!} size={256} />
                <p className="payment-id">ID de Pago: {paymentId}</p>
                <button onClick={() => setQrData(null)} className="btn-cerrar-modal">
                  Cerrar
                </button>
              </>
            )}
          </div>
        </div>
      )}
	  
	  {showSuccessModal && (
          <div className="modal-qr-overlay">
            <div className="modal-qr-content" style={{borderTop: '5px solid #4CAF50'}}>
               <div style={{fontSize: '50px', color: '#4CAF50', marginBottom: '10px'}}>
                 ✅
               </div>
               <h2 style={{color: '#20321E'}}>¡Pago Exitoso!</h2>
               <p>Hemos registrado tu pago correctamente.</p>
               <p>Recibirás el comprobante en tu correo electrónico.</p>
               
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
