import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/Login.css';
import Navbar from './Navbar';


const Login: React.FC = () => {
  const [mail, setMail] = useState('');
  const [contrasenia, setContrasenia] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [isVisible, setIsVisible] = useState(false);
  const navigate = useNavigate();
  const [isRecovering, setIsRecovering] = useState(false); //acabo d poner yo esa linea

  const handleClick = () => setIsVisible(!isVisible);
  //puse el handle recuperar
  const handleRecuperar = async (e: React.FormEvent) => {
      e.preventDefault();
      setError(null); setSuccess(null);
      if(!mail) { setError('Ingresa tu email.'); return; }

      try {
          const params = new URLSearchParams();
          params.append('action', 'recuperar');
          params.append('mail', mail);

          const res = await fetch('http://localhost:8080/club/usuario', {
              method: 'POST',
              headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
              body: params.toString(),
          });
          const data = await res.json();
          if (res.ok) setSuccess('📧 Revisa tu correo para cambiar la clave.');
          else setError(data.error || 'Error al enviar.');
      } catch(err) { setError('Error de conexión'); }
    };

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
	  <Navbar/>
        <div className="home-content">
          <h2>Bienvenido al Club Los Andes</h2>
          <p>
            {isRecovering 
              ? "Ingresá tu mail y te enviaremos un enlace para cambiar tu clave." 
              : "Iniciá sesión para acceder a tu cuenta."}
          </p>

          <div className="form_inicio">
            
            {/* El título cambia según el estado */}
            <h3>{isRecovering ? "Recuperar Contraseña" : "Iniciar Sesión"}</h3>
            
            {/* El formulario ejecuta una función u otra según el estado */}
            <form onSubmit={isRecovering ? handleRecuperar : handleSubmit}>
              
              <input
                type="email"
                className="inp"
                placeholder="Email"
                value={mail}
                onChange={(e) => setMail(e.target.value)}
                required
              />

              {/* Si NO estamos recuperando, mostramos el campo contraseña */}
              {!isRecovering && (
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
              )}

              {/* --- AQUÍ ESTÁ EL BOTÓN DE OLVIDASTE TU CONTRASEÑA --- */}
              <div style={{textAlign: 'right', marginBottom: '15px', marginTop: '5px'}}>
                  {!isRecovering ? (
                      <span 
                          style={{fontSize: '0.9rem', textDecoration: 'underline', cursor: 'pointer', color: '#20321E'}}
                          // Al hacer click, activamos el modo recuperación
                          onClick={() => { setIsRecovering(true); setError(null); setSuccess(null); }}
                      >
                          ¿Olvidaste tu contraseña?
                      </span>
                  ) : (
                      <span 
                          style={{fontSize: '0.9rem', textDecoration: 'underline', cursor: 'pointer', color: '#20321E'}}
                          // Botón para Cancelar y volver al Login
                          onClick={() => { setIsRecovering(false); setError(null); setSuccess(null); }}
                      >
                          Cancelar / Volver
                      </span>
                  )}
              </div>
              {/* ----------------------------------------------------- */}

              <button type="submit" className="btn_is">
                  {isRecovering ? "Enviar Enlace" : "Iniciar Sesión"}
              </button>
            </form>

            {error && <p className="error-box">{error}</p>}
            {success && <p className="success-box">{success}</p>}

            {!isRecovering && (
              <>
                  <hr style={{ margin: '20px 0', opacity: 0.4 }} />
                  <p>¿No tenés cuenta? <Link to="/registro">Registrate acá</Link></p>
              </>
            )}
          </div>
        </div>
      </div>
    );
};

export default Login;


