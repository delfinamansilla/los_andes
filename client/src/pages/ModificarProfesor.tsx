import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from '../pages/NavbarAdmin';
import '../styles/ModificarProfesor.css'; 
// Interfaz para el objeto Profesor
interface Profesor {
  id: number;
  nombre_completo: string;
  telefono: string;
  mail: string;
}

const ModificarProfesor: React.FC = () => {
  const navigate = useNavigate();

  // Estados para los datos del formulario
  const [idProfesor, setIdProfesor] = useState<number | null>(null);
  const [nombreCompleto, setNombreCompleto] = useState('');
  const [telefono, setTelefono] = useState('');
  const [mail, setMail] = useState('');
  
  // Estados para manejar la carga y los mensajes
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // useEffect para leer los datos del profesor desde localStorage
  useEffect(() => {
    const profesorJSON = localStorage.getItem('profesorSeleccionado');
    
    if (profesorJSON) {
      const profesor = JSON.parse(profesorJSON);
      
      setIdProfesor(profesor.id);
      setNombreCompleto(profesor.nombre_completo);
      setTelefono(profesor.telefono);
      setMail(profesor.mail);
    } else {
      setError('No se ha seleccionado ningún profesor para modificar.');
    }
    
    setLoading(false);
  }, []);

  // Función para enviar los datos actualizados al backend
  const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();
      console.log("PASO 1: Se hizo clic en 'Guardar Cambios'.");

      setError(null);
      setSuccess(null);

      if (!idProfesor) {
        console.error("ERROR: El ID del profesor es nulo. No se puede continuar.");
        setError('Error: No se ha podido identificar al profesor a modificar.');
        return;
      }
      console.log(`PASO 2: Se va a actualizar el profesor con ID: ${idProfesor}`);

      try {
        const params = new URLSearchParams();
        params.append('action', 'actualizar');
        params.append('id', String(idProfesor));
        params.append('nombre_completo', nombreCompleto);
        params.append('telefono', telefono);
        params.append('mail', mail);
        console.log("PASO 3: Enviando estos datos al backend:", params.toString());

        const response = await fetch('http://localhost:8080/club/profesor', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: params.toString(),
        });

        console.log("PASO 4: Respuesta recibida del servidor. Status:", response.status);

        // Leemos la respuesta como texto para verla sin importar qué sea
        const rawText = await response.clone().text();
        console.log("       Respuesta CRUDA del servidor:", rawText);

        const data = await response.json();
        console.log("PASO 5: Respuesta convertida a JSON:", data);

        if (response.ok && data.status === 'ok') {
          console.log("¡ÉXITO! La respuesta del servidor es OK y el status es 'ok'.");
          setSuccess('✅ ¡Profesor actualizado correctamente!');
        } else {
          console.error("FALLO: La respuesta del servidor no fue 'ok' o el status no era 'ok'.");
          throw new Error(data.message || 'Error al actualizar el profesor.');
        }
        
      } catch (err: any) {
        console.error("ERROR CATASTRÓFICO: Se ha producido un error en el bloque catch.", err);
        setError(err.message);
      }
  };

  // Renderizado del Componente
  if (loading) return <div>Cargando...</div>;
  if (error) return <div className="error-box">{error}</div>;

  return (
    // --> CAMBIO 1: La clase del div principal ahora es "modify-profesor-page"
    <div className="modify-profesor-page">
      <NavbarAdmin />
      
      {/* --> CAMBIO 2: Añadimos los dos divs que centran el contenido */}
      <div className="content-area">
        <div className="form-container">
          
          {/* El título y el párrafo ahora van DENTRO del recuadro cremita */}
          <h2>Modificar Profesor</h2>
          <p>Edita los datos y haz clic en "Guardar Cambios".</p>
          
          {/* Mostramos errores aquí si los hay */}
          {error && !success && <p className="error-box">{error}</p>}

          {/* --> CAMBIO 3: La etiqueta <form> ya no necesita una clase */}
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="nombreCompleto">Nombre Completo</label>
              <input
                id="nombreCompleto" type="text" className="inp"
                value={nombreCompleto} onChange={(e) => setNombreCompleto(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label htmlFor="telefono">Teléfono</label>
              <input
                id="telefono" type="tel" className="inp"
                value={telefono} 
                onChange={(e) => setTelefono(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label htmlFor="mail">Email</label>
              <input
                id="mail" type="email" className="inp"
                value={mail} onChange={(e) => setMail(e.target.value)}
              />
            </div>
            
            {/* --> CAMBIO 4: Agrupamos los botones en un div y cambiamos la clase del principal */}
            <div className="button-group">
                <button type="submit" className="btn-save">Guardar Cambios</button>
                <button type="button" onClick={() => navigate('/listado-profesor')} className="btn-secondary">
                  Volver a la Lista
                </button>
            </div>
          </form>

          {success && <p className="success-box">{success}</p>}
        </div>
      </div>
    </div>
  );
};

export default ModificarProfesor;