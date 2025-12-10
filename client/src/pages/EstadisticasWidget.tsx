import React, { useState, useEffect } from 'react';
import { TrendingUp, Calendar, CheckCircle } from 'lucide-react';
import '../styles/EstadisticasWidget.css';

const EstadisticasWidget: React.FC = () => {
  const [stats, setStats] = useState({
    actividadesInscritas: 0,
    reservasFuturas: 0,
    cuotasPagadas: 0
  });
  const [loading, setLoading] = useState(true);

  const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');

  useEffect(() => {
    if (!usuario.id) {
      setLoading(false);
      return;
    }

    fetchEstadisticas();
  }, []);

  const fetchEstadisticas = async () => {
    try {
      const resActividades = await fetch(`https://losandesback-production.up.railway.app/inscripcion?action=porusuario&id_usuario=${usuario.id}`);
      const actividades = await resActividades.json();

      const resCanchas = await fetch(`https://losandesback-production.up.railway.app/alquiler_cancha?action=mis_reservas&idUsuario=${usuario.id}`);
      const canchas = await resCanchas.json();

      const resSalones = await fetch(`https://losandesback-production.up.railway.app/alquiler_salon?action=mis_reservas&idUsuario=${usuario.id}`);
      const salones = await resSalones.json();

      const totalReservas = canchas.length + salones.length;

      const resPagos = await fetch(`https://losandesback-production.up.railway.app/pagocuota?action=listar_por_usuario&id_usuario=${usuario.id}`);
      const pagos = await resPagos.json();

      setStats({
        actividadesInscritas: actividades.length,
        reservasFuturas: totalReservas,
        cuotasPagadas: pagos.length
      });
    } catch (error) {
      console.error('Error cargando estadísticas:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="estadisticas-widget loading">
        <p>Cargando...</p>
      </div>
    );
  }

  return (
    <div className="estadisticas-widget">
      <div className="stats-grid">
        <div className="stat-item">
          <div className="stat-icon actividades">
            <TrendingUp size={20} />
          </div>
          <div className="stat-info">
            <p className="stat-value">{stats.actividadesInscritas}</p>
            <p className="stat-label">Actividades</p>
          </div>
        </div>

        <div className="stat-item">
          <div className="stat-icon reservas">
            <Calendar size={20} />
          </div>
          <div className="stat-info">
            <p className="stat-value">{stats.reservasFuturas}</p>
            <p className="stat-label">Reservas activas</p>
          </div>
        </div>

        <div className="stat-item">
          <div className="stat-icon cuotas">
            <CheckCircle size={20} />
          </div>
          <div className="stat-info">
            <p className="stat-value">{stats.cuotasPagadas}</p>
            <p className="stat-label">Cuotas pagadas</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EstadisticasWidget;