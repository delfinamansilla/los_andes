import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import NavbarSocio from './NavbarSocio';
import Footer from './Footer';
import '../styles/AlquilarSalon.css'; 

interface Rango {
  horaDesde: string;
  horaHasta: string;
}

const TURNOS = [
  { desde: '08:00', hasta: '09:30' },
  { desde: '09:30', hasta: '11:00' },
  { desde: '11:00', hasta: '12:30' },
  { desde: '12:30', hasta: '14:00' },
  { desde: '14:00', hasta: '15:30' },
  { desde: '15:30', hasta: '17:00' },
  { desde: '17:00', hasta: '18:30' },
  { desde: '18:30', hasta: '20:00' },
  { desde: '20:00', hasta: '21:30' },
  { desde: '21:30', hasta: '23:00' },
];


const AlquilarCancha: React.FC = () => {
  const { idCancha } = useParams();
  const navigate = useNavigate();

  const [fecha, setFecha] = useState('');
  const [ocupados, setOcupados] = useState<Rango[]>([]);
  const [loadingHoras, setLoadingHoras] = useState(false);
  const [loadingReservaTurno, setLoadingReservaTurno] = useState<string | null>(null);
  const [error, setError] = useState('');

  
  const [modalMessage, setModalMessage] = useState<string | null>(null);
  const [modalType, setModalType] = useState<'error' | 'success'>('success');


  const usuario = JSON.parse(localStorage.getItem('usuario') || 'null');

  if (!usuario) {
    window.location.href = '/login';
  }


  const handleFechaChange = async (value: string) => {
    setFecha(value);
    setOcupados([]);
    setModalMessage(null);

    if (!value) return;

    setLoadingHoras(true);

    try {
      const url = `http://localhost:8080/club/alquiler_cancha?action=horarios&idCancha=${idCancha}&fecha=${value}`;

      const res = await fetch(url);
      const text = await res.text();

      let data;
      try {
        data = JSON.parse(text);
      } catch (e) {
		setModalType('error');
		setModalMessage('Respuesta inválida del servidor.');
        return;
      }

      if (data.status === 'sin_horarios') {
        setOcupados([]);
        return;
      }

      if (Array.isArray(data.ocupados)) {
        setOcupados(data.ocupados);
      } else {
        setOcupados([]);
      }
    } catch (err) {
      setError('No se pudo cargar la disponibilidad.');
    } finally {
      setLoadingHoras(false);
    }
  };

 
  const turnoOcupado = (desde: string, hasta: string) => {
    return ocupados.some((o) => {
      return !(hasta <= o.horaDesde || desde >= o.horaHasta);
    });
  };


  const reservar = async (desde: string, hasta: string) => {
    setModalMessage(null);
	setLoadingReservaTurno(desde);

    try {
      const params = new URLSearchParams();
      params.append('action', 'pre_reserva');
      params.append('fecha', fecha);
      params.append('hora_desde', desde);
      params.append('hora_hasta', hasta);
      params.append('id_cancha', String(idCancha));
      params.append('id_usuario', usuario.id);
      params.append('email', usuario.mail);

      const url = `http://localhost:8080/club/alquiler_cancha?${params.toString()}`;

      const res = await fetch(url);
      const text = await res.text();

	  if (text.includes('mail_enviado')) {
	    setModalType('success');
	    setModalMessage('Te enviamos un mail para confirmar la reserva. Revisá tu bandeja!');
	    setTimeout(() => {
	      navigate('/inicio-socio');
	    }, 10000);
	  } else {
	    setModalType('error');
	    setModalMessage('❌ No se pudo iniciar la reserva.');
	  }
    } catch (err) {
      setModalMessage('❌ Error de conexión con el servidor.');
    }finally {
	   setLoadingReservaTurno(null); 
	 }
  };

  return (
    <>
      <NavbarSocio />

      <div className="alquilar-salon-page">
        <div className="alquilar-content">
          <h2>Alquilar Cancha</h2>

         
          <div className="form-box">
            <label>Seleccioná una fecha:</label>
            <input
              type="date"
              value={fecha}
              min={new Date().toISOString().split('T')[0]}
              onChange={(e) => handleFechaChange(e.target.value)}
              className="inp"
            />
          </div>

          
          {fecha && (
            <div className="horarios-box">
              <h3>Horarios disponibles</h3>

              {loadingHoras ? (
                <p>Cargando horarios...</p>
              ) : (
                <ul className="lista-horarios">
                  {TURNOS.map((t) => {
                    const ocupado = turnoOcupado(t.desde, t.hasta);
					const isLoading = loadingReservaTurno === t.desde;

                    return (
                      <li
                        key={t.desde}
                        className={`horario-item ${
                          ocupado ? 'ocupado' : 'libre'
                        }`}
                      >
                        <span>
                          {t.desde} - {t.hasta}
                        </span>

                        {!ocupado ? (
							<button
							  className="btn-reservar"
							  onClick={() => reservar(t.desde, t.hasta)}
							  disabled={isLoading} // solo este botón
							>
							  {isLoading ? 'Reservando...' : 'Reservar'}
							</button>
                        ) : (
                          <span className="ocupado-text">Ocupado</span>
                        )}
                      </li>
                    );
                  })}
                </ul>
              )}
            </div>
          )}

        </div>
      </div>

      <Footer />
	    {modalMessage && (
	      <div className="modal-backdrop">
	        <div className="modal">
	          <h3>{modalType === 'error' ? 'Error' : 'Éxito'}</h3>
	          <p>{modalMessage}</p>
	          <div className="modal-buttons">
	            <button
	              className={modalType === 'error' ? 'btn-cancel' : 'btn-confirm'}
	              onClick={() => setModalMessage(null)}
	            >
	              Cerrar
	            </button>
	          </div>
	        </div>
	      </div>
	    )}
	  </>
  );
};

export default AlquilarCancha;
