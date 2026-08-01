import React, { useState } from 'react';
import NavbarAdmin from './NavbarAdmin';
import { API_URL } from '../config';
import { useNavigate } from 'react-router-dom';
import '../styles/ConfigurarCupo.css';

const ConfigurarCupo = () => {
  const [nuevoCupo, setNuevoCupo] = useState('');
  const navigate = useNavigate();

  const [mensajeExito, setMensajeExito] = useState('');
  const [mensajeError, setMensajeError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensajeExito('');
    setMensajeError('');

    try {
      const res = await fetch(`${API_URL}/configuracion?action=update_cupo&valor=${nuevoCupo}`, { 
        method: 'POST' 
      });

      const data = await res.json();

      if (res.ok) {
        // Caso éxito (Status 200)
        setMensajeExito('Cupo actualizado correctamente');
        setTimeout(() => navigate('/cupo-actual'), 2000);
      } else {
        // Caso error de negocio (Status 400 - El que viene de LogicConfiguracion)
        setMensajeError(data.error || 'Error al actualizar el cupo');
        setTimeout(() => setMensajeError(''), 5000);
      }
    } catch (err) {
      // Caso error de red
      console.error(err);
      setMensajeError('Error de conexión con el servidor');
      setTimeout(() => setMensajeError(''), 3000);
    }
  };

  return (
    <div className="pag-configurar-cupo">
      <NavbarAdmin />
      
      <div className="contenido-configurar">
        <div className="form-configurar">
          <h1>Configurar Límite de Socios</h1>

          {/* Bloque de mensajes igual a CrearSalon */}
          <div className="mensajes-globales">
            {mensajeExito && <p className="mensaje-exito">{mensajeExito}</p>}
            {mensajeError && <p className="mensaje-error">{mensajeError}</p>}
          </div>
          
          <form onSubmit={handleSubmit} className="configurar-group">
            <label htmlFor="cupo">Nuevo Cupo Máximo:</label>
            <input 
              id="cupo"
              type="number" 
              placeholder="Ej: 150"
              value={nuevoCupo}
              onChange={(e) => setNuevoCupo(e.target.value)}
              required
            />

            <div className="botones-container">
              <button type="submit">Actualizar Cupo</button>
              <button 
                type="button" 
                className="btn-cancel" 
                onClick={() => navigate('/inicio-admin')}
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ConfigurarCupo;