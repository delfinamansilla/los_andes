import React, { useState, useEffect } from 'react';
import NavbarSocio from './NavbarSocio';
import Footer from './Footer';
import { Clock, MapPin, Phone, Mail, Award, Shield, Users, Dumbbell } from 'lucide-react';
import '../styles/InicioSocio.css';
import EstadoCuentaWidget from './EstadoCuentaWidget';
import ProximasActividadesWidget from './ProximasActividadesWidget';
import ReservasActivasWidget from './ReservasActivasWidget';
import EstadisticasWidget from './EstadisticasWidget';

const InicioSocio: React.FC = () => {
	const [esCumple, setEsCumple] = useState(false);
	  const [nombreUsuario, setNombreUsuario] = useState("");

	  useEffect(() => {
	      const userStr = localStorage.getItem('usuario');
	      if (userStr) {
	        const user = JSON.parse(userStr);
	        setNombreUsuario(user.nombre_completo || "Socio");

	        if (user.fecha_nacimiento) {
	          const nacimiento = new Date(user.fecha_nacimiento + 'T00:00:00');
	          const hoy = new Date();

	          const esMes = nacimiento.getMonth() === hoy.getMonth();
	          const esDia = nacimiento.getDate() === hoy.getDate();

	          if (esMes && esDia) {
	            setEsCumple(true);
	            
	            
	            const timer = setTimeout(() => {
	              setEsCumple(false);
	            }, 15000);

	            return () => clearTimeout(timer);
	          }
	        }
	      }
	    }, []);

	    // aca dibujamos los globitos
		const renderBalloons = () => {
		    const balloons = [];
		    const colors = ['#FF6B6B', '#4ECDC4', '#FFE66D', '#1A535C', '#FF9F1C', '#9B5DE5'];

		    for (let i = 0; i < 30; i++) {
		      const randomLeft = Math.floor(Math.random() * 100);
		      
		 
		      const randomDuration = Math.floor(Math.random() * 3) + 10; 
		
		      const randomDelay = Math.floor(Math.random() * 4);
		      
		      const randomColor = colors[Math.floor(Math.random() * colors.length)];
		      const randomScale = 0.7 + Math.random() * 0.6;

		      const style = {
		        left: `${randomLeft}%`,
		        animationDuration: `${randomDuration}s`, 
		        animationDelay: `${randomDelay}s`,      
		        transform: `scale(${randomScale})`,
		        '--balloon-color': randomColor,
		      } as React.CSSProperties;

		      balloons.push(
		        <div key={i} className="balloon" style={style}></div>
		      );
		    }
		    return balloons;
		  };
		 
  return (
    <div className="inicio-socio-page">
      <NavbarSocio />
	  
	  {/* --- LÓGICA DE CUMPLEAÑOS --- */}
	        {esCumple && (
	          <>
	            <div className="balloon-container">
	              {renderBalloons()}
	            </div>
	            <div className="birthday-message">
	              <h3>¡Feliz Cumpleaños, {nombreUsuario}!</h3>
	              <p style={{margin:0, fontSize: '0.9rem'}}>El club te desea un gran día.</p>
	            </div>
	          </>
	        )}
      
      <div className="contenido-socio">
	  
        <div className="bienvenida-header">
          <h2>Los Andes</h2>
          <p>Tu espacio deportivo en el corazón de Rosario</p>
        </div>
		<EstadoCuentaWidget />
			  <ProximasActividadesWidget />
			  <ReservasActivasWidget />
			  <EstadisticasWidget />
        <div className="seccion-card">
          <div className="seccion-titulo">
            <Clock className="icono-titulo" size={28} />
            <h3>Horarios de Atención</h3>
          </div>
          <div className="grid-horarios">
            <div className="horario-card horario-azul">
              <p className="horario-dia">Lunes a Viernes</p>
              <p className="horario-hora">7:00 AM - 11:00 PM</p>
            </div>
            <div className="horario-card horario-azul">
              <p className="horario-dia">Sábados y Domingos</p>
              <p className="horario-hora">8:00 AM - 10:00 PM</p>
            </div>
            <div className="horario-card horario-verde">
              <p className="horario-dia">Secretaría</p>
              <p className="horario-hora">Lunes a Viernes: 9:00 AM - 6:00 PM</p>
            </div>
            <div className="horario-card horario-verde">
              <p className="horario-dia">Buffet</p>
              <p className="horario-hora">Todos los días: 8:00 AM - 10:00 PM</p>
            </div>
          </div>
        </div>

        <div className="seccion-card">
          <div className="seccion-titulo">
            <Award className="icono-titulo" size={28} />
            <h3>Beneficios de tu membresía</h3>
          </div>
          <div className="grid-beneficios">
            <div className="beneficio-item">
              <div className="beneficio-icono beneficio-azul">
                <Dumbbell size={20} />
              </div>
              <div>
                <p className="beneficio-titulo">Acceso ilimitado al gimnasio</p>
                <p className="beneficio-desc">Equipamiento completo y profesores capacitados</p>
              </div>
            </div>
            <div className="beneficio-item">
              <div className="beneficio-icono beneficio-verde">
                <Users size={20} />
              </div>
              <div>
                <p className="beneficio-titulo">Clases grupales incluidas</p>
                <p className="beneficio-desc">Spinning, yoga, funcional y más</p>
              </div>
            </div>
            <div className="beneficio-item">
              <div className="beneficio-icono beneficio-morado">
                <Shield size={20} />
              </div>
              <div>
                <p className="beneficio-titulo">Seguro deportivo</p>
                <p className="beneficio-desc">Cobertura en todas las actividades del club</p>
              </div>
            </div>
            <div className="beneficio-item">
              <div className="beneficio-icono beneficio-naranja">
                <Award size={20} />
              </div>
              <div>
                <p className="beneficio-titulo">Descuentos especiales</p>
                <p className="beneficio-desc">20% OFF en alquiler de canchas y buffet</p>
              </div>
            </div>
          </div>
          <div className="invitados-nota">
            <p><strong>Invitados:</strong> Cada socio puede traer hasta 2 invitados por mes (abonan tarifa reducida)</p>
          </div>
        </div>

        <div className="seccion-card">
          <div className="seccion-titulo">
            <Shield className="icono-titulo" size={28} />
            <h3>Reglamento del Club</h3>
          </div>
          <div className="reglamento-lista">
            <div className="reglamento-item">
              <p className="reglamento-titulo">Reserva de canchas</p>
              <p className="reglamento-desc">Las reservas se realizan con 48hs de anticipación. Cancelaciones con menos de 24hs se cobran el 50%.</p>
            </div>
            <div className="reglamento-item">
              <p className="reglamento-titulo">Vestimenta deportiva</p>
              <p className="reglamento-desc">Obligatorio el uso de calzado deportivo adecuado. Remera y short/calza requeridos en todas las áreas.</p>
            </div>
            <div className="reglamento-item">
              <p className="reglamento-titulo">Respeto y convivencia</p>
              <p className="reglamento-desc">Mantener el orden y limpieza. Respetar horarios de turnos y no fumar en instalaciones cerradas.</p>
            </div>
            <div className="reglamento-item">
              <p className="reglamento-titulo">Elementos personales</p>
              <p className="reglamento-desc">El club no se responsabiliza por pérdida de objetos. Usar los lockers disponibles.</p>
            </div>
            <div className="reglamento-item">
              <p className="reglamento-titulo">Menores de edad</p>
              <p className="reglamento-desc">Los menores de 16 años deben estar acompañados por un adulto responsable.</p>
            </div>
          </div>
        </div>

		<div className="seccion-card">
		  <div className="seccion-titulo">
		    <Phone className="icono-titulo" size={28} />
		    <h3>Contacto y Ubicación</h3>
		  </div>

		  <div className="grid-contacto-mapa">
		  
		    <div className="contacto-info">
		      <div className="contacto-item">
		        <MapPin className="contacto-icono" size={24} />
		        <div>
		          <p className="contacto-label">Dirección</p>
		          <p className="contacto-valor">Av. Pellegrini 1250</p>
		          <p className="contacto-valor">Rosario, Santa Fe (S2000)</p>
		        </div>
		      </div>
		      <div className="contacto-item">
		        <Phone className="contacto-icono" size={24} />
		        <div>
		          <p className="contacto-label">Teléfono</p>
		          <p className="contacto-valor">(0341) 424-5678</p>
		          <p className="contacto-valor">WhatsApp: +54 9 341 555-1234</p>
		        </div>
		      </div>
		      <div className="contacto-item">
		        <Mail className="contacto-icono" size={24} />
		        <div>
		          <p className="contacto-label">Email</p>
		          <p className="contacto-valor">info@clublosandesrosario.com.ar</p>
		          <p className="contacto-valor">socios@clublosandesrosario.com.ar</p>
		        </div>
		      </div>
		    </div>
		    
		    <div className="map-container">
		      <iframe 
		        width="100%" 
		        height="100%" 
		        frameBorder={0} 
		        scrolling="no" 
		        marginHeight={0} 
		        marginWidth={0} 
		        src="https://www.openstreetmap.org/export/embed.html?bbox=-60.64655%2C-32.95755%2C-60.64345%2C-32.95585&amp;layer=mapnik&amp;marker=-32.9567%2C-60.6450" 
		        style={{ border: 'none', borderRadius: '10px' }}
		      ></iframe>
		    </div>

		  </div>
		</div>
      </div>
	  <Footer />
    </div>
  );
};

export default InicioSocio;
