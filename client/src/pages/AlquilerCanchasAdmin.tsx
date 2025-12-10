import React, { useState, useEffect } from 'react';
import NavbarAdmin from './NavbarAdmin';
import '../styles/VerAlquileresSalon.css';


interface AlquilerData {
  id: number;
  fecha_alquiler: string;
  hora_desde: string;
  hora_hasta: string;
  id_cancha: number;
  id_usuario: number;
}

interface AlquilerConUsuario {
  alquiler: AlquilerData;
  nombreUsuario: string;
  mailUsuario?: string;
}

interface Cancha {
  id: number;
  nombre: string;
}

const AlquileresCanchasAdmin = () => {
  const [alquileres, setAlquileres] = useState<AlquilerConUsuario[]>([]);
  const [cancha, setCancha] = useState<Cancha | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const canchaStored = localStorage.getItem('canchaVerAlquileres');
    if (!canchaStored) {
      setError('No se seleccionó ninguna cancha.');
      setLoading(false);
      return;
    }

    const canchaParsed: Cancha = JSON.parse(canchaStored);
    setCancha(canchaParsed);
    fetchAlquileres(canchaParsed.id);
  }, []);

  const fetchAlquileres = async (idCancha: number) => {
    try {
      const res = await fetch(`https://losandesback-production.up.railway.app/alquiler_cancha?action=listar_por_cancha&id_cancha=${idCancha}`);
      if (!res.ok) throw new Error('Error al obtener alquileres.');
      const data = await res.json();
      setAlquileres(data);
    } catch (err) {
      if (err instanceof Error) setError(err.message);
      else setError(String(err));
    } finally {
      setLoading(false);
    }
  };

  const handleVolver = () => {
    localStorage.removeItem('canchaVerAlquileres');
    window.location.href = '/canchas-admin';
  };

  const formatHora = (hora?: string) => {
    if (!hora) return ""; 
    return hora.substring(0, 5);
  };

  return (
    <div className="ver-alquileres-page">
      <NavbarAdmin />

      <div className="admin-container">

        {cancha && <h2>Alquileres: {cancha.nombre}</h2>}

        {error && <div className="error-msg">{error}</div>}
        {loading && <p className="loading-msg">Cargando alquileres...</p>}

        {!loading && !error && (
          <div className="alquileres-card-container">

            {alquileres.length === 0 ? (
              <p className="empty-msg">No hay alquileres registrados para esta cancha.</p>
            ) : (
              <table className="tabla-alquileres">
                <thead>
                  <tr>
                    <th>Fecha</th>
                    <th>Horario</th>
                    <th>Cliente</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {alquileres.map((item) => {
                    const { alquiler, nombreUsuario } = item;

                    const fechaAlquiler = new Date(alquiler.fecha_alquiler + 'T' + alquiler.hora_hasta);
                    const hoy = new Date();
                    const esPasado = fechaAlquiler < hoy;

                    return (
                      <tr key={alquiler.id}>
                        <td>
                          <i className="fa-regular fa-calendar" style={{ marginRight: '8px' }}></i>
                          {alquiler.fecha_alquiler}
                        </td>
                        <td>
                          <i className="fa-regular fa-clock" style={{ marginRight: '8px' }}></i>
                          {formatHora(alquiler.hora_desde)} - {formatHora(alquiler.hora_hasta)}
                        </td>
                        <td className="usuario-info">
                          <strong>{nombreUsuario}</strong>
                          <span className="usuario-mail">{item.mailUsuario}</span>
                        </td>
                        <td>
                          {esPasado ? (
                            <span className="badge badge-finalizado">Finalizado</span>
                          ) : (
                            <span className="badge badge-pendiente">Pendiente</span>
                          )}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            )}
          </div>
        )}

        <div className="btn-volver-container">
          <button onClick={handleVolver} className="btn-volver">
            <i className="fa-solid fa-arrow-left"></i> Volver a Canchas
          </button>
        </div>

      </div>
    </div>
  );
};

export default AlquileresCanchasAdmin;
