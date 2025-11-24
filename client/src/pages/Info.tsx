import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import NavbarSocio from './NavbarSocio';
import Footer from './Footer';
import '../styles/info.css';

interface Contenido {
  titulo: string;
  texto?: string;
  lista?: string[];
}

const Info: React.FC = () => {
  const { tipo } = useParams();
  const navigate = useNavigate();

  const contenidos: Record<string, Contenido> = {

    reglamento: {
      titulo: 'Reglamento del Club',
      lista: [
        'Presentar credencial al ingresar y tener la cuota al día.',
        'Prohibido fumar en áreas cerradas.',
        'No manipular equipamiento sin autorización del personal.',
        'Menores de 14 años deben ingresar acompañados por un adulto.',
        'Mantener el orden e higiene en vestuarios y espacios comunes.',
        'Prohibidas conductas agresivas, insultos o daños al patrimonio.',
        'Puntualidad obligatoria para actividades grupales.',
        'El personal puede intervenir ante incumplimientos o uso indebido de instalaciones.'
      ]
    },

    beneficios: {
      titulo: 'Beneficios para los Socios',
      lista: [
        'Acceso completo a instalaciones recreativas.',
        'Uso libre del gimnasio y áreas deportivas.',
        'Descuentos en clases especiales, talleres y eventos.',
        'Prioridad en inscripciones y reservas.',
        'Convenios con comercios para descuentos en artículos deportivos e indumentaria.',
        'Participación en jornadas de salud y actividades familiares gratuitas.'
      ]
    },

    'preguntas-frecuentes': {
      titulo: 'Preguntas Frecuentes',
      lista: [
        '¿Cómo me asocio? Presentando DNI, un servicio y completando la ficha de inscripción.',
        '¿Puedo invitar amigos? Sí, con pases diarios o invitaciones especiales.',
        '¿Cómo reservo actividades? Desde la app oficial o en administración.',
        '¿Qué formas de pago se aceptan? Presencial o débito automático.',
        '¿Puedo cambiar mi plan? Sí, acercándote a administración.',
        '¿Qué hago si pierdo mi credencial? Solicitar reposición presentando DNI.'
      ]
    },

    terminos: {
      titulo: 'Términos y Condiciones',
      lista: [
        'El socio debe usar las instalaciones de forma responsable.',
        'Debe respetar horarios y indicaciones del personal.',
        'El incumplimiento puede generar advertencias o suspensión.',
        'El club no se responsabiliza por objetos extraviados.',
        'Ciertas actividades requieren apto físico actualizado.',
        'La institución puede modificar horarios o reglamentos según necesidad.'
      ]
    },

    privacidad: {
      titulo: 'Política de Privacidad',
      texto:
        'Toda información personal es tratada con estrictas normas de seguridad. No se comparte con terceros sin consentimiento, salvo obligación legal. Los socios pueden solicitar modificación o eliminación de sus datos en cualquier momento.'
    },

    nosotros: {
      titulo: 'Sobre Nosotros',
      texto:
        'Fundado en 1952, el Club Deportivo Los Andes es una institución emblemática de Rosario, dedicada al deporte, la cultura y la comunidad. Con más de 70 años de trayectoria, seguimos creciendo junto a nuestros socios.'
    }
  };

  const data = contenidos[tipo ?? ''] || {
    titulo: 'Información no encontrada',
    texto: 'La sección que intentas acceder no existe.'
  };

  return (
	<div className="info-page">
	      <NavbarSocio />
	      
	      <div className="info-main-content">
	        <h1>{data.titulo}</h1>

	        <div className="info-card">
	          {data.texto && <p>{data.texto}</p>}

	          {data.lista && (
	            <ul className="lista-info">
	              {data.lista.map((item, index) => (
	                <li key={index}>{item}</li>
	              ))}
	            </ul>
	          )}
	        </div>

	        <button className="btn-volver" onClick={() => navigate('/inicio-socio')}>
	          Volver
	        </button>
	      </div>

	      <Footer />
	    </div>
  );
};

export default Info;
