import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/NavbarAdmin.css';
import logoClub from '../assets/los_andes.png';

const NavbarAdmin: React.FC = () => {
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

  const handleInicio = () => navigate('/inicio-admin');

  return (
    <>
      <nav className="navbar-admin">

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

        <div className="navbar-logo" onClick={() => navigate('/inicio-admin')}>
          <img src={logoClub} alt="logo" />
        </div>

        <ul className="navbar-menu">

          <li
            className="menu-item"
            onMouseOver={() => handleMouseEnter('personas')}
            onMouseOut={handleMouseLeave}
          >
            <i className=""></i> Gestión de personas {/*emoji: fa-solid fa-users*/}
			
            {activeMenu === 'personas' && (
              <ul className="dropdown">
                <li><button onClick={() => navigate('/listado-socio')}>Socios</button></li>
                <hr />
                <li><button onClick={() => navigate('/listado-profesor')}>Profesores</button></li>
                <li><button onClick={() => navigate('/agregar-profesor')}>Agregar profesor</button></li>
              </ul>
            )}
          </li>

          <li
            className="menu-item"
            onMouseOver={() => handleMouseEnter('infra')}
            onMouseOut={handleMouseLeave}
          >
            <i className=""></i> Infraestructura {/*emoji: fa-solid fa-building-columns*/}
            {activeMenu === 'infra' && (
              <ul className="dropdown">
                <li><button onClick={() => navigate('/canchas-admin')}>Canchas</button></li>
                <li><button onClick={() => navigate('/crear')}>Agregar cancha</button></li>
                <hr />
                <li><button onClick={() => navigate('/salones-admin')}>Salones</button></li>
                <li><button onClick={() => navigate('/crear-salon')}>Agregar salón</button></li>
              </ul>
            )}
          </li>

          <li
            className="menu-item"
            onMouseOver={() => handleMouseEnter('finanzas')}
            onMouseOut={handleMouseLeave}
          >
            <i className=""></i> Finanzas {/*emoji: fa-solid fa-money-bill-wave*/}
            {activeMenu === 'finanzas' && (
              <ul className="dropdown">
                <li><button onClick={() => navigate('/listado-cuota')}>Cuotas</button></li>
                <li><button onClick={() => navigate('/agregar-cuota')}>Agregar cuota</button></li>
              </ul>
            )}
          </li>

          <li
            className="menu-item"
            onMouseOver={() => handleMouseEnter('deportes')}
            onMouseOut={handleMouseLeave}
          >
            <i className=""></i> Deportes {/*emoji: fa-solid fa-volleyball*/}
            {activeMenu === 'deportes' && (
              <ul className="dropdown">
                <li><button onClick={() => navigate('/actividades')}>Actividades</button></li>
                <li><button onClick={() => navigate('/actividades/nueva')}>Agregar actividad</button></li>
                <hr />
                <li><button onClick={() => navigate('/admin-partidos')}>Partidos</button></li>
                <li><button onClick={() => navigate('/agregar-partido')}>Agregar partido</button></li>
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

export default NavbarAdmin;