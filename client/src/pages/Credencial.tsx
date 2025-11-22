import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../styles/Credencial.css'; 
import logoClub from '../assets/los_andes.png';
import NavbarSocio from './NavbarSocio';
import Footer from './Footer';
import { QRCodeSVG } from "qrcode.react";

const Credencial: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [socio, setSocio] = useState<any>(null);
  const [error, setError] = useState('');
  const [mensaje, setMensaje] = useState('');
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [imageUrl, setImageUrl] = useState('');

  useEffect(() => {
    if (id) {
      setImageUrl(`http://localhost:8080/club/usuario?action=verfoto&id=${id}&t=${new Date().getTime()}`);
      
      fetch(`http://localhost:8080/club/usuario?action=buscarc&id=${id}`)
        .then(res => res.ok ? res.json() : Promise.reject('Usuario no encontrado'))
        .then(data => setSocio(data))
        .catch(err => setError(err.message));
    }
  }, [id]);
  
  const handleFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
 
    const file = event.target.files?.[0];
    if (!file || !id) return;
    const formData = new FormData();
    formData.append('idUsuario', id);
    formData.append('foto', file);
    try {
      const res = await fetch('http://localhost:8080/club/usuario', { method: 'POST', body: formData });
      const result = await res.json();
      if (res.ok) {
        setMensaje('¡Foto actualizada!');
        setImageUrl(`http://localhost:8080/club/usuario?action=verfoto&id=${id}&t=${new Date().getTime()}`);
      } else {
        setError(result.error || 'Error al subir la foto.');
      }
    } catch (err) {
      setError('Error de conexión.');
    } finally {
      setTimeout(() => { setMensaje(''); setError(''); }, 3000);
    }
  };
  const handleFotoClick = () => {
    fileInputRef.current?.click();
  };

  if (error) return <div className="pagina-credencial"><p>{error}</p></div>;
  if (!socio) return <div className="pagina-credencial"><p>Cargando...</p></div>;

  return (
      <div className="pagina-credencial">
        <NavbarSocio />
        
        <div className="credencial-content-wrapper"> 
          <div className="carnet-socio">
            <header className="carnet-header">
              <h3>Club Los Andes</h3>
              <img src={logoClub} alt="Logo del Club" className="club-logo" />
            </header>
            
            <main className="carnet-body">
              <div className="carnet-foto" onClick={handleFotoClick}>
                <img 
                  src={imageUrl} 
                  alt=""
                  title="Foto de perfil" 
                  onError={(e) => { e.currentTarget.src = 'https://via.placeholder.com/150' }} 
                />
                <div className="foto-overlay">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
                  <span>Cambiar</span>
                </div>
                <input type="file" ref={fileInputRef} onChange={handleFileChange} style={{ display: 'none' }} accept="image/*" />
              </div>

              <div className="carnet-datos">
                <h2>{socio.nombre_completo}</h2>
                <p><strong>DNI:</strong> {socio.dni}</p>
                <p><strong>Rol:</strong> {socio.rol}</p>
                <p><strong>Estado: </strong> 
                  <span className={`estado-badge ${socio.estado ? 'activo' : 'inactivo'}`}>
                    {socio.estado ? ' Activo' : ' Inactivo'}
                  </span>
                </p>
              </div>
			  <div className="carnet-qr">
			    <QRCodeSVG 
			      value={socio.id?.toString() || "socio"} 
			      size={100}
			      bgColor="#DDD8CA"
			      fgColor="#20321E"
			      level="H"
			    />
			  </div>
            </main>
          </div> 

          {mensaje && <p className="mensaje-exito">{mensaje}</p>}
          {error && <p className="mensaje-error">{error}</p>}

          <button onClick={() => navigate('/modificar-usuario')} className="btn-volver">Volver a Mi cuenta</button>
        </div>
        <Footer /> 
      </div> 
    );

};

export default Credencial;