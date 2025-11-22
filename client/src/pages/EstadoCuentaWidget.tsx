import React, { useState, useEffect } from 'react';
import { DollarSign, AlertCircle, CheckCircle, Calendar } from 'lucide-react';
import '../styles/EstadoCuentaWidget.css';

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

const EstadoCuentaWidget: React.FC = () => {
  const [cuotasPendientes, setCuotasPendientes] = useState<CuotaProcesada[]>([]);
  const [ultimoPago, setUltimoPago] = useState<CuotaProcesada | null>(null);
  const [proximoVencimiento, setProximoVencimiento] = useState<CuotaProcesada | null>(null);
  const [loading, setLoading] = useState(true);
  const [totalDeuda, setTotalDeuda] = useState(0);

  const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');

  useEffect(() => {
    if (!usuario || !usuario.id) {
      setLoading(false);
      return;
    }

    Promise.all([
      fetch('http://localhost:8080/club/cuota?action=listar').then(res => res.json()),
      fetch('http://localhost:8080/club/montocuota?action=listar').then(res => res.json()),
      fetch(`http://localhost:8080/club/pagocuota?action=listar_por_usuario&id_usuario=${usuario.id}`).then(res => res.json())
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

      // Filtrar pendientes
      const pendientes = datosCombinados.filter(c => !c.estaPagada);
      setCuotasPendientes(pendientes);

      // Calcular deuda total
      const deuda = pendientes.reduce((sum, c) => sum + c.monto, 0);
      setTotalDeuda(deuda);

      // Obtener último pago
      const pagadas = datosCombinados
        .filter(c => c.estaPagada && c.fecha_pago)
        .sort((a, b) => new Date(b.fecha_pago!).getTime() - new Date(a.fecha_pago!).getTime());
      
      if (pagadas.length > 0) {
        setUltimoPago(pagadas[0]);
      }

      // Obtener próximo vencimiento
      const hoy = new Date();
      const proximasPendientes = pendientes
        .filter(c => new Date(c.fecha_vencimiento) >= hoy)
        .sort((a, b) => new Date(a.fecha_vencimiento).getTime() - new Date(b.fecha_vencimiento).getTime());
      
      if (proximasPendientes.length > 0) {
        setProximoVencimiento(proximasPendientes[0]);
      }
    })
    .catch(err => {
      console.error('Error al cargar estado de cuenta:', err);
    })
    .finally(() => {
      setLoading(false);
    });
  }, []);

  const formatearPeriodo = (nro: number) => {
    if (!nro) return "N/A";
    const anio = Math.floor(nro / 100);
    const mes = nro % 100;
    const meses = ["", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"];
    return `${meses[mes]} ${anio}`;
  };

  const formatearFecha = (fecha: string) => {
    const date = new Date(fecha);
    return date.toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  if (loading) {
    return (
      <div className="estado-cuenta-widget loading">
        <p>Cargando estado de cuenta...</p>
      </div>
    );
  }

  return (
    <div className="estado-cuenta-widget">
      <div className="widget-header">
        <DollarSign size={24} />
        <h3>Estado de Cuenta</h3>
      </div>

      <div className="widget-cards">
        {/* Card de cuotas pendientes */}
        <div className={`info-card ${cuotasPendientes.length > 0 ? 'card-warning' : 'card-success'}`}>
          <div className="card-icon">
            {cuotasPendientes.length > 0 ? (
              <AlertCircle size={32} />
            ) : (
              <CheckCircle size={32} />
            )}
          </div>
          <div className="card-info">
            <p className="card-label">Cuotas Pendientes</p>
            <p className="card-value">{cuotasPendientes.length}</p>
            {totalDeuda > 0 && (
              <p className="card-secondary">Total: ${totalDeuda.toFixed(2)}</p>
            )}
          </div>
        </div>

        {/* Card de próximo vencimiento */}
        <div className="info-card card-info-blue">
          <div className="card-icon">
            <Calendar size={32} />
          </div>
          <div className="card-info">
            <p className="card-label">Próximo Vencimiento</p>
            {proximoVencimiento ? (
              <>
                <p className="card-value">{formatearPeriodo(proximoVencimiento.nro_cuota)}</p>
                <p className="card-secondary">{formatearFecha(proximoVencimiento.fecha_vencimiento)}</p>
              </>
            ) : (
              <p className="card-value">Sin pendientes</p>
            )}
          </div>
        </div>

        {/* Card de último pago */}
        <div className="info-card card-success-light">
          <div className="card-icon">
            <CheckCircle size={32} />
          </div>
          <div className="card-info">
            <p className="card-label">Último Pago</p>
            {ultimoPago ? (
              <>
                <p className="card-value">{formatearPeriodo(ultimoPago.nro_cuota)}</p>
                <p className="card-secondary">{formatearFecha(ultimoPago.fecha_pago!)}</p>
              </>
            ) : (
              <p className="card-value">Sin registros</p>
            )}
          </div>
        </div>
      </div>

      <div className="widget-footer">
        <a href="/mis-cuotas" className="btn-ver-todas">
          Ver todas mis cuotas →
        </a>
      </div>
    </div>
  );
};

export default EstadoCuentaWidget;