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
  const [success, setSuccess] = useState<string | null>(null);

  const usuario = JSON.parse(localStorage.getItem('usuario') || 'null');

  if (!usuario) {
    window.location.href = '/login';
  }

  // --------------------------------------------
  //   CAMBIO DE FECHA -> PEDIR HORARIOS OCUPADOS
  // --------------------------------------------
  const handleFechaChange = async (value: string) => {
    setFecha(value);
    setOcupados([]);
    setError(null);
    setSuccess(null);

    if (!value) return;

    setLoadingHoras(true);

    try {
      const url = `http://localhost:8080/club/alquiler_salon?action=horarios&idSalon=${idSalon}&fecha=${value}`;

      const res = await fetch(url);
      const text = await res.text();

      let data;
      try {
        data = JSON.parse(text);
      } catch (e) {
        setError("Respuesta inválida del servidor.");
        return;
      }

      if (data.status === "sin_horarios") {
        setOcupados([]);
        return;
      }

      // AHORA buscamos la clave "ocupados"
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

  // --------------------------------------------
  //   CHEQUEAR SI UN TURNO ESTÁ OCUPADO
  // --------------------------------------------
  const turnoOcupado = (desde: string, hasta: string) => {
    return ocupados.some((o) => {
      return !(hasta <= o.horaDesde || desde >= o.horaHasta);
    });
  };

  // --------------------------------------------
  //   RESERVAR
  // --------------------------------------------
  const reservar = async (desde: string, hasta: string) => {
    setError(null);
    setSuccess(null);

    try {
      const params = new URLSearchParams();
      params.append('action', 'crear');
      params.append('fecha', fecha);
      params.append('hora_desde', desde);
      params.append('hora_hasta', hasta);
      params.append('id_salon', String(idSalon));
      params.append('id_usuario', usuario.id);

      const res = await fetch('http://localhost:8080/club/alquiler_salon', {	 

        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString(),
      });

      const text = await res.text();

      if (text.includes("ok")) {
        setSuccess("Reserva realizada con éxito 🎉");
        setTimeout(() => navigate('/salones'), 1800);
      } else {
        setError("❌ No se pudo realizar la reserva.");
      }

    } catch (err) {
      setError("❌ Error de conexión con el servidor.");
    }
  };

  return (
    <>
      <NavbarSocio />

      <div className="alquilar-salon-page">
        <div className="alquilar-content">
          <h2>Alquilar Salón</h2>

          {/* FECHA */}
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

          {/* HORARIOS */}
          {fecha && (
            <div className="horarios-box">
              <h3>Horarios disponibles</h3>

              {loadingHoras ? (
                <p>Cargando horarios...</p>
              ) : (
                <ul className="lista-horarios">
                  {TURNOS.map((t) => {
                    const ocupado = turnoOcupado(t.desde, t.hasta);

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
          {success && <p className="success-box">{success}</p>}
        </div>
      </div>

      <Footer />
    </>
  );

};

export default AlquilarSalon;
