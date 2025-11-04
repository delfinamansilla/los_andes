import React from 'react';
import NavbarSocio from '../pages/NavbarSocio';

const InicioSocio: React.FC = () => {
  return (
    <div>
      <NavbarSocio />
      <div className="contenido-socio">
        <h2>Bienvenido, Socio 👋</h2>
        <p>Usá el menú superior para ver tus actividades y canchas disponibles.</p>
      </div>
    </div>
  );
};

export default InicioSocio;
