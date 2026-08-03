import React from 'react';
import { useState, useEffect } from 'react';
import NavbarAdmin from './NavbarAdmin';
import EstadisticasAdminWidget from './EstadisticasAdminWidget';
import ActividadRecienteWidget from './ActividadRecienteWidget';
import '../styles/InicioAdmin.css';
import { useNavigate } from 'react-router-dom';
interface Usuario {
  nombre_completo: string;
}

const InicioAdmin: React.FC = () => {
  const [nombreAdmin, setNombreAdmin] = useState<string>('Administrador');
  const navigate = useNavigate();
  useEffect(() => {
    const rawUsuario = localStorage.getItem('usuario');
    
    if (rawUsuario) {
      const usuario: Usuario = JSON.parse(rawUsuario);
      setNombreAdmin(usuario.nombre_completo.split(' ')[0]);
    }
  }, []);
  
  return (
    <div className="admin-home-page">
      <NavbarAdmin />
      
      <div className="contenido-admin">
        <div className="bienvenida-admin">
          <h2>Bienvenido, {nombreAdmin}</h2>
          <p>Panel de gestión del club Los Andes Rosario</p>
        </div>

        <div className="seccion-admin">
          <h3 className="titulo-seccion">¿Qué puedes hacer hoy?</h3>
          <div className="grid-guia">
            <div className="item-guia">
              <span className="numero-guia">1</span>
              <div className="contenido-guia">
                <h5>Registrar nuevos socios</h5>
                <p>Agrega miembros al sistema y asigna membresías</p>
              </div>
            </div>
            <div className="item-guia">
              <span className="numero-guia">2</span>
              <div className="contenido-guia">
                <h5>Gestionar reservas de canchas</h5>
                <p>Aprueba, modifica o cancela reservas pendientes</p>
              </div>
            </div>
            <div className="item-guia">
              <span className="numero-guia">3</span>
              <div className="contenido-guia">
                <h5>Programar actividades semanales</h5>
                <p>Crea horarios de clases y asigna profesores</p>
              </div>
            </div>
            <div className="item-guia">
              <span className="numero-guia">4</span>
              <div className="contenido-guia">
                <h5>Revisar estado de pagos</h5>
                <p>Consulta cuotas pendientes y genera recordatorios</p>
              </div>
            </div>
			<div className="item-guia">
			    <span className="numero-guia">5</span>

			    <div className="contenido-guia">
			      <h5>Socios pendientes</h5>

			      <p>Revisar solicitudes de nuevos socios</p>

			     

			    </div>
			  </div>
			  <div
			    className="item-guia"
			  >
			      <span className="numero-guia">6</span>

			      <div className="contenido-guia">
			          <h5>Informe de recaudación</h5>
			          <p>Consultar los pagos realizados por los socios</p>
			      </div>
			  </div>
			  <div
			    className="item-guia"
			  >
			      <span className="numero-guia">7</span>

			      <div className="contenido-guia">
			          <h5>Informe de ocupacion</h5>
			          <p>Consultar la ocupacion de las canchas y salones</p>
			      </div>
			  </div>			  
          </div>
		  
        </div>

        <EstadisticasAdminWidget />
		<ActividadRecienteWidget />

        <div className="grid-dos-columnas">
          <div className="seccion-admin">
            <h3 className="titulo-seccion">Recordatorios de gestión</h3>
            <div className="lista-recordatorios">
              <div className="recordatorio-item">
                <i className="fa-solid fa-calendar-days icono-recordatorio"></i>
                <p>Revisar reservas semanalmente para optimizar disponibilidad</p>
              </div>
              <div className="recordatorio-item">
                <i className="fa-solid fa-clock icono-recordatorio"></i>
                <p>Actualizar horarios de clases al inicio de cada mes</p>
              </div>
              <div className="recordatorio-item">
                <i className="fa-solid fa-receipt icono-recordatorio"></i>
                <p>Verificar pagos pendientes los días 1 y 15</p>
              </div>
              <div className="recordatorio-item">
                <i className="fa-solid fa-wrench icono-recordatorio"></i>
                <p>Programar mantenimiento de canchas trimestralmente</p>
              </div>
              <div className="recordatorio-item">
                <i className="fa-solid fa-chart-column icono-recordatorio"></i>
                <p>Generar reportes mensuales de actividad del club</p>
              </div>
            </div>
          </div>

		  <div className="seccion-admin">
		  	              <h3 className="titulo-seccion">ℹ Información del Sistema</h3>
		  	              <div className="info-sistema">
		  	                <div className="info-item">
		  	                  <span className="info-label">Versión:</span>
		  	                  <span className="info-valor">1.0.3</span>
		  	                </div>
		  	                <div className="info-item">
		  	                  <span className="info-label">Última actualización:</span>
		  	                  <span className="info-valor">15 de Octubre 2024</span>
		  	                </div>
		  	                <div className="info-item">
		  	                  <span className="info-label">Estado del servidor:</span>
		  	                  <span className="info-valor estado-online">🟢 Online</span>
		  	                </div>
		  	                <div className="info-item">
		  	                  <span className="info-label">Base de datos:</span>
		  	                  <span className="info-valor">Conectada</span>
		  	                </div>
		  	                
		  	              </div>
		  	            </div>
		  	          </div>

        <div className="footer-admin">
          <p>Sistema de Gestión - Club Los Andes Rosario</p>
          <p className="footer-copy">© 2024 Todos los derechos reservados</p>
        </div>
      </div>
    </div>
  );
};

export default InicioAdmin;