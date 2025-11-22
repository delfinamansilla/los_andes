import React, { useState, useEffect } from 'react';
import { Calendar, DollarSign, Clock, MapPin, Building } from 'lucide-react';
import '../styles/ActividadRecienteWidget.css';

interface AlquilerCancha {
  id: number;
  fecha_alquiler: string;
  hora_desde: string;
  hora_hasta: string;
  id_cancha: number;
  id_usuario: number;
}

interface AlquilerSalon {
  id: number;
  fecha: string;
  horaDesde: string;
  horaHasta: string;
  idSalon: number;
  idUsuario: number;
}

interface PagoCuota {
  id_usuario: number;
  id_cuota: number;
  fecha_pago: string;
}

interface Usuario {
  id: number;
  nombre_completo: string;
}

interface Cancha {
  id: number;
  nro_cancha: number;
}

interface Salon {
  id: number;
  nombre: string;
}

interface Cuota {
  id: number;
  nro_cuota: number;
}

interface EventoActividad {
  tipo: 'reserva_cancha' | 'reserva_salon' | 'pago_cuota';
  descripcion: string;
  fecha: Date;
  icono: React.ReactNode;
  colorClass: string;
}

const ActividadRecienteWidget: React.FC = () => {
  const [eventos, setEventos] = useState<EventoActividad[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cargarActividades();
  }, []);

  const cargarActividades = async () => {
    try {
      // Cargar todos los datos necesarios
      const [
        resUsuarios,
        resCanchas,
        resSalones,
        resCuotas,
        resAlqCancha,
        resAlqSalon,
        resPagos
      ] = await Promise.all([
        fetch('http://localhost:8080/club/usuario?action=listar'),
        fetch('http://localhost:8080/club/cancha?action=listar'),
        fetch('http://localhost:8080/club/salon?action=listar'),
        fetch('http://localhost:8080/club/cuota?action=listar'),
        fetch('http://localhost:8080/club/alquiler_cancha?action=listar'),
        fetch('http://localhost:8080/club/alquiler_salon?action=listar'),
        fetch('http://localhost:8080/club/pagocuota?action=listar')
      ]);

      const usuarios: Usuario[] = await resUsuarios.json();
      const canchas: Cancha[] = await resCanchas.json();
      const salones: Salon[] = await resSalones.json();
      const cuotas: Cuota[] = await resCuotas.json();
      const alquileresCanchas: AlquilerCancha[] = await resAlqCancha.json();
      const alquileresSalones: AlquilerSalon[] = await resAlqSalon.json();
      const pagos: PagoCuota[] = await resPagos.json();

      const eventosTemp: EventoActividad[] = [];

      // Procesar alquileres de canchas
      alquileresCanchas.forEach(alq => {
        const usuario = usuarios.find(u => u.id === alq.id_usuario);
        const cancha = canchas.find(c => c.id === alq.id_cancha);
        
        if (usuario && cancha) {
          // Combinar fecha y hora para ordenamiento
          const fechaHora = new Date(`${alq.fecha_alquiler}T${alq.hora_desde}`);
          
          eventosTemp.push({
            tipo: 'reserva_cancha',
            descripcion: `${usuario.nombre_completo} reservó Cancha ${cancha.nro_cancha}`,
            fecha: fechaHora,
            icono: <MapPin size={20} />,
            colorClass: 'evento-cancha'
          });
        }
      });

      // Procesar alquileres de salones
      alquileresSalones.forEach(alq => {
        const usuario = usuarios.find(u => u.id === alq.idUsuario);
        const salon = salones.find(s => s.id === alq.idSalon);
        
        if (usuario && salon) {
          const fechaHora = new Date(`${alq.fecha}T${alq.horaDesde}`);
          
          eventosTemp.push({
            tipo: 'reserva_salon',
            descripcion: `${usuario.nombre_completo} reservó ${salon.nombre}`,
            fecha: fechaHora,
            icono: <Building size={20} />,
            colorClass: 'evento-salon'
          });
        }
      });

      // Procesar pagos de cuotas
      pagos.forEach(pago => {
        const usuario = usuarios.find(u => u.id === pago.id_usuario);
        const cuota = cuotas.find(c => c.id === pago.id_cuota);
        
        if (usuario && cuota) {
          const fechaPago = new Date(pago.fecha_pago);
          const periodo = formatearPeriodo(cuota.nro_cuota);
          
          eventosTemp.push({
            tipo: 'pago_cuota',
            descripcion: `${usuario.nombre_completo} pagó cuota de ${periodo}`,
            fecha: fechaPago,
            icono: <DollarSign size={20} />,
            colorClass: 'evento-pago'
          });
        }
      });

      // Ordenar por fecha (más recientes primero) y tomar solo los últimos 5
      const eventosOrdenados = eventosTemp
        .sort((a, b) => b.fecha.getTime() - a.fecha.getTime())
        .slice(0, 5);

      setEventos(eventosOrdenados);
    } catch (error) {
      console.error('Error cargando actividades:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatearPeriodo = (nro: number): string => {
    if (!nro) return 'N/A';
    const anio = Math.floor(nro / 100);
    const mes = nro % 100;
    const meses = ['', 'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 
                   'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
    return `${meses[mes]} ${anio}`;
  };

  const formatearTiempoRelativo = (fecha: Date): string => {
    const ahora = new Date();
    const diffMs = ahora.getTime() - fecha.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHoras = Math.floor(diffMs / 3600000);
    const diffDias = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Hace un momento';
    if (diffMins < 60) return `Hace ${diffMins} min`;
    if (diffHoras < 24) return `Hace ${diffHoras} hora${diffHoras > 1 ? 's' : ''}`;
    if (diffDias < 7) return `Hace ${diffDias} día${diffDias > 1 ? 's' : ''}`;
    
    return fecha.toLocaleDateString('es-AR', { day: '2-digit', month: 'short' });
  };

  if (loading) {
    return (
      <div className="actividad-reciente-widget loading">
        <p>Cargando actividad reciente...</p>
      </div>
    );
  }

  return (
    <div className="actividad-reciente-widget">
      <div className="widget-header-actividad">
        <Clock size={24} />
        <h3>Actividad Reciente</h3>
        <button onClick={cargarActividades} className="btn-refresh-small">
          ↻
        </button>
      </div>

      {eventos.length === 0 ? (
        <div className="empty-actividad">
          <p>No hay actividad reciente registrada</p>
        </div>
      ) : (
        <div className="timeline-actividad">
          {eventos.map((evento, index) => (
            <div key={index} className={`evento-item ${evento.colorClass}`}>
              <div className="evento-icono">
                {evento.icono}
              </div>
              <div className="evento-contenido">
                <p className="evento-descripcion">{evento.descripcion}</p>
                <span className="evento-tiempo">
                  {formatearTiempoRelativo(evento.fecha)}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ActividadRecienteWidget;