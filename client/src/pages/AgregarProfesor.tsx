import React, { useState,useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from '../pages/NavbarAdmin';
import '../styles/AgregarProfesor.css';

const AgregarProfesor: React.FC = () => {
  const [nombreCompleto, setNombreCompleto] = useState('');
  const [telefono, setTelefono] = useState('');
  const [mail, setMail] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    if (!nombreCompleto || !telefono || !mail) {
      setError('❌ Todos los campos son obligatorios.');
      return;
    }


    try {
      const params = new URLSearchParams();
      params.append('action', 'agregar');
      params.append('nombre_completo', nombreCompleto);
      params.append('telefono', telefono);
      params.append('mail', mail);
	  
	  const url = 'https://losandesback-production.up.railway.app/profesor';

      const res = await fetch('https://losandesback-production.up.railway.app/profesor', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString(),
      });
	  
	     const responseClone = res.clone();

	     const rawText = await responseClone.text();

      const data = await res.json();

      if (data && data.status === 'ok') {
        setSuccess('¡Profesor agregado exitosamente!');

        setNombreCompleto('');
        setTelefono('');
        setMail('');
      } else {
        setError(data.message || 'Error inesperado al agregar el profesor.');
      }
    } catch (err) {
      console.error(err);
      setError('Error al conectar con el servidor.');
    }
  };

  return (
	
    <div className="agregar-profesor-page">
	<NavbarAdmin />
	<div className="content-area"> 
      <div className="form-container">
	 
        <h2>Agregar Nuevo Profesor</h2>
        <p>Complete los datos para registrar un nuevo profesor en el sistema.</p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="nombreCompleto">Nombre Completo</label>
            <input
              id="nombreCompleto"
              type="text"
              className="inp"
              placeholder="Ej: Juan Pérez"
              value={nombreCompleto}
              onChange={(e) => setNombreCompleto(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="telefono">Teléfono</label>
            <input
              id="telefono"
              type="tel"
              className="inp"
              placeholder="Ej: 1122334455"
              value={telefono}
              onChange={(e) => setTelefono(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="mail">Email</label>
            <input
              id="mail"
              type="email"
              className="inp"
              placeholder="Ej: juan.perez@example.com"
              value={mail}
              onChange={(e) => setMail(e.target.value)}
            />
          </div>

          <button type="submit" className="btn_agregar">Agregar Profesor</button>
        </form>

        {error && <p className="error-box">{error}</p>}
        {success && <p className="success-box">{success}</p>}
      </div>
	  </div>
    </div>
  );
};

export default AgregarProfesor;