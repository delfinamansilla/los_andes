import React from 'react';
import NavbarAdmin from './NavbarAdmin';
import '../styles/InicioAdmin.css';

const InicioAdmin: React.FC = () => {
  return (
    <div className="admin-home-page">
      <NavbarAdmin />
      
      <div className="contenido-admin">
        {/* Bienvenida */}
        <div className="bienvenida-admin">
          <h2>Bienvenido, Administrador 👋</h2>
          <p>Panel de gestión del Club Deportivo Rosario</p>
        </div>

        {/* Guía Rápida */}
        <div className="seccion-admin">
          <h3 className="titulo-seccion">💡 ¿Qué puedes hacer hoy?</h3>
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
          </div>
        </div>

        <div className="grid-dos-columnas">
          {/* Recordatorios */}
          <div className="seccion-admin">
            <h3 className="titulo-seccion">📌 Recordatorios de Gestión</h3>
            <div className="lista-recordatorios">
              <div className="recordatorio-item">
                <span className="icono-recordatorio">📅</span>
                <p>Revisar reservas semanalmente para optimizar disponibilidad</p>
              </div>
              <div className="recordatorio-item">
                <span className="icono-recordatorio">🕐</span>
                <p>Actualizar horarios de clases al inicio de cada mes</p>
              </div>
              <div className="recordatorio-item">
                <span className="icono-recordatorio">💰</span>
                <p>Verificar pagos pendientes los días 1 y 15</p>
              </div>
              <div className="recordatorio-item">
                <span className="icono-recordatorio">🔧</span>
                <p>Programar mantenimiento de canchas trimestralmente</p>
              </div>
              <div className="recordatorio-item">
                <span className="icono-recordatorio">📊</span>
                <p>Generar reportes mensuales de actividad del club</p>
              </div>
            </div>
          </div>

          {/* Información del Sistema */}
          <div className="seccion-admin">
            <h3 className="titulo-seccion">ℹ️ Información del Sistema</h3>
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
              <div className="ayuda-sistema">
                <h5>📚 Centro de Ayuda</h5>
                <ul>
                  <li><a href="#">Guía de usuario administrador</a></li>
                  <li><a href="#">Preguntas frecuentes (FAQ)</a></li>
                  <li><a href="#">Reportar un problema</a></li>
                  <li><a href="#">Contactar soporte técnico</a></li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        {/* Estadísticas Visuales */}
        <div className="seccion-admin">
          <h3 className="titulo-seccion">📊 Áreas de Gestión</h3>
          <div className="grid-estadisticas">
            <div className="stat-card">
              <div className="stat-icono">👥</div>
              <div className="stat-info">
                <h4>Socios</h4>
                <div className="stat-barra">
                  <div className="stat-progreso" style={{width: '75%'}}></div>
                </div>
                <p className="stat-descripcion">Gestión de membresías activas</p>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icono">🏟️</div>
              <div className="stat-info">
                <h4>Canchas</h4>
                <div className="stat-barra">
                  <div className="stat-progreso" style={{width: '60%'}}></div>
                </div>
                <p className="stat-descripcion">Ocupación y mantenimiento</p>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icono">⚽</div>
              <div className="stat-info">
                <h4>Actividades</h4>
                <div className="stat-barra">
                  <div className="stat-progreso" style={{width: '85%'}}></div>
                </div>
                <p className="stat-descripcion">Clases y eventos programados</p>
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-icono">👨‍🏫</div>
              <div className="stat-info">
                <h4>Profesores</h4>
                <div className="stat-barra">
                  <div className="stat-progreso" style={{width: '70%'}}></div>
                </div>
                <p className="stat-descripcion">Equipo docente disponible</p>
              </div>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="footer-admin">
          <p>Sistema de Gestión - Club Deportivo Rosario</p>
          <p className="footer-copy">© 2024 Todos los derechos reservados</p>
        </div>
      </div>
    </div>
  );
};

export default InicioAdmin;