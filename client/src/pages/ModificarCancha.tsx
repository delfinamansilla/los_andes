import React, { useState, useEffect } from 'react';
import NavbarAdmin from './NavbarAdmin';
import { useNavigate } from 'react-router-dom';
import '../styles/ModificarCancha.css';

interface Cancha {
  id: number;
  nro_cancha: number;
  ubicacion: string;
  descripcion: string;
  tamanio: number;
  estado: boolean;
}

const ModificarCancha: React.FC = () => {
  const navigate = useNavigate();
  const [cancha, setCancha] = useState<Cancha | null>(null);
  const [mensajeExito, setMensajeExito] = useState('');
  const [mensajeError, setMensajeError] = useState('');

  const [nroCancha, setNroCancha] = useState(0);
  const [ubicacion, setUbicacion] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [tamanio, setTamanio] = useState(0);
  const [estado, setEstado] = useState(true);

  useEffect(() => {
    const stored = localStorage.getItem('canchaSeleccionada');
    if (!stored) {
      navigate('/canchas'); // si no hay cancha seleccionada, volver
      return;
    }
    const c = JSON.parse(stored) as Cancha;
    setCancha(c);
    setNroCancha(c.nro_cancha);
    setUbicacion(c.ubicacion);
    setDescripcion(c.descripcion);
    setTamanio(c.tamanio);
    setEstado(c.estado);
  }, [navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!cancha) return;

    const params = new URLSearchParams();
    params.append('action', 'actualizar');
    params.append('id', cancha.id.toString());
    params.append('nro_cancha', nroCancha.toString());
    params.append('ubicacion', ubicacion);
    params.append('descripcion', descripcion);
    params.append('tamanio', tamanio.toString());
    params.append('estado', estado.toString());

    try {
      const res = await fetch('http://localhost:8080/club/cancha', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString(),
      });

      if (res.ok) {
        setMensajeExito('✅ Cancha actualizada correctamente');
        localStorage.removeItem('canchaSeleccionada');
        setTimeout(() => navigate('/canchas-admin'), 2000);
      } else {
        setMensajeError('❌ Error al actualizar la cancha');
        setTimeout(() => setMensajeError(''), 3000);
      }
    } catch (err) {
      console.error(err);
      setMensajeError('🚫 Error de conexión con el servidor');
      setTimeout(() => setMensajeError(''), 3000);
    }
  };

  if (!cancha) return <p>Cargando cancha...</p>;

  return (
    <div className="pag-modificar">
      <NavbarAdmin />
      
      {/* Este es el contenedor principal que centra todo verticalmente */}
      <div className="contenido-cancha">
        
        {/* Título principal de la página */}
        <h1>Modificar Cancha {cancha.nro_cancha}</h1>

        {/* El recuadro cremita del formulario */}
        <div className="form-modificar">
          {mensajeExito && <p className="mensaje-exito">{mensajeExito}</p>}
          {mensajeError && <p className="mensaje-error">{mensajeError}</p>}
          
          <form onSubmit={handleSubmit} className="modificar">
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
              <button type="submit">Guardar cambios</button>
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

export default ModificarCancha;
