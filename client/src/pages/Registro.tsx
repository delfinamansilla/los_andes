import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Registro: React.FC = () => {
  const [rol, setRol] = useState<'socio' | 'administrador'>('socio');
  const [formData, setFormData] = useState({
    nombre_completo: '',
    dni: '',
    telefono: '',
    mail: '',
    fecha_nacimiento: '',
    contrasenia: '',
    nro_socio: '',
  });
  const [isPasswordVisible, setIsPasswordVisible] = useState(false);
  const [error, setError] = useState<string | null>(null);
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
      if (formData.nro_socio) params.append('nro_socio', formData.nro_socio);

      const res = await fetch('http://localhost:8080/club/usuario', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString(),
      });

      if (res.ok) {
        alert('✅ Usuario registrado correctamente');
        navigate('/login');
      } else {
        setError('⚠ Error al registrar el usuario.');
      }
    } catch (err) {
      console.error(err);
      setError('🚫 Error al conectar con el servidor.');
    }
  };

  return (
    <div className="form-container">
      <h2>Registro de {rol === 'socio' ? 'Socio' : 'Administrador'}</h2>

      {error && <p className="error-box">{error}</p>}

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


        <div className="relative password-field">
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

        <button type="submit">Registrarse</button>
      </form>

      <div className="separador">
        <button onClick={toggleRol}>
          {rol === 'socio'
            ? 'Registrarme como Administrador'
            : 'Registrarme como Socio'}
        </button>
      </div>
    </div>
  );
};

export default Registro;
