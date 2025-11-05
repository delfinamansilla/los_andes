import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from '../pages/NavbarAdmin';
import '../styles/Registro.css';

const Registro: React.FC = () => {
  const [rol, setRol] = useState<'socio' | 'administrador'>('socio');
  
  const [formData, setFormData] = useState({
    nombre_completo: '',
    dni: '',
    telefono: '',
    mail: '',
    fecha_nacimiento: '',
    contrasenia: '',
  });
  const [isPasswordVisible, setIsPasswordVisible] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const navigate = useNavigate();

  const toggleRol = () => {
    setRol((prev) => (prev === 'socio' ? 'administrador' : 'socio'));
  };

  const togglePasswordVisibility = () => {
    setIsPasswordVisible((prev) => !prev);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    try {
      const params = new URLSearchParams();
      params.append('action', 'registrar');
      params.append('nombre_completo', formData.nombre_completo);
      params.append('dni', formData.dni);
      params.append('telefono', formData.telefono);
      params.append('mail', formData.mail);
      params.append('fecha_nacimiento', formData.fecha_nacimiento);
      params.append('contrasenia', formData.contrasenia);
      params.append('rol', rol);

	  const res = await fetch('http://localhost:8080/club/usuario', {
	    method: 'POST',
	    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
	    body: params.toString(),
	  });

	  const text = await res.text();
	  console.log('Respuesta del servidor:', text);

	  if (res.ok) {
	    setSuccess('✅ Usuario registrado correctamente. Redirigiendo...');
	    setTimeout(() => navigate('/login'), 4000);
	  } else {
	    setError(`⚠ Error al registrar el usuario: ${text}`);
	  }

    } catch (err) {
      console.error(err);
      setError('🚫 Error al conectar con el servidor.');
    }
  };

  return (
    // 1. Añadimos el contenedor principal de la página
	
    <div className="registro-page">
	<NavbarAdmin />
      {/* 2. Añadimos el contenedor que centra el contenido */}
      <div className="content-area">
        {/* El .form-container ahora es el recuadro cremita */}
        <div className="form-container">
          <h2>Registro de {rol === 'socio' ? 'Socio' : 'Administrador'}</h2>

          {/* Mensajes */}
          {error && <p className="error-box">{error}</p>}
          {success && <p className="success-box">{success}</p>}

          <form onSubmit={handleSubmit}>
            <input
              type="text"
              placeholder="Nombre Completo"
              name="nombre_completo"
              value={formData.nombre_completo}
              onChange={handleInputChange}
              required
            />

            <input
              type="text"
              placeholder="DNI"
              name="dni"
              value={formData.dni}
              onChange={handleInputChange}
              required
            />

            <input
              type="tel"
              placeholder="Teléfono"
              name="telefono"
              value={formData.telefono}
              onChange={handleInputChange}
              required
            />

            <input
              type="email"
              placeholder="Email"
              name="mail"
              value={formData.mail}
              onChange={handleInputChange}
              required
            />

            <input
              type="date"
              name="fecha_nacimiento"
              value={formData.fecha_nacimiento}
              onChange={handleInputChange}
              required
            />

            {/* El div de la contraseña ya tiene la clase correcta */}
            <div className="password-field">
              <input
                type={isPasswordVisible ? 'text' : 'password'}
                placeholder="Contraseña"
                name="contrasenia"
                value={formData.contrasenia}
                onChange={handleInputChange}
                required
              />
              <button type="button" onClick={togglePasswordVisibility}>
                {isPasswordVisible ? '🔒' : '👁️'}
              </button>
            </div>

            {/* 3. Cambiamos la clase del botón principal */}
            <button type="submit" className="btn-primary">Registrarse</button>
          </form>

          <div className="separador">
            {/* 4. Cambiamos la clase del botón de cambio de rol */}
            <button onClick={toggleRol} className="btn-toggle-role">
              {rol === 'socio'
                ? 'Registrarme como Administrador'
                : 'Registrarme como Socio'}
            </button>
          </div>

          <div className="volver-container">
            <button onClick={() => navigate('/login')} className="volver-btn">
              Volver al Login
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Registro;
