import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarSocio from './NavbarSocio';
import '../styles/MisAlquileresSalon.css';
import Footer from './Footer';
interface Salon {
  id: number;
  nombre: string;
  imagen?: string;
}

interface Alquiler {
  id: number;
  fecha: string;
  horaDesde: string;
  horaHasta: string;
  idSalon: number;
  idUsuario: number;
}

interface ReservaConDetalle extends Alquiler {
  datosSalon?: Salon;
}

const MisAlquileresSalon: React.FC = () => {
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
      
      const resReservas = await fetch(`https://losandesback-production.up.railway.app/alquiler_salon?action=mis_reservas&idUsuario=${usuario.id}`);
      if (!resReservas.ok) throw new Error('Error al obtener reservas');
      const dataReservas: Alquiler[] = await resReservas.json();

      const resSalones = await fetch('https://losandesback-production.up.railway.app/salon?action=listar');
      if (!resSalones.ok) throw new Error('Error al obtener salones');
      const dataSalones: Salon[] = await resSalones.json();

      const reservasCombinadas = dataReservas.map((reserva) => {
          const salonEncontrado = dataSalones.find(s => s.id === reserva.idSalon);
          return {
              ...reserva,
              datosSalon: salonEncontrado
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
      const res = await fetch(`https://losandesback-production.up.railway.app/alquiler_salon?action=eliminar&id=${reservaAEliminar.id}`);
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
          <h2>Mis Reservas de Salones</h2>

          {loading && <p className="loading-text">Cargando...</p>}
          {error && <p className="error-msg">{error}</p>}

          {!loading && !error && reservas.length === 0 && (
            <div className="empty-state">
              <p>No tienes reservas activas.</p>
              <button 
                className="btn-reservar-nuevo"
                onClick={() => navigate('/salones')}
              >
                Reservar Nuevo
              </button>
            </div>
          )}

          <div className="lista-reservas">
            {reservas.map((item) => (
              <div key={item.id} className="reserva-card">
                
                <div className="reserva-img">
                  {item.datosSalon?.imagen ? (
                    <img src={item.datosSalon.imagen} alt={item.datosSalon.nombre} />
                  ) : (
                    <div className="img-placeholder">
                      <i className="fa-solid fa-building"></i>
                    </div>
                  )}
                </div>

                <div className="reserva-content">
                  
                  <div className="card-header">
                    <h3>{item.datosSalon?.nombre || 'Salón'}</h3>
                    <span className="estado-badge">Confirmado</span>
                  </div>

                  <div className="card-body">
                    <div className="dato-item">
                      <i className="fa-regular fa-calendar"></i>
                      <span>{item.fecha}</span>
                    </div>
                    <div className="dato-item">
                      <i className="fa-regular fa-clock"></i>
                      <span>{formatHora(item.horaDesde)} - {formatHora(item.horaHasta)} hs</span>
                    </div>
                  </div>

                  <div className="card-footer">
                    <button 
                      className="btn-cancelar-reserva" 
                      onClick={() => handleEliminar(item)}
                    >
                      <i className="fa-solid fa-trash"></i>
                      Cancelar Reserva
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
                <button onClick={confirmarEliminar} className="btn-confirm">
                  Sí, Eliminar
                </button>
                <button onClick={() => setShowModalEliminar(false)} className="btn-cancel">
                  Volver
                </button>
              </div>
            </div>
          </div>
        )}
        
        <Footer />
      </div>
    );
};

export default MisAlquileresSalon;