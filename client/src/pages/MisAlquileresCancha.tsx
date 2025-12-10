import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarSocio from './NavbarSocio';
import '../styles/MisAlquileresCancha.css';
import Footer from './Footer';


interface Cancha {
  id: number;
  nro_cancha: number;
  ubicacion: string;
  descripcion: string;
}

interface Alquiler {
  id: number;
  fecha_alquiler: string;
  hora_desde: string;
  hora_hasta: string;
  id_cancha: number;
  id_usuario: number;
}

interface ReservaConDetalle extends Alquiler {
  datosCancha?: Cancha;
}

const MisAlquileresCancha: React.FC = () => {
  const [reservas, setReservas] = useState<ReservaConDetalle[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const [showModalEliminar, setShowModalEliminar] = useState(false);
  const [reservaAEliminar, setReservaAEliminar] = useState<ReservaConDetalle | null>(null);

  const navigate = useNavigate();

  const fetchData = async () => {
    setLoading(true);
    try {
      const usuarioStorage = localStorage.getItem('usuario');
      if (!usuarioStorage) {
        navigate('/login');
        return;
      }
      const usuario = JSON.parse(usuarioStorage);

      const resReservas = await fetch(`https://losandesback-production.up.railway.app/alquiler_cancha?action=mis_reservas&idUsuario=${usuario.id}`);
      if (!resReservas.ok) throw new Error('Error al obtener reservas');
      const dataReservas: Alquiler[] = await resReservas.json();

      const resCanchas = await fetch('https://losandesback-production.up.railway.app/cancha?action=listar');
      if (!resCanchas.ok) throw new Error('Error al obtener canchas');
      const dataCanchas: Cancha[] = await resCanchas.json();

      const reservasCombinadas = dataReservas.map((reserva) => {
        const canchaEncontrada = dataCanchas.find(c => c.id === reserva.id_cancha);
        return {
          ...reserva,
          datosCancha: canchaEncontrada
        };
      });

      setReservas(reservasCombinadas);
    } catch (err: any) {
      console.error(err);
      setError(err.message || 'Error de conexión');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [navigate]);

  const handleEliminar = (reserva: ReservaConDetalle) => {
    setReservaAEliminar(reserva);
    setShowModalEliminar(true);
  };

  const confirmarEliminar = async () => {
    if (!reservaAEliminar) return;
    try {
      const res = await fetch(`https://losandesback-production.up.railway.app/alquiler_cancha?action=eliminar&id=${reservaAEliminar.id}`);
      if (!res.ok) throw new Error('No se pudo eliminar');

      setShowModalEliminar(false);
      setReservaAEliminar(null);
      setReservas(prev => prev.filter(r => r.id !== reservaAEliminar.id));
    } catch (err: any) {
      alert(err.message);
      setShowModalEliminar(false);
    }
  };

  const formatHora = (hora: string) => hora.substring(0, 5);

  return (
    <div className="mis-reservas-page">
      <NavbarSocio />

      <div className="reservas-container">
        <h2>Mis Reservas de Canchas</h2>

        {loading && <p className="loading-text">Cargando...</p>}
        {error && <p className="error-msg">{error}</p>}

        {!loading && !error && reservas.length === 0 && (
          <div className="empty-state">
            <p>No tienes reservas activas.</p>
            <button
              className="btn-reservar-nuevo"
              onClick={() => navigate('/canchas')}
            >
              Reservar Nueva
            </button>
          </div>
        )}

        <div className="lista-reservas">
          {reservas.map((item) => (
            <div key={item.id} className="reserva-card">
              <div className="reserva-content">
                <div className="card-header">
                  <h3>Cancha {item.datosCancha?.nro_cancha || item.id_cancha}</h3>
                </div>

                <div className="card-body">
                  <div className="dato-item">
                    <i className="fa-regular fa-calendar"></i>
                    <span>{item.fecha_alquiler}</span>
                  </div>
                  <div className="dato-item">
                    <i className="fa-regular fa-clock"></i>
                    <span>{formatHora(item.hora_desde)} - {formatHora(item.hora_hasta)} hs</span>
                  </div>
                </div>

                <div className="card-footer">
                  <button
                    className="btn-cancelar-reserva"
                    onClick={() => handleEliminar(item)}
                  >
                    <i className="fa-solid fa-trash"></i> Cancelar Reserva
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {showModalEliminar && (
        <div className="modal-backdrop">
          <div className="modal">
            <h3>¿Cancelar reserva?</h3>
            <p>Esta acción es permanente.</p>
            <div className="modal-buttons">
              <button onClick={confirmarEliminar} className="btn-confirm">Sí, Eliminar</button>
              <button onClick={() => setShowModalEliminar(false)} className="btn-cancel">Volver</button>
            </div>
          </div>
        </div>
      )}
      <Footer />
    </div>
  );
};

export default MisAlquileresCancha;
