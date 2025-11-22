import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import '../styles/Login.css';
import Navbar from './Navbar';

const CambioContrasenia: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  
  // Obtener el token de la URL: localhost:3000/cambiar-contrasenia?token=XYZ...
  const token = searchParams.get('token'); 
  
  const [pass1, setPass1] = useState('');
  const [pass2, setPass2] = useState('');
  const [mensaje, setMensaje] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!token) return;

    if (pass1 !== pass2) {
        setMensaje('❌ Las contraseñas no coinciden.');
        return;
    }

    const params = new URLSearchParams();
    params.append('action', 'restablecer'); // Llama al segundo caso del Servlet
    params.append('token', token);          // Enviamos el Token, no el ID
    params.append('nueva_pass', pass1);

    try {
        const res = await fetch('http://localhost:8080/club/usuario', {
            method: 'POST',
            body: params
        });
        const data = await res.json();

        if (res.ok) {
            setMensaje('¡Contraseña cambiada! Redirigiendo...');
            setTimeout(() => navigate('/login'), 3000);
        } else {
            setMensaje('❌ ' + (data.error || 'Error al cambiar contraseña'));
        }
    } catch (err) {
        setMensaje('❌ Error de conexión');
    }
  };

  if (!token) return <div className="home-page"><h2>Enlace inválido</h2></div>;

  return (
    <div className="home-page">
	<Navbar/>
      <div className="home-content">
        <h2>Crear Nueva Contraseña</h2>
        <div className="form_inicio">
            <form onSubmit={handleSubmit}>
                <input type="password" placeholder="Nueva contraseña" className="inp"
                    value={pass1} onChange={e => setPass1(e.target.value)} required />
                
                <input type="password" placeholder="Confirmar contraseña" className="inp"
                    value={pass2} onChange={e => setPass2(e.target.value)} required style={{marginTop:10}} />
                
                <button type="submit" className="btn_is">Confirmar Cambio</button>
            </form>
            {mensaje && <p style={{marginTop:10, fontWeight:'bold'}}>{mensaje}</p>}
        </div>
      </div>
    </div>
  );
};

export default CambioContrasenia;