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

          const res = await fetch('https://losandesback-production.up.railway.app/usuario', {
              method: 'POST',
              headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
              body: params.toString(),
          });
          const data = await res.json();
          if (res.ok) setSuccess('📧 Revisa tu correo para cambiar la clave.');
          else setError(data.error || 'Error al enviar.');
      } catch(err) { setError('Error de conexión'); }
    };
	
	const verificarCuotas = async (usuarioId: number): Promise<boolean> => {
	    try {
		
	      const resCuotas = await fetch('https://losandesback-production.up.railway.app/cuota?action=listar');
	      console.log(resCuotas);
	      if (!resCuotas.ok) {
	        console.error('Error al obtener cuotas');
	        return true; 
	      }

	      const todasLasCuotas = await resCuotas.json();

	      const cuotasOrdenadas = todasLasCuotas.sort((a: any, b: any) => {
	        const fechaA = new Date(a.fecha_vencimiento);
	        const fechaB = new Date(b.fecha_vencimiento);
	        return fechaB.getTime() - fechaA.getTime();
	      });

	      const ultimasDosCuotas = cuotasOrdenadas.slice(0, 2);
		  

	      if (ultimasDosCuotas.length < 2) {
	        return true; 
	      }

	      const resPagos = await fetch(`https://losandesback-production.up.railway.app/pagocuota?action=listar_por_usuario&id_usuario=${usuarioId}`);
	      
	      if (!resPagos.ok) {
	        console.error('Error al obtener pagos');
	        return true; 
	      }

	      const pagosUsuario = await resPagos.json();


	      const idUltimasCuotas = ultimasDosCuotas.map((c: any) => c.id);
	      
	      const tienePagoPrimeraQuota = pagosUsuario.some((pago: any) => 
	        pago.id_cuota === idUltimasCuotas[0]
	      );
	      
	      const tienePagoSegundaCuota = pagosUsuario.some((pago: any) => 
	        pago.id_cuota === idUltimasCuotas[1]
	      );

	      if (!tienePagoPrimeraQuota && !tienePagoSegundaCuota) {
	        return false;
	      }

	      return true;

	    } catch (err) {
	      console.error('Error al verificar cuotas:', err);
	      return true;
	    }
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
      const res = await fetch('https://losandesback-production.up.railway.app/usuario', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
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
       
		  if (data.usuario.rol === 'socio') {
			
			const puedeAcceder = await verificarCuotas(data.usuario.id);
			          
	          if (!puedeAcceder) {
	            setError('Tenés las últimas dos cuotas sin pagar. Por favor, acércate a la oficina o envía un mail a losandesclubrosario@gmail.com para regularizar tu situación.');
	            return;
	          }
	        }
			
			localStorage.setItem('usuario', JSON.stringify(data.usuario));

		        setSuccess(`Bienvenido ${data.usuario.nombre_completo}`);
				setTimeout(() => {
				  if (data.usuario.rol === 'socio') {
					navigate('/inicio-socio');
				  } else {
				    navigate('/inicio-admin');
				  }
				}, 2000);

      } else if (res.status === 401) {
        setError('Correo o contraseña incorrectos.');
      } else {
        setError('Error inesperado en el servidor.');
      }
    } catch (err) {
      console.error(err);
      setError('Error al conectar con el servidor.');
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
            
            <h3>{isRecovering ? "Recuperar Contraseña" : "Iniciar Sesión"}</h3>
            
            <form onSubmit={isRecovering ? handleRecuperar : handleSubmit}>
              
              <input
                type="email"
                className="inp"
                placeholder="Email"
                value={mail}
                onChange={(e) => setMail(e.target.value)}
                required
              />

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

              <div style={{textAlign: 'right', marginBottom: '15px', marginTop: '5px'}}>
                  {!isRecovering ? (
                      <span 
                          style={{fontSize: '0.9rem', textDecoration: 'underline', cursor: 'pointer', color: '#20321E'}}
                          onClick={() => { setIsRecovering(true); setError(null); setSuccess(null); }}
                      >
                          ¿Olvidaste tu contraseña?
                      </span>
                  ) : (
                      <span 
                          style={{fontSize: '0.9rem', textDecoration: 'underline', cursor: 'pointer', color: '#20321E'}}
                          onClick={() => { setIsRecovering(false); setError(null); setSuccess(null); }}
                      >
                          Cancelar / Volver
                      </span>
                  )}
              </div>

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


