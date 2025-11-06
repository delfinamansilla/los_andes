import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/Login.css';

const Login: React.FC = () => {
  const [mail, setMail] = useState('');
  const [contrasenia, setContrasenia] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [isVisible, setIsVisible] = useState(false);
  const navigate = useNavigate();

  const handleClick = () => setIsVisible(!isVisible);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    try {
      const params = new URLSearchParams();
      params.append('action', 'login');
      params.append('mail', mail);
      params.append('contrasenia', contrasenia);
      const res = await fetch('http://localhost:8080/club/usuario', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        credentials: 'include',
        body: params.toString(),
      });

      const text = await res.text();

      let data: any = null;
      try {
        data = JSON.parse(text);
      } catch {
        console.warn('No se pudo parsear JSON.');
      }

      if (data && data.status === 'ok') {
        localStorage.setItem('usuario', JSON.stringify(data.usuario));

        setSuccess(`✅ Bienvenido ${data.usuario.nombre_completo}`);
		setTimeout(() => {
		  if (data.usuario.rol === 'socio') {
		    navigate('/inicio-socio');
		  } else {
		    navigate('/inicio-admin');
		  }
		}, 2000);

      } else if (res.status === 401) {
        setError('❌ Correo o contraseña incorrectos.');
      } else {
        setError('⚠ Error inesperado en el servidor.');
      }
    } catch (err) {
      console.error(err);
      setError('🚫 Error al conectar con el servidor.');
    }
  };

  return (
    <div className="home-page">
      <div className="home-content">
        <h2>Bienvenido al Club Los Andes</h2>
        <p>Iniciá sesión para acceder a tu cuenta o registrate si todavía no tenés una.</p>

        <div className="form_inicio">
          <h3>Iniciar Sesión</h3>
          <form onSubmit={handleSubmit}>
            <input
              type="email"
              className="inp"
              placeholder="Email"
              value={mail}
              onChange={(e) => setMail(e.target.value)}
              required
            />

            <div className="relative password-field">
              <input
                type={isVisible ? 'text' : 'password'}
                className="inp password-input"
                placeholder="Contraseña"
                value={contrasenia}
                onChange={(e) => setContrasenia(e.target.value)}
                required
              />
              <button type="button" onClick={handleClick}>
                {isVisible ? 'Ocultar' : 'Ver'}
              </button>
            </div>

            <button type="submit" className="btn_is">Iniciar Sesión</button>
          </form>

          {/* Mostrar mensajes */}
          {error && <p className="error-box">{error}</p>}
          {success && <p className="success-box">{success}</p>}

          <hr style={{ margin: '20px 0', opacity: 0.4 }} />

          <p>
            ¿No tenés cuenta? <Link to="/registro">Registrate acá</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;


