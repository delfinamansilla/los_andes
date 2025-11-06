import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/NavbarSocio.css';

const NavbarSocio: React.FC = () => {
  const navigate = useNavigate();
  const [activeMenu, setActiveMenu] = useState<string | null>(null);
  const [nombreUsuario, setNombreUsuario] = useState<string>('Usuario');
  const [showModal, setShowModal] = useState(false);

  const timeoutRef = useRef<NodeJS.Timeout | null>(null);

  const handleMouseEnter = (menu: string) => {
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    setActiveMenu(menu);
  };

  const handleMouseLeave = () => {
    timeoutRef.current = setTimeout(() => {
      setActiveMenu(null);
    }, 300); 
  };

  useEffect(() => {
    const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
    if (usuario.nombre_completo) setNombreUsuario(usuario.nombre_completo);
  }, []);

  const handleCerrarSesion = () => {
    localStorage.removeItem('usuario');
    navigate('/');
  };

  const handleInicio = () => navigate('/inicio-socio');

  return (
      <>
        <nav className="navbar-socio">
          <div className="navbar-left">
            <button className="btn-usuario" onClick={() => navigate('/modificar-usuario')}>
              <i className="fa-solid fa-user"></i> {nombreUsuario}
            </button>

            <button className="btn-usuario" style={{ marginLeft: '10px' }} onClick={handleInicio}>
              <i className="fa-solid fa-house-user"></i> Inicio
            </button>

  		  <button
  		    className="btn-usuario"
  		    style={{ marginLeft: '10px' }}
  		    onClick={() => setShowModal(true)}
  		  >
  		    <i className="fa-solid fa-lock"></i> Cerrar sesión
  		  </button>
          </div>

          <ul className="navbar-menu">
            <li
              className="menu-item"
              onMouseOver={() => handleMouseEnter('actividades')}
              onMouseOut={handleMouseLeave}
            >
              <i className="fa-solid fa-person-walking"></i> Actividades
              {activeMenu === 'actividades' && (
                <ul className="dropdown">
                  <li><button onClick={() => navigate('/inscripcion-actividad')}>Ver todas</button></li>
                  <li><button onClick={() => navigate('/mis-actividades')}>Ver mis actividades</button></li>
                </ul>
              )}
            </li>

            <li
              className="menu-item"
              onMouseOver={() => handleMouseEnter('canchas')}
              onMouseOut={handleMouseLeave}
            >
              <i className="fa-solid fa-volleyball"></i> Canchas
              {activeMenu === 'canchas' && (
                <ul className="dropdown">
                  <li><button onClick={() => navigate('/canchas')}>Ver todas</button></li>
                </ul>
              )}
            </li>
          </ul>
        </nav>

        {showModal && (
          <div className="modal-backdrop">
            <div className="modal">
              <h3>¿Estás seguro que quieres cerrar sesión?</h3>
              <div className="modal-buttons">
                <button onClick={handleCerrarSesion} className="btn-confirm">Sí</button>
                <button onClick={() => setShowModal(false)} className="btn-cancel">No</button>
              </div>
            </div>
          </div>
        )}
      </>
    );
  };

export default NavbarSocio;


