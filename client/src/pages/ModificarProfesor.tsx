import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from '../pages/NavbarAdmin';
import '../styles/ModificarProfesor.css'; 

interface Profesor {
  id: number;
  nombre_completo: string;
  telefono: string;
  mail: string;
}

const ModificarProfesor: React.FC = () => {
  const navigate = useNavigate();

  const [idProfesor, setIdProfesor] = useState<number | null>(null);
  const [nombreCompleto, setNombreCompleto] = useState('');
  const [telefono, setTelefono] = useState('');
  const [mail, setMail] = useState('');

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

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

  const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();

      setError(null);
      setSuccess(null);

      if (!idProfesor) {
        console.error("ERROR: El ID del profesor es nulo. No se puede continuar.");
        setError('Error: No se ha podido identificar al profesor a modificar.');
        return;
      }

      try {
        const params = new URLSearchParams();
        params.append('action', 'actualizar');
        params.append('id', String(idProfesor));
        params.append('nombre_completo', nombreCompleto);
        params.append('telefono', telefono);
        params.append('mail', mail);

        const response = await fetch('http://localhost:8080/club/profesor', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: params.toString(),
        });

        const rawText = await response.clone().text();

        const data = await response.json();

        if (response.ok && data.status === 'ok') {
          setSuccess('✅ ¡Profesor actualizado correctamente!');
        } else {
          throw new Error(data.message || 'Error al actualizar el profesor.');
        }
        
      } catch (err: any) {
        setError(err.message);
      }
  };

  if (loading) return <div>Cargando...</div>;
  if (error) return <div className="error-box">{error}</div>;

  return (
    <div className="modify-profesor-page">
      <NavbarAdmin />

      <div className="content-area">
        <div className="form-container">

          <h2>Modificar Profesor</h2>
          <p>Edita los datos y haz clic en "Guardar Cambios".</p>

          {error && !success && <p className="error-box">{error}</p>}

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