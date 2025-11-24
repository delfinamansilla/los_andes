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
  { desde: '08:00', hasta: '12:00' },
  { desde: '12:00', hasta: '16:00' },
  { desde: '16:00', hasta: '20:00' },
  { desde: '20:00', hasta: '23:59' },
];

const AlquilarSalon: React.FC = () => {
  const { idSalon } = useParams();
  const navigate = useNavigate();

  const [fecha, setFecha] = useState('');
  const [ocupados, setOcupados] = useState<Rango[]>([]);
  const [loadingHoras, setLoadingHoras] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [modalMessage, setModalMessage] = useState<string | null>(null);
  const [modalType, setModalType] = useState<'error' | 'success'>('success');
  const [loadingReservaTurno, setLoadingReservaTurno] = useState<string | null>(null);

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
      const url = `http://https://losandesback-production.up.railway.app/alquiler_salon?action=horarios&idSalon=${idSalon}&fecha=${value}`;

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

      if (data.status === "sin_horarios") {
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
      params.append("action", "pre_reserva");
      params.append("fecha", fecha);
      params.append("hora_desde", desde);
      params.append("hora_hasta", hasta);
      params.append("id_salon", String(idSalon));
      params.append("id_usuario", usuario.id);
      params.append("email", usuario.mail); 

      const url = `https://losandesback-production.up.railway.app/alquiler_salon?${params.toString()}`;

      const res = await fetch(url);
      const text = await res.text();

      if (text.includes("mail_enviado")) {
		setModalType('success');
			    setModalMessage('Te enviamos un mail para confirmar la reserva. Revisá tu bandeja!');
			    setTimeout(() => {
			      navigate('/inicio-socio');
			    }, 10000);
      } else {
		setModalType('error');
		setModalMessage('No se pudo iniciar la reserva.');
      }

    } catch (err) {
      setModalMessage('Error de conexión con el servidor.');
    }finally {
	  	   setLoadingReservaTurno(null); 
	  	 }
  };


  return (
    <>
      <NavbarSocio />

      <div className="alquilar-salon-page">
        <div className="alquilar-content">
          <h2>Alquilar Salón</h2>

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
                        className={`horario-item ${ocupado ? 'ocupado' : 'libre'}`}
                      >
                        <span>{t.desde} - {t.hasta}</span>

                        {!ocupado ? (
                          <button className="btn-reservar" onClick={() => reservar(t.desde, t.hasta)}>
                            Reservar
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

          {error && <p className="error-box">{error}</p>}
          
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

export default AlquilarSalon;
