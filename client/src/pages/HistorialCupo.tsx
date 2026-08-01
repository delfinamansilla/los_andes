import React, { useState, useEffect } from 'react';
import NavbarAdmin from './NavbarAdmin';
import { API_URL } from '../config';
import '../styles/HistorialCupo.css';

const HistorialCupo = () => {
  const [historial, setHistorial] = useState([]);
  const [loading, setLoading] = useState(true);

  const cargarHistorial = () => {
    fetch(`${API_URL}/configuracion`)
      .then(res => res.json())
      .then(data => {
        setHistorial(data);
        setLoading(false);
      })
      .catch(err => console.error(err));
  };

  useEffect(() => {
    cargarHistorial();
  }, []);

  const formatearFecha = (fechaStr: string) => {
    if (!fechaStr) return "N/A";
    const fecha = new Date(fechaStr);
    
    const dia = fecha.getDate().toString().padStart(2, '0');
    const mes = (fecha.getMonth() + 1).toString().padStart(2, '0');
    const anio = fecha.getFullYear();
    const horas = fecha.getHours().toString().padStart(2, '0');
    const minutos = fecha.getMinutes().toString().padStart(2, '0');

    return `${dia}/${mes}/${anio} - ${horas}:${minutos}hs`;
  };

  if (loading) return <div className="loading-container">Cargando historial...</div>;

  return (
    <div className="admin-page">
      <NavbarAdmin />
      
      <div className="content-area">
        <div className="list-container">
          <h2>Historial de Cupos</h2>
          
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Valor del Cupo</th>
                <th>Fecha de Aplicación</th>
              </tr>
            </thead>
            <tbody>
              {historial.map((h: any) => (
                <tr key={h.id}>
                  <td>#{h.id}</td>
                  <td><strong>{h.valor} Socios</strong></td>
                  <td>{formatearFecha(h.fechaCambio)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          
          {historial.length === 0 && (
            <p className="mensaje-vacio">No hay registros en el historial.</p>
          )}
        </div>
      </div>

    </div>
  );
};

export default HistorialCupo;