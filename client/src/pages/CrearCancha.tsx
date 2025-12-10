import React, { useState } from 'react';
import NavbarAdmin from './NavbarAdmin';
import { useNavigate } from 'react-router-dom';
import '../styles/CrearCancha.css';

const CrearCancha: React.FC = () => {
  const navigate = useNavigate();
  const [nroCancha, setNroCancha] = useState(0);
  const [ubicacion, setUbicacion] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [tamanio, setTamanio] = useState(0);
  const [estado, setEstado] = useState(true);
  const [mensajeExito, setMensajeExito] = useState('');
  const [mensajeError, setMensajeError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const params = new URLSearchParams();
    params.append('action', 'registrar');
    params.append('nro_cancha', nroCancha.toString());
    params.append('ubicacion', ubicacion);
    params.append('descripcion', descripcion);
    params.append('tamanio', tamanio.toString());
    params.append('estado', estado.toString());

    try {
      const res = await fetch('https://losandesback-production.up.railway.app/cancha', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString(),
      });

      if (res.ok) {
        setMensajeExito('Cancha creada correctamente');
        setTimeout(() => navigate('/canchas-admin'), 2000);
      } else {
        setMensajeError('Error al crear la cancha');
        setTimeout(() => setMensajeError(''), 3000);
      }
    } catch (err) {
      console.error(err);
      setMensajeError('Error de conexión con el servidor');
      setTimeout(() => setMensajeError(''), 3000);
    }
  };

  return (
    <div className="pag-crear">
      <NavbarAdmin />

      <div className="contenido-crear">
        <div className="form-crear">
          <h1>Crear Nueva Cancha</h1>
          {mensajeExito && <p className="mensaje-exito">{mensajeExito}</p>}
          {mensajeError && <p className="mensaje-error">{mensajeError}</p>}
          <form onSubmit={handleSubmit} className="crear">
            <label>Número de Cancha:</label>
            <input type="number" value={nroCancha} onChange={(e) => setNroCancha(Number(e.target.value))} />

            <label>Ubicación:</label>
            <input value={ubicacion} onChange={(e) => setUbicacion(e.target.value)} />

            <label>Descripción:</label>
            <input value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />

            <label>Tamaño (m²):</label>
            <input type="number" value={tamanio} onChange={(e) => setTamanio(Number(e.target.value))} />

            <label>Estado:</label>
            <select value={estado ? 'true' : 'false'} onChange={(e) => setEstado(e.target.value === 'true')}>
              <option value="true">Disponible</option>
              <option value="false">No disponible</option>
            </select>

            <div className="botones-container">
              <button type="submit">Crear Cancha</button>
              <button type="button" onClick={() => navigate('/canchas-admin')} className="btn-cancel">
                Cancelar
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CrearCancha;
