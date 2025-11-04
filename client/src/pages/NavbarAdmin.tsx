import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../styles/NavbarAdmin.css'; // lo hacemos después

const NavbarAdmin: React.FC = () => {
  const navigate = useNavigate();

  const [activeMenu, setActiveMenu] = useState<string | null>(null);

  const toggleMenu = (menu: string | null) => {
    setActiveMenu(menu);
  };


  return (
    <nav className="navbar-admin">
      <div className="navbar-left">
        <button className="btn-usuario" onClick={() => navigate('/modificar-usuario')}>
          👤 Usuario
        </button>
      </div>

      <ul className="navbar-menu">
        {/* PROFESORES */}
        <li
          className="menu-item"
          onMouseEnter={() => toggleMenu('profesores')}
          onMouseLeave={() => toggleMenu(null)}
        >
          Profesores
          {activeMenu === 'profesores' && (
            <ul className="dropdown">
              <li><Link to="/profesores">Ver todos</Link></li>
              <li><Link to="/profesores/nuevo">Agregar nuevo</Link></li>
              <li><Link to="/profesores/eliminar">Eliminar profesor</Link></li>
            </ul>
          )}
        </li>

        {/* CANCHAS */}
        <li
          className="menu-item"
          onMouseEnter={() => toggleMenu('canchas')}
          onMouseLeave={() => toggleMenu(null)}
        >
          Canchas
          {activeMenu === 'canchas' && (
            <ul className="dropdown">
              <li><Link to="/canchas">Ver todas</Link></li>
              <li><Link to="/canchas/nueva">Agregar nueva</Link></li>
            </ul>
          )}
        </li>

        {/* ACTIVIDADES */}
        <li
          className="menu-item"
          onMouseEnter={() => toggleMenu('actividades')}
          onMouseLeave={() => toggleMenu(null)}
        >
          Actividades
          {activeMenu === 'actividades' && (
            <ul className="dropdown">
              <li><Link to="/actividades">Ver todas</Link></li>
              <li><Link to="/actividades/nueva">Agregar nueva</Link></li>
            </ul>
          )}
        </li>
      </ul>
    </nav>
  );
};

export default NavbarAdmin;
