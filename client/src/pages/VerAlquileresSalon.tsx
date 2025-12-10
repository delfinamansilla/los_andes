import React, { useState, useEffect } from 'react';
import NavbarAdmin from './NavbarAdmin';
import '../styles/VerAlquileresSalon.css'; 

interface AlquilerData {
  id: number;
  fecha: string;
  horaDesde: string;
  horaHasta: string;
  idSalon: number;
  idUsuario: number;
}

interface AlquilerConUsuario {
  alquiler: AlquilerData;
  nombreUsuario: string;
  mailUsuario?: string;
}

interface Salon {
  id: number;
  nombre: string;
}

const VerAlquileresSalon = () => {
  const [alquileres, setAlquileres] = useState<AlquilerConUsuario[]>([]);
  const [salon, setSalon] = useState<Salon | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const salonStored = localStorage.getItem('salonVerAlquileres');
    if (!salonStored) {
      setError('No se seleccionó ningún salón.');
      setLoading(false);
      return;
    }
    const salonParsed: Salon = JSON.parse(salonStored);
    setSalon(salonParsed);
    fetchAlquileres(salonParsed.id);
  }, []);

  const fetchAlquileres = async (idSalon: number) => {
    try {
      const res = await fetch(`https://losandesback-production.up.railway.app/alquiler_salon?action=listar_por_salon&id_salon=${idSalon}`);
      if (!res.ok) throw new Error('Error al obtener alquileres');
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
    localStorage.removeItem('salonVerAlquileres');
    window.location.href = '/salones-admin';
  };

  const formatHora = (hora: string) => hora.substring(0, 5);

  return (
    <div className="ver-alquileres-page">
      <NavbarAdmin />
      
      <div className="admin-container">

        {salon && <h2>Alquileres: {salon.nombre}</h2>}

        {error && <div className="error-msg">{error}</div>}
        {loading && <p className="loading-msg">Cargando alquileres...</p>}

        {!loading && !error && (
          <div className="alquileres-card-container">
            
            {alquileres.length === 0 ? (
              <p className="empty-msg">No hay alquileres registrados para este salón.</p>
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
                    
                    const fechaAlquiler = new Date(alquiler.fecha + 'T' + alquiler.horaHasta);
                    const hoy = new Date();
                    const esPasado = fechaAlquiler < hoy;

                    return (
                      <tr key={alquiler.id}>
                        <td>
                          <i className="fa-regular fa-calendar" style={{ marginRight: '8px' }}></i>
                          {alquiler.fecha}
                        </td>
                        <td>
                          <i className="fa-regular fa-clock" style={{ marginRight: '8px' }}></i>
                          {formatHora(alquiler.horaDesde)} - {formatHora(alquiler.horaHasta)}
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
		            <i className="fa-solid fa-arrow-left"></i> Volver a Salones
		          </button>
		        </div>
      </div>
    </div>
  );
};

export default VerAlquileresSalon;