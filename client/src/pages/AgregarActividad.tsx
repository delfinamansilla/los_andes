import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';
import '../styles/AgregarActividad.css';


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

  const [profesores, setProfesores] = useState<any[]>([]);
  const [canchas, setCanchas] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [actividadCreada, setActividadCreada] = useState(false);
  const [actividad, setActividad] = useState<any | null>(null);


  // 🔹 Cargar profesores y canchas al montar el componente
  useEffect(() => {
    const fetchData = async () => {
      try {
        const [profRes, canchaRes] = await Promise.all([
          fetch('http://localhost:8080/club/profesor?action=listar'),
          fetch('http://localhost:8080/club/cancha?action=listar'),
        ]);

        const profesoresData = await profRes.json();
        const canchasData = await canchaRes.json();

        setProfesores(profesoresData);
        setCanchas(canchasData);
      } catch (err) {
        console.error('❌ Error al cargar datos:', err);
      }
    };

    fetchData();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    const payload = {
      nombre: formData.nombre,
      cupo: parseInt(formData.cupo),
      descripcion: formData.descripcion,
      inscripcion_desde: formData.inscripcion_desde?.slice(0, 10),
      inscripcion_hasta: formData.inscripcion_hasta?.slice(0, 10),
      id_profesor: parseInt(formData.id_profesor),
      id_cancha: parseInt(formData.id_cancha),
    };

    try {
      const res = await fetch('http://localhost:8080/club/actividad?action=crear', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      const text = await res.text();
      const data = JSON.parse(text);
      if (!res.ok) throw new Error(data.message || 'Error al crear la actividad');
	  
	  const nuevaActividad = data.actividad || payload;
	  setActividad(nuevaActividad);
	  setActividadCreada(true);

      alert('✅ Actividad creada correctamente');
      
    } catch (err) {
      console.error('🚫 Error al crear actividad:', err);
      alert('❌ Error al crear la actividad');
    } finally {
      setLoading(false);
    }
  };
  
  const handleAgregarHorario = () => {
    if (actividadCreada && actividad) {
      localStorage.setItem('actividad', JSON.stringify(actividad));
      navigate('/agregar-horario');
    } else {
      alert('⚠ Primero debes crear la actividad antes de agregar horarios.');
    }
  };


  return (
    <div>
      <NavbarAdmin />
      <div className="page-container">
        <h2>Nueva Actividad</h2>
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

          {/* 🔽 Selector de profesor */}
          <label>
            Profesor:
            <select name="id_profesor" value={formData.id_profesor} onChange={handleChange} required>
              <option value="">Seleccionar profesor</option>
              {profesores.map(p => (
                <option key={p.id} value={p.id}>
                  {p.nombre_completo}
                </option>
              ))}
            </select>
          </label>

          {/* 🔽 Selector de cancha */}
          <label>
            Cancha:
            <select name="id_cancha" value={formData.id_cancha} onChange={handleChange} required>
              <option value="">Seleccionar cancha</option>
              {canchas.map(c => (
                <option key={c.id} value={c.id}>
                  {c.descripcion} (N° {c.nro_cancha})
                </option>
              ))}
            </select>
          </label>

          <div className="form-actions">
            <button type="submit" disabled={loading}>
              {loading ? 'Creando...' : 'Crear Actividad'}
            </button>
            <button type="button" onClick={() => navigate('/actividades')}>
              Cancelar
            </button>

			<button
			  type="button"
			  onClick={handleAgregarHorario}
			  disabled={!actividadCreada}
			>
			  ➕ Agregar Horario
			</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AgregarActividad;