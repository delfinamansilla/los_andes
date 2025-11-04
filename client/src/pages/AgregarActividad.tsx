import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';

const AgregarActividad: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    nombre: '',
    cupo: '',
    descripcion: '',
    inscripcion_desde: '',
    inscripcion_hasta: '',
    id_profesor: '',
    id_cancha: '',
  });

  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setLoading(true);

    try {
      const res = await fetch('http://localhost:8080/club/actividad?action=agregar', {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nombre: formData.nombre,
          cupo: parseInt(formData.cupo),
          descripcion: formData.descripcion,
          inscripcion_desde: formData.inscripcion_desde,
          inscripcion_hasta: formData.inscripcion_hasta,
          id_profesor: parseInt(formData.id_profesor),
          id_cancha: parseInt(formData.id_cancha),
        }),
      });

      if (res.ok) {
        setSuccess('✅ Actividad creada correctamente');
        setTimeout(() => navigate('/actividades'), 1500);
      } else {
        const txt = await res.text();
        console.error('Error al crear actividad:', txt);
        setError('❌ No se pudo crear la actividad');
      }
    } catch (err) {
      console.error('Error de red:', err);
      setError('🚫 Error de conexión con el servidor');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <NavbarAdmin />
      <div className="page-container">
        <h2>🆕 Nueva Actividad</h2>
        <form className="form-actividad" onSubmit={handleSubmit}>
          <label>
            Nombre:
            <input type="text" name="nombre" value={formData.nombre} onChange={handleChange} required />
          </label>

          <label>
            Cupo:
            <input type="number" name="cupo" value={formData.cupo} onChange={handleChange} required />
          </label>

          <label>
            Descripción:
            <textarea name="descripcion" value={formData.descripcion} onChange={handleChange} />
          </label>

          <label>
            Inscripción Desde:
            <input type="date" name="inscripcion_desde" value={formData.inscripcion_desde} onChange={handleChange} required />
          </label>

          <label>
            Inscripción Hasta:
            <input type="date" name="inscripcion_hasta" value={formData.inscripcion_hasta} onChange={handleChange} required />
          </label>

          <label>
            ID Profesor:
            <input type="number" name="id_profesor" value={formData.id_profesor} onChange={handleChange} required />
          </label>

          <label>
            ID Cancha:
            <input type="number" name="id_cancha" value={formData.id_cancha} onChange={handleChange} required />
          </label>

          <div className="form-actions">
            <button type="submit" disabled={loading}>
              {loading ? 'Creando...' : 'Crear Actividad'}
            </button>
            <button type="button" onClick={() => navigate('/actividades')}>Cancelar</button>
          </div>
        </form>

        {error && <p className="error-box">{error}</p>}
        {success && <p className="success-box">{success}</p>}
      </div>
    </div>
  );
};

export default AgregarActividad;
