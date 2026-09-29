import React, { useState, useEffect, useMemo } from 'react';
import NavbarAdmin from './NavbarAdmin';
import '../styles/VerAlquileresSalon.css';
import { API_URL } from "../config";

const PAGE_SIZE = 3;
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
  const [fechaDesde, setFechaDesde] = useState('');
  const [fechaHasta, setFechaHasta] = useState('');
  const [fechaDesdeAplicada, setFechaDesdeAplicada] = useState('');
  const [fechaHastaAplicada, setFechaHastaAplicada] = useState('');
  const [visibleCount, setVisibleCount] = useState(PAGE_SIZE);

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
      const res = await fetch(`${API_URL}/alquiler_cancha?action=listar_por_cancha&id_cancha=${idCancha}`);
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
  
  const alquileresFiltrados = useMemo(() => {
    let lista = [...alquileres];

    if (fechaDesdeAplicada) {
      lista = lista.filter((item) => item.alquiler.fecha_alquiler >= fechaDesdeAplicada);
    }

    if (fechaHastaAplicada) {
      lista = lista.filter((item) => item.alquiler.fecha_alquiler <= fechaHastaAplicada);
    }

    lista.sort((a, b) => {
      const fechaHoraA = `${a.alquiler.fecha_alquiler}T${a.alquiler.hora_desde}`;
      const fechaHoraB = `${b.alquiler.fecha_alquiler}T${b.alquiler.hora_desde}`;
      return fechaHoraB.localeCompare(fechaHoraA);
    });

    return lista;
  }, [alquileres, fechaDesdeAplicada, fechaHastaAplicada]);
   
    const alquileresVisibles = alquileresFiltrados.slice(0, visibleCount);
    const hayMas = visibleCount < alquileresFiltrados.length;
   
	const handleFiltrar = () => {
	  setFechaDesdeAplicada(fechaDesde);
	  setFechaHastaAplicada(fechaHasta);
	  setVisibleCount(PAGE_SIZE);
	};
   
	const handleLimpiarFiltro = () => {
	  setFechaDesde('');
	  setFechaHasta('');
	  setFechaDesdeAplicada('');
	  setFechaHastaAplicada('');
	  setVisibleCount(PAGE_SIZE);
	};
   
    const handleVerMas = () => {
      setVisibleCount((prev) => prev + PAGE_SIZE);
    };

  return (
    <div className="ver-alquileres-page">
      <NavbarAdmin />

      <div className="admin-container">

        {cancha && <h2>Alquileres: {cancha.nombre}</h2>}

        {error && <div className="error-msg">{error}</div>}
        {loading && <p className="loading-msg">Cargando alquileres...</p>}

        {!loading && !error && (
		<>
			<div className="filtro-fechas-container">
              <div className="filtro-campo">
                <label htmlFor="fecha-desde">Desde</label>
                <input
                  id="fecha-desde"
                  type="date"
                  value={fechaDesde}
                  onChange={(e) => setFechaDesde(e.target.value)}
                />
              </div>
 
              <div className="filtro-campo">
                <label htmlFor="fecha-hasta">Hasta</label>
                <input
                  id="fecha-hasta"
                  type="date"
                  value={fechaHasta}
                  onChange={(e) => setFechaHasta(e.target.value)}
                />
              </div>
 
              <div className="filtro-botones">
                <button className="btn-filtrar" onClick={handleFiltrar}>
                  <i className="fa-solid fa-filter"></i> Filtrar
                </button>
                {(fechaDesde || fechaHasta) && (
                  <button className="btn-limpiar-filtro" onClick={handleLimpiarFiltro}>
                    <i className="fa-solid fa-xmark"></i> Limpiar
                  </button>
                )}
              </div>
            </div>
		
          <div className="alquileres-card-container">

            {alquileresFiltrados.length === 0 ? (
              <p className="empty-msg">No hay alquileres registrados para esta cancha en ese rango de fechas.</p>
            ) : (
			<>
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
                  {alquileresVisibles.map((item) => {
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
			  
			  {hayMas && (
                      <div className="ver-mas-container">
                        <button className="btn-ver-mas" onClick={handleVerMas}>
                          Ver más
                        </button>
                      </div>
                    )}
                  </>
                )}
			</div>
		  </>
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
