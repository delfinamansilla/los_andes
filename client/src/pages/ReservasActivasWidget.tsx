import React, { useEffect, useState } from 'react';
import { Calendar, Clock, MapPin, Building, ChevronRight, ChevronLeft } from 'lucide-react';
import '../styles/ReservasActivasWidget.css';

interface Cancha {
  id: number;
  nro_cancha: number;
  ubicacion: string;
  descripcion: string;
}

interface Salon {
  id: number;
  nombre: string;
  imagen?: string;
}

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

interface ReservaUnificada {
  id: number;
  tipo: 'cancha' | 'salon';
  fecha: string;
  horaDesde: string;
  horaHasta: string;
  nombre: string;
  ubicacion?: string;
  imagen?: string;
}

const ReservasActivasWidget: React.FC = () => {
  const [reservas, setReservas] = useState<ReservaUnificada[]>([]);
  const [loading, setLoading] = useState(true);
  const [currentIndex, setCurrentIndex] = useState(0);

  const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');

  useEffect(() => {
    if (!usuario.id) {
      setLoading(false);
      return;
    }

    fetchReservas();
  }, []);

  const fetchReservas = async () => {
    try {
      // Fetch alquileres de canchas
      const resCanchas = await fetch(`http://localhost:8080/club/alquiler_cancha?action=mis_reservas&idUsuario=${usuario.id}`);
      const dataCanchas: AlquilerCancha[] = await resCanchas.json();

      // Fetch todas las canchas para obtener detalles
      const resCanchasInfo = await fetch('http://localhost:8080/club/cancha?action=listar');
      const canchasInfo: Cancha[] = await resCanchasInfo.json();

      // Fetch alquileres de salones
      const resSalones = await fetch(`http://localhost:8080/club/alquiler_salon?action=mis_reservas&idUsuario=${usuario.id}`);
      const dataSalones: AlquilerSalon[] = await resSalones.json();

      // Fetch todos los salones para obtener detalles
      const resSalonesInfo = await fetch('http://localhost:8080/club/salon?action=listar');
      const salonesInfo: Salon[] = await resSalonesInfo.json();

      // Unificar reservas de canchas
      const reservasCanchas: ReservaUnificada[] = dataCanchas.map(alq => {
        const cancha = canchasInfo.find(c => c.id === alq.id_cancha);
        return {
          id: alq.id,
          tipo: 'cancha' as const,
          fecha: alq.fecha_alquiler,
          horaDesde: alq.hora_desde.substring(0, 5),
          horaHasta: alq.hora_hasta.substring(0, 5),
          nombre: `Cancha ${cancha?.nro_cancha || alq.id_cancha}`,
          ubicacion: cancha?.ubicacion
        };
      });

      // Unificar reservas de salones
      const reservasSalones: ReservaUnificada[] = dataSalones.map(alq => {
        const salon = salonesInfo.find(s => s.id === alq.idSalon);
        return {
          id: alq.id,
          tipo: 'salon' as const,
          fecha: alq.fecha,
          horaDesde: alq.horaDesde.substring(0, 5),
          horaHasta: alq.horaHasta.substring(0, 5),
          nombre: salon?.nombre || 'Salón',
          imagen: salon?.imagen
        };
      });

      // Combinar y ordenar por fecha más cercana
      const todasReservas = [...reservasCanchas, ...reservasSalones].sort((a, b) => {
        return new Date(a.fecha).getTime() - new Date(b.fecha).getTime();
      });

      setReservas(todasReservas);
    } catch (error) {
      console.error('Error cargando reservas:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatearFecha = (fecha: string) => {
    const date = new Date(fecha);
    return date.toLocaleDateString('es-AR', { 
      weekday: 'short', 
      day: '2-digit', 
      month: 'short' 
    });
  };

  const diasRestantes = (fecha: string) => {
    const hoy = new Date();
    const fechaReserva = new Date(fecha);
    const diff = Math.ceil((fechaReserva.getTime() - hoy.getTime()) / (1000 * 60 * 60 * 24));
    
    if (diff === 0) return 'Hoy';
    if (diff === 1) return 'Mañana';
    if (diff < 7) return `En ${diff} días`;
    return formatearFecha(fecha);
  };

  const siguiente = () => {
    setCurrentIndex((prev) => (prev + 1) % reservas.length);
  };

  const anterior = () => {
    setCurrentIndex((prev) => (prev - 1 + reservas.length) % reservas.length);
  };

  if (loading) {
    return (
      <div className="reservas-activas-widget loading">
        <p>Cargando reservas...</p>
      </div>
    );
  }

  if (reservas.length === 0) {
    return (
      <div className="reservas-activas-widget empty">
        <div className="widget-header">
          <Calendar size={24} />
          <h3>Mis Reservas Activas</h3>
        </div>
        <div className="empty-state">
          <p>No tienes reservas activas.</p>
          <div className="empty-actions">
            <a href="/socio/canchas" className="btn-reservar">
              Reservar Cancha
            </a>
            <a href="/socio/salones" className="btn-reservar">
              Reservar Salón
            </a>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="reservas-activas-widget">
      <div className="widget-header">
        <Calendar size={24} />
        <h3>Mis Reservas Activas</h3>
        <span className="badge-count">{reservas.length}</span>
      </div>

      <div className="carrusel-container">
        {reservas.length > 1 && (
          <button className="carrusel-btn prev" onClick={anterior}>
            <ChevronLeft size={24} />
          </button>
        )}

        <div className="carrusel-content">
          {reservas.slice(currentIndex, currentIndex + 2).map((reserva) => (
            <div key={`${reserva.tipo}-${reserva.id}`} className="reserva-card">
              <div className="reserva-header">
                <div className="tipo-badge">
                  {reserva.tipo === 'cancha' ? (
                    <><MapPin size={16} /> Cancha</>
                  ) : (
                    <><Building size={16} /> Salón</>
                  )}
                </div>
                <div className="dias-restantes">{diasRestantes(reserva.fecha)}</div>
              </div>



              <div className="reserva-body">
                <h4 className="reserva-nombre">{reserva.nombre}</h4>
                
                <div className="reserva-detalles">
                  <div className="detalle-item">
                    <Calendar size={16} />
                    <span>{formatearFecha(reserva.fecha)}</span>
                  </div>
                  
                  <div className="detalle-item">
                    <Clock size={16} />
                    <span>{reserva.horaDesde} - {reserva.horaHasta}</span>
                  </div>

                  {reserva.ubicacion && (
                    <div className="detalle-item">
                      <MapPin size={16} />
                      <span>{reserva.ubicacion}</span>
                    </div>
                  )}
                </div>
              </div>

              
            </div>
          ))}
        </div>

        {reservas.length > 1 && (
          <button className="carrusel-btn next" onClick={siguiente}>
            <ChevronRight size={24} />
          </button>
        )}
      </div>

      {reservas.length > 2 && (
        <div className="carrusel-indicadores">
          {Array.from({ length: Math.ceil(reservas.length / 2) }).map((_, idx) => (
            <button
              key={idx}
              className={`indicador ${idx === Math.floor(currentIndex / 2) ? 'active' : ''}`}
              onClick={() => setCurrentIndex(idx * 2)}
            />
          ))}
        </div>
      )}

    </div>
  );
};

export default ReservasActivasWidget;