import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/NavbarAdmin.css';

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
    }, 300); // delay de 300ms antes de cerrar el dropdown
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
            👤 {nombreUsuario}
          </button>

          <button className="btn-usuario" style={{ marginLeft: '10px' }} onClick={handleInicio}>
            🏠 Inicio
          </button>

          <button
            className="btn-usuario"
            style={{ marginLeft: '10px', backgroundColor: '#dc2626', borderColor: '#dc2626' }}
            onClick={() => setShowModal(true)}
          >
            🔒 Cerrar sesión
          </button>
        </div>

        <ul className="navbar-menu">
          <li
            className="menu-item"
            onMouseOver={() => handleMouseEnter('profesores')}
            onMouseOut={handleMouseLeave}
          >
            Profesores
            {activeMenu === 'profesores' && (
              <ul className="dropdown">
                <li><button onClick={() => navigate('/listado-profesor')}>Ver todos</button></li>
                <li><button onClick={() => navigate('/agregar-profesor')}>Agregar nuevo</button></li>
              </ul>
            )}
          </li>

          <li
            className="menu-item"
            onMouseOver={() => handleMouseEnter('canchas')}
            onMouseOut={handleMouseLeave}
          >
            Canchas
            {activeMenu === 'canchas' && (
              <ul className="dropdown">
                <li><button onClick={() => navigate('/canchas')}>Ver todas</button></li>
                <li><button onClick={() => navigate('/canchas/nueva')}>Agregar nueva</button></li>
              </ul>
            )}
          </li>

          <li
            className="menu-item"
            onMouseOver={() => handleMouseEnter('actividades')}
            onMouseOut={handleMouseLeave}
          >
            Actividades
            {activeMenu === 'actividades' && (
              <ul className="dropdown">
                <li><button onClick={() => navigate('/actividades')}>Ver todas</button></li>
                <li><button onClick={() => navigate('/actividades/nueva')}>Agregar nueva</button></li>
              </ul>
            )}
          </li>
        </ul>
      </nav>

      {/* Modal de confirmación */}
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