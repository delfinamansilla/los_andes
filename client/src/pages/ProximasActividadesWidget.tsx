import React, { useEffect, useState } from 'react';
import { Calendar, Clock, MapPin, User, ChevronRight } from 'lucide-react';
import '../styles/ProximasActividadesWidget.css';

interface Inscripcion {
  inscripcion_id: number;
  fecha_inscripcion: string;
  actividad_id: number;
  actividad_nombre: string;
  actividad_descripcion: string;
  profesor_nombre: string;
  cancha_descripcion: string;
  dia: string;
  hora_desde: string;
  hora_hasta: string;
}

const ProximasActividadesWidget: React.FC = () => {
  const [inscripciones, setInscripciones] = useState<Inscripcion[]>([]);
  const [proximaClase, setProximaClase] = useState<{actividad: Inscripcion, horasRestantes: number} | null>(null);
  const [loading, setLoading] = useState(true);

  const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');

  useEffect(() => {
    if (!usuario.id) {
      setLoading(false);
      return;
    }

    const url = `https://losandesback-production.up.railway.app/inscripcion?action=porusuario&id_usuario=${usuario.id}`;

    fetch(url)
      .then(res => res.json())
      .then((data: Inscripcion[]) => {
        setInscripciones(data);
        calcularProximaClase(data);
      })
      .catch(err => {
        console.error('Error cargando actividades:', err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const calcularProximaClase = (actividades: Inscripcion[]) => {
    const ahora = new Date();
    const diaActual = ahora.getDay(); 
    const horaActual = ahora.getHours() + ahora.getMinutes() / 60;

    const diasSemana: {[key: string]: number} = {
      'Domingo': 0, 'Lunes': 1, 'Martes': 2, 'Miércoles': 3,
      'Jueves': 4, 'Viernes': 5, 'Sábado': 6
    };

    let proximaAct: {actividad: Inscripcion, horasRestantes: number} | null = null;
    let menorDiferencia = Infinity;

    actividades.forEach(act => {
      if (!act.dia || !act.hora_desde) return;

      const diaClase = diasSemana[act.dia];
      if (diaClase === undefined) return;

      const [hora, minutos] = act.hora_desde.split(':').map(Number);
      const horaClase = hora + minutos / 60;

      let diasDiferencia = diaClase - diaActual;
      if (diasDiferencia < 0) diasDiferencia += 7; 
      if (diasDiferencia === 0 && horaClase < horaActual) diasDiferencia = 7; 

      const horasDiferencia = diasDiferencia * 24 + (horaClase - horaActual);

      if (horasDiferencia > 0 && horasDiferencia < menorDiferencia) {
        menorDiferencia = horasDiferencia;
        proximaAct = { actividad: act, horasRestantes: Math.round(horasDiferencia) };
      }
    });

    setProximaClase(proximaAct);
  };

  const formatearTiempoRestante = (horas: number): string => {
    if (horas < 1) return 'En menos de 1 hora';
    if (horas < 24) return `En ${Math.round(horas)} horas`;
    const dias = Math.floor(horas / 24);
    const horasRestantes = Math.round(horas % 24);
    if (horasRestantes === 0) return `En ${dias} día${dias > 1 ? 's' : ''}`;
    return `En ${dias}d ${horasRestantes}h`;
  };

  if (loading) {
    return (
      <div className="proximas-actividades-widget loading">
        <p>Cargando actividades...</p>
      </div>
    );
  }

  if (inscripciones.length === 0) {
    return (
      <div className="proximas-actividades-widget empty">
        <div className="widget-header">
          <Calendar size={24} />
          <h3>Mis Próximas Actividades</h3>
        </div>
        <div className="empty-state">
          <p>No estás inscripto a ninguna actividad todavía.</p>
          <a href="/inscripcion-actividad" className="btn-inscribirse">
            Ver actividades disponibles →
          </a>
        </div>
      </div>
    );
  }

  return (
    <div className="proximas-actividades-widget">
      <div className="widget-header">
        <Calendar size={24} />
        <h3>Mis Próximas Actividades</h3>
        <span className="badge-count">{inscripciones.length}</span>
      </div>

      {proximaClase && (
        <div className="proxima-clase-destacada">
          <div className="proxima-clase-header">
            <Clock size={20} />
            <span className="proxima-clase-label">Próxima Clase</span>
          </div>
          <div className="proxima-clase-content">
            <h4>{proximaClase.actividad.actividad_nombre}</h4>
            <div className="proxima-clase-info">
              <span className="tiempo-restante">
                {formatearTiempoRestante(proximaClase.horasRestantes)}
              </span>
              <span className="dia-hora">
                {proximaClase.actividad.dia} • {proximaClase.actividad.hora_desde}
              </span>
            </div>
          </div>
        </div>
      )}

      <div className="actividades-lista">
        {inscripciones.slice(0, 3).map((inscripcion) => (
          <div key={inscripcion.inscripcion_id} className="actividad-card">
            <div className="actividad-main">
              <h4 className="actividad-nombre">{inscripcion.actividad_nombre}</h4>
              <p className="actividad-descripcion">{inscripcion.actividad_descripcion}</p>
            </div>
            
            <div className="actividad-detalles">
              {inscripcion.dia && inscripcion.hora_desde && (
                <div className="detalle-item">
                  <Clock size={16} />
                  <span>{inscripcion.dia} {inscripcion.hora_desde} - {inscripcion.hora_hasta}</span>
                </div>
              )}
              
              {inscripcion.profesor_nombre && (
                <div className="detalle-item">
                  <User size={16} />
                  <span>{inscripcion.profesor_nombre}</span>
                </div>
              )}
              
              {inscripcion.cancha_descripcion && (
                <div className="detalle-item">
                  <MapPin size={16} />
                  <span>{inscripcion.cancha_descripcion}</span>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>

      {inscripciones.length > 3 && (
        <div className="ver-mas">
          <p className="texto-mas">Y {inscripciones.length - 3} actividad{inscripciones.length - 3 > 1 ? 'es' : ''} más</p>
        </div>
      )}

      <div className="widget-footer">
        <a href="/mis-actividades" className="btn-ver-todas">
          Ver todas mis actividades
          <ChevronRight size={18} />
        </a>
      </div>
    </div>
  );
};

export default ProximasActividadesWidget;