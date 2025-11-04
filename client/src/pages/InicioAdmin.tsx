import React from 'react';
import NavbarAdmin from '../pages/NavbarAdmin';

const InicioAdmin: React.FC = () => {
  return (
    <div>
      <NavbarAdmin />
      <div className="contenido-admin">
        <h2>Bienvenido, Administrador 👋</h2>
        <p>Usá el menú superior para gestionar profesores, canchas y actividades.</p>
      </div>
    </div>
  );
};

export default InicioAdmin;
