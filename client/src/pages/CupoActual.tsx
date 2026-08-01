import React, { useState, useEffect } from 'react';
import NavbarAdmin from './NavbarAdmin';
import { API_URL } from '../config';
import { useNavigate } from 'react-router-dom';
import '../styles/CupoActual.css';

const CupoActual = () => {
  const [cupoMax, setCupoMax] = useState(0);
  const [sociosActivos, setSociosActivos] = useState(0);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([
      fetch(`${API_URL}/configuracion?action=actual`).then(res => res.json()),
      fetch(`${API_URL}/usuario?action=listar`).then(res => res.json())
    ]).then(([config, usuarios]) => {
      setCupoMax(config.cupo);
      const activos = usuarios.filter((u: any) => u.rol === 'socio' && u.estado === true).length;
      setSociosActivos(activos);
      setLoading(false);
    }).catch(err => {
      console.error(err);
      setLoading(false);
    });
  }, []);

  const restantes = cupoMax - sociosActivos;

  return (
    <div className="pag-cupo-actual">
      <NavbarAdmin />
      
      <div className="contenido-cupo">
        <h1>Estado del Club</h1>
        
        <div className="card-cupo">
          {loading ? (
            <p style={{textAlign: 'center'}}>Cargando datos...</p>
          ) : (
            <div className="info-cupo-container">
              <div className="info-item">
                <label>Capacidad Total</label>
                <span>{cupoMax} Socios</span>
              </div>

              <div className="info-item">
                <label>Socios Inscriptos</label>
                <span>{sociosActivos} Socios</span>
              </div>

              <div className="info-item" style={{border: 'none'}}>
                <label>Lugares Disponibles</label>
                <span className={`cupo-restante-valor ${restantes <= 10 ? 'cupo-critico' : 'cupo-positivo'}`}>
                  {restantes}
                </span>
              </div>

              <div className="botones-container">
               
                <button className="btn-config" onClick={() => navigate('/configurar-cupo')}>
                  Cambiar Cupo
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

    </div>
  );
};

export default CupoActual;