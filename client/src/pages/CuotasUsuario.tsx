import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';
import Modal from './Modal'; 
import '../styles/CuotasUsuario.css';

interface Usuario {
  id: number;
  nombre_completo: string;
  dni: string;
}

interface Cuota {
  id: number;
  nro_cuota: number;
  fecha_vencimiento: string;
}

interface MontoCuota {
  id_cuota: number;
  monto: number;
}

interface PagoCuota {
  id: number;
  id_cuota: number;
  id_usuario: number;
  fecha_pago: string;
}

const CuotasUsuario: React.FC = () => {
  const navigate = useNavigate();
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  
  const [cuotas, setCuotas] = useState<Cuota[]>([]);
  const [montos, setMontos] = useState<MontoCuota[]>([]);
  const [pagos, setPagos] = useState<PagoCuota[]>([]);
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [modalConfirmacion, setModalConfirmacion] = useState<{ visible: boolean, idCuota: number | null }>({
    visible: false,
    idCuota: null
  });

  const [modalInfo, setModalInfo] = useState<{ visible: boolean, titulo: string, mensaje: string }>({
    visible: false,
    titulo: '',
    mensaje: ''
  });

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    const userJson = localStorage.getItem('usuarioSeleccionadoParaCuotas');
    
    if (!userJson) {
      setError('No se ha seleccionado un usuario. Vuelva al listado.');
      setLoading(false);
      return;
    }

    const userObj = JSON.parse(userJson);
    setUsuario(userObj);

    try {
      const resCuotas = await fetch('https://losandesback-production.up.railway.app/cuota?action=listar');
      if (!resCuotas.ok) throw new Error('Error al cargar cuotas');
      
      const resMontos = await fetch('https://losandesback-production.up.railway.app/montocuota?action=listar');
      if (!resMontos.ok) throw new Error('Error al cargar montos');

      const resPagos = await fetch(`https://losandesback-production.up.railway.app/pagocuota?action=listar_por_usuario&id_usuario=${userObj.id}`);
      if (!resPagos.ok) throw new Error('Error al cargar pagos');

      const dataCuotas = await resCuotas.json();
      const dataMontos = await resMontos.json();
      const dataPagos = await resPagos.json();

      setCuotas(dataCuotas);
      setMontos(dataMontos);
      setPagos(dataPagos);
    
    } catch (err: any) {
      console.error(err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleBotonPagar = (idCuota: number) => {
    setModalConfirmacion({ visible: true, idCuota: idCuota });
  };

  const confirmarPago = async () => {
    if (!usuario || modalConfirmacion.idCuota === null) return;

    const idCuota = modalConfirmacion.idCuota;
    setModalConfirmacion({ visible: false, idCuota: null });

    try {
      const params = new URLSearchParams();
      params.append('action', 'pagar');
      params.append('id_usuario', usuario.id.toString());
      params.append('id_cuota', idCuota.toString());

      const response = await fetch('https://losandesback-production.up.railway.app/pagocuota', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString(),
      });

      if (response.ok) {
        setModalInfo({
            visible: true,
            titulo: '¡Pago Exitoso!',
            mensaje: 'El pago se ha registrado correctamente en el sistema.'
        });

        const resPagos = await fetch(`https://losandesback-production.up.railway.app/pagocuota?action=listar_por_usuario&id_usuario=${usuario.id}`);
        const dataPagos = await resPagos.json();
        setPagos(dataPagos);
      } else {
        const errData = await response.json();
        setModalInfo({
            visible: true,
            titulo: 'Error ❌',
            mensaje: 'No se pudo registrar el pago: ' + (errData.error || "Error desconocido")
        });
      }

    } catch (error) {
      console.error(error);
      setModalInfo({
          visible: true,
          titulo: 'Error de Conexión ❌',
          mensaje: 'Ocurrió un problema al intentar conectar con el servidor.'
      });
    }
  };

  const getEstadoPago = (idCuota: number) => {
    const pago = pagos.find(p => p.id_cuota === idCuota);
    return pago ? { pagado: true, fecha: pago.fecha_pago } : { pagado: false, fecha: null };
  };

  const getMontoCuota = (idCuota: number) => {
    const montoObj = montos.find(m => m.id_cuota === idCuota);
    return montoObj ? montoObj.monto : 0;
  };

  const cerrarModalConfirmacion = () => {
    setModalConfirmacion({ visible: false, idCuota: null });
  };

  const cerrarModalInfo = () => {
    setModalInfo({ visible: false, titulo: '', mensaje: '' });
  };

  if (loading) return <div className="cuotas-page-container"><p style={{color:'white', marginTop:50}}>Cargando datos...</p></div>;
  
  if (error) {
      return (
        <div className="cuotas-page-container">
            <NavbarAdmin />
            <div className="error-msg">
                <p>{error}</p>
                <button onClick={() => navigate('/listado-socios')} className="btn-back">Volver</button>
            </div>
        </div>
      );
  }
  
  const formatearPeriodo = (nro: number) => {
      if (!nro) return "N/A";
      const anio = Math.floor(nro / 100);
      const mes = nro % 100;
      const meses = ["", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];
      return `${meses[mes]} ${anio}`;
    };

  return (
    <div className="cuotas-page-container">
      <NavbarAdmin />
      
      <div className="header-bar">
        <h2>Estado de Cuenta</h2>
        {usuario && (
           <div className="user-info">
              <span> {usuario.nombre_completo}</span>
              <span> DNI: {usuario.dni}</span>
           </div>
        )}
      </div>

      <div className="table-container">
        {cuotas.length === 0 ? (
            <p style={{color: 'white', textAlign: 'center'}}>No existen cuotas generadas en el sistema.</p>
        ) : (
            <table className="styled-table">
                <thead>
                    <tr>
                        <th>Concepto</th>
                        <th>Vencimiento</th>
                        <th>Monto ($)</th>
                        <th>Estado</th>
                        <th>Fecha de Pago</th>
                        <th>Acción</th>
                    </tr>
                </thead>

                <tbody>
                    {cuotas.map((c) => {
                        const estado = getEstadoPago(c.id);
                        const montoBase = getMontoCuota(c.id);
                        
                        const fechaVenc = new Date(c.fecha_vencimiento);
                        fechaVenc.setHours(0, 0, 0, 0); 
                        
                        const hoy = new Date();
                        hoy.setHours(0, 0, 0, 0);

                        let fechaDePago = null;
                        if (estado.pagado && estado.fecha) {
                            fechaDePago = new Date(estado.fecha);
                            fechaDePago.setHours(0, 0, 0, 0);
                        }

                        let montoFinal = montoBase;
                        let aplicaInteres = false;
                        let textoInteres = "";

                        if (estado.pagado) {
                            if (fechaDePago && fechaDePago > fechaVenc) {
                                montoFinal = montoBase * 1.10;
                                aplicaInteres = true;
                                textoInteres = "Pagado con recargo (10%)";
                            }
                        } else {
                            if (hoy > fechaVenc) {
                                montoFinal = montoBase * 1.10;
                                aplicaInteres = true;
                                textoInteres = "Vencida (Interés 10%)";
                            }
                        }

                        return (
                            <tr key={c.id}>
                                <td className="text-bold">
                                    Cuota {formatearPeriodo(c.nro_cuota)}
                                    {aplicaInteres && (
                                        <div style={{
                                            fontSize: '0.75rem', 
                                            color: estado.pagado ? '#555' : '#ff6b6b', 
                                            marginTop: '4px',
                                            fontWeight: 'bold'
                                        }}>
                                            ⚠ {textoInteres}
                                        </div>
                                    )}
                                </td>
                                
                                <td>{new Date(c.fecha_vencimiento).toLocaleDateString()}</td>
                                
                                <td className="text-monto">
                                  $ {montoFinal.toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                                  {aplicaInteres && (
                                      <div style={{
                                          textDecoration: 'line-through', 
                                          color: '#888', 
                                          fontSize: '0.8rem'
                                      }}>
                                          Orig: ${montoBase.toLocaleString('es-AR')}
                                      </div>
                                  )}
                                </td>
                                
                                <td>
                                    {estado.pagado ? (
                                        <span className="status-badge status-pagado">✅ Pagado</span>
                                    ) : (
                                        aplicaInteres ? (
                                             <span className="status-badge status-vencida">⚠ Vencida</span>
                                        ) : (
                                             <span className="status-badge status-pendiente">⏳ Pendiente</span>
                                        )
                                    )}
                                </td>
                                
                                <td>
                                    {estado.pagado && estado.fecha
                                        ? new Date(estado.fecha).toLocaleDateString() 
                                        : '-'}
                                </td>
                                
                                <td>
                                  {!estado.pagado ? (
                                    <button 
                                      className="btn-pay"
                                      onClick={() => handleBotonPagar(c.id)}
                                    >
                                       Realizó Pago
                                    </button>
                                  ) : (
                                    <span style={{color: '#888', fontSize: '0.9rem'}}>Completado</span>
                                  )}
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        )}
      </div>

      <button onClick={() => navigate('/listado-socio')} className="btn-back">
          ⬅ Volver al Listado
      </button>

      {modalConfirmacion.visible && (
        <Modal
          titulo="Confirmar Pago"
          mensaje="¿Está seguro de que el socio ha realizado el pago de esta cuota? Esta acción registrará la fecha de hoy."
          textoConfirmar="Sí, registrar pago"
          textoCancelar="Cancelar"
          onConfirmar={confirmarPago}
          onCancelar={cerrarModalConfirmacion}
        />
      )}

      {modalInfo.visible && (
        <Modal
          titulo={modalInfo.titulo}
          mensaje={modalInfo.mensaje}
          textoConfirmar="Aceptar"
          textoCancelar="" 
          onConfirmar={cerrarModalInfo}
          onCancelar={cerrarModalInfo}
        />
      )}

    </div>
  );
};

export default CuotasUsuario;