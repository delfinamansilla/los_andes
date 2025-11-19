import React from 'react';
import '../styles/Footer.css';
import { useNavigate } from 'react-router-dom';

const Footer: React.FC = () => {
  const navigate = useNavigate();
  
  return (
    <footer className="footer-club">
      <div className="footer-contenido">
        {/* Columna 1: Navegación */}
        <div className="footer-columna">
          <h3>Navegación</h3>
          <ul>
            <li><button onClick={() => navigate('/inicio-socio')}>Inicio</button></li>
            <li><button onClick={() => navigate('/inscripcion-actividad')}>Actividades</button></li>
            <li><button onClick={() => navigate('/canchas')}>Canchas</button></li>
            <li><button onClick={() => navigate('/info/nosotros')}>Sobre Nosotros</button></li>
          </ul>
        </div>

        {/* Columna 2: Información */}
        <div className="footer-columna">
          <h3>Información</h3>
          <ul>
            <li><button onClick={() => navigate('/info/reglamento')}>Reglamento</button></li>
            <li><button onClick={() => navigate('/info/beneficios')}>Beneficios</button></li>
            <li><button onClick={() => navigate('/info/preguntas-frecuentes')}>Preguntas Frecuentes</button></li>
            <li><button onClick={() => navigate('/info/terminos')}>Términos y Condiciones</button></li>
            <li><button onClick={() => navigate('/info/politica')}>Política de Privacidad</button></li>
          </ul>
        </div>

        {/* Columna 3: Atención al Socio */}
        <div className="footer-columna">
          <h3>Atención al Socio</h3>
          <div className="footer-info">
            <div className="info-item">
              <span className="icono">📞</span>
              <span>(0341) 424-5678</span>
            </div>
            <div className="info-item">
              <span className="icono">📱</span>
              <span>+54 9 341 555-1234</span>
            </div>
            <div className="info-item">
              <span className="icono">✉️</span>
              <span>info@clublosandes.com.ar</span>
            </div>
          </div>
        </div>

        {/* Columna 4: Ubicación y Redes */}
        <div className="footer-columna">
          <h3>Visitanos</h3>
          <div className="footer-info">
            <div className="info-item">
              <span className="icono">📍</span>
              <span>Av. Pellegrini 1250<br />Rosario, Santa Fe (S2000)</span>
            </div>
            
          </div>
        </div>
      </div>

      {/* Barra inferior */}
      <div className="footer-bottom">
        <p>© 2024 Club Deportivo Los Andes - Todos los derechos reservados</p>
        <p className="footer-creditos">Rosario, Santa Fe, Argentina</p>
      </div>
    </footer>
  );
};

export default Footer;