import React, { useState, useEffect } from 'react';
import { Users, Calendar, DollarSign, GraduationCap, TrendingUp, TrendingDown } from 'lucide-react';
import '../styles/EstadisticasAdminWidget.css';

interface Usuario {
  id: number;
  nombre_completo: string;
  rol: string;
  estado: boolean;
}

interface AlquilerCancha {
  id: number;
  fecha_alquiler: string;
  hora_desde: string;
  hora_hasta: string;
}

interface AlquilerSalon {
  id: number;
  fecha: string;
  horaDesde: string;
  horaHasta: string;
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
  id_cuota: number;
  fecha_pago: string;
}

interface Profesor {
  idProfesor: number;
  nombreCompleto: string;
}

interface Estadistica {
  titulo: string;
  valor: number;
  icono: React.ReactNode;
  tendencia: number;
  enlace: string;
  colorClass: string;
  montoExtra?: number;
}

const EstadisticasAdminWidget: React.FC = () => {
  const [sociosActivos, setSociosActivos] = useState(0);
  const [reservasHoy, setReservasHoy] = useState(0);
  const [cuotasPendientes, setCuotasPendientes] = useState(0);
  const [montoPendiente, setMontoPendiente] = useState(0);
  const [profesoresActivos, setProfesoresActivos] = useState(0);
  const [loading, setLoading] = useState(true);

  const [tendenciaSocios] = useState(5.2);
  const [tendenciaReservas] = useState(-2.1);
  const [tendenciaCuotas] = useState(8.3);
  const [tendenciaProfesores] = useState(0);

  useEffect(() => {
    cargarEstadisticas();
  }, []);

  const cargarEstadisticas = async () => {
    try {
      const resUsuarios = await fetch('https://losandesback-production.up.railway.app/usuario?action=listar');
      const usuarios: Usuario[] = await resUsuarios.json();
      const socios = usuarios.filter(u => u.rol.toLowerCase() === 'socio' && u.estado);
      setSociosActivos(socios.length);

      const hoy = new Date().toISOString().split('T')[0];
      
      const resCanchas = await fetch('https://losandesback-production.up.railway.app/alquiler_cancha?action=listar');
      const alquileresCanchas: AlquilerCancha[] = await resCanchas.json();
      const canchasHoy = alquileresCanchas.filter(a => a.fecha_alquiler === hoy).length;

      const resSalones = await fetch('https://losandesback-production.up.railway.app/alquiler_salon?action=listar');
      const alquileresSalones: AlquilerSalon[] = await resSalones.json();
      const salonesHoy = alquileresSalones.filter(a => a.fecha === hoy).length;

      setReservasHoy(canchasHoy + salonesHoy);

      const resCuotas = await fetch('https://losandesback-production.up.railway.app/cuota?action=listar');
      const cuotas: Cuota[] = await resCuotas.json();

      const resMontos = await fetch('https://losandesback-production.up.railway.app/montocuota?action=listar');
      const montos: MontoCuota[] = await resMontos.json();

      const resPagos = await fetch('https://losandesback-production.up.railway.app/pagocuota?action=listar');
      const pagos: PagoCuota[] = await resPagos.json();

      const idsPagadas = new Set(pagos.map(p => p.id_cuota));
      const cuotasPendientes = cuotas.filter(c => !idsPagadas.has(c.id));

      let totalPendiente = 0;
      cuotasPendientes.forEach(cuota => {
        const monto = montos.find(m => m.id_cuota === cuota.id);
        if (monto) {
          totalPendiente += monto.monto;
        }
      });

      setCuotasPendientes(cuotasPendientes.length);
      setMontoPendiente(totalPendiente);

      const resProfesores = await fetch('https://losandesback-production.up.railway.app/profesor?action=listar');
      const profesores: Profesor[] = await resProfesores.json();
      setProfesoresActivos(profesores.length);

    } catch (error) {
      console.error('Error cargando estadísticas:', error);
    } finally {
      setLoading(false);
    }
  };

  const estadisticas: Estadistica[] = [
    {
      titulo: 'Socios Activos',
      valor: sociosActivos,
      icono: <Users size={32} />,
      tendencia: tendenciaSocios,
      enlace: '/admin/socios',
      colorClass: 'stat-socios'
    },
    {
      titulo: 'Reservas de Hoy',
      valor: reservasHoy,
      icono: <Calendar size={32} />,
      tendencia: tendenciaReservas,
      enlace: '/admin/canchas',
      colorClass: 'stat-reservas'
    },
    {
      titulo: 'Cuotas Pendientes',
      valor: cuotasPendientes,
      icono: <DollarSign size={32} />,
      tendencia: tendenciaCuotas,
      enlace: '/admin/cuotas',
      colorClass: 'stat-cuotas',
      montoExtra: montoPendiente
    },
    {
      titulo: 'Profesores Activos',
      valor: profesoresActivos,
      icono: <GraduationCap size={32} />,
      tendencia: tendenciaProfesores,
      enlace: '/admin/profesores',
      colorClass: 'stat-profesores'
    }
  ];

  if (loading) {
    return (
      <div className="estadisticas-widget loading">
        <p>Cargando estadísticas...</p>
      </div>
    );
  }

  return (
    <div className="estadisticas-widget">
      <div className="widget-header-stats">
        <h3>Estadísticas en Tiempo Real</h3>
        <button onClick={cargarEstadisticas} className="btn-refresh">
          Actualizar
        </button>
      </div>

      <div className="grid-estadisticas-real">
        {estadisticas.map((stat, index) => (
          <div 
            key={index} 
            className={`stat-card-real ${stat.colorClass}`}
          >
            <div className="stat-header-real">
              <div className="stat-icon-real">
                {stat.icono}
              </div>
              <div className="stat-tendencia">
                {stat.tendencia > 0 ? (
                  <>
                    <TrendingUp size={16} className="trend-up" />
                    <span className="trend-up">+{stat.tendencia}%</span>
                  </>
                ) : stat.tendencia < 0 ? (
                  <>
                    <TrendingDown size={16} className="trend-down" />
                    <span className="trend-down">{stat.tendencia}%</span>
                  </>
                ) : (
                  <span className="trend-neutral">—</span>
                )}
              </div>
            </div>

            <div className="stat-body-real">
              <h4 className="stat-titulo">{stat.titulo}</h4>
              <div className="stat-valor-container">
                <span className="stat-valor-real">
                  {stat.valor}
                </span>
                {stat.montoExtra !== undefined && (
                  <div className="monto-extra">
                    <span className="monto-label">Total:</span>
                    <span className="monto-valor">${stat.montoExtra.toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default EstadisticasAdminWidget;