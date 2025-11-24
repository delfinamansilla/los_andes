import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';
import '../styles/AgregarPartido.css';



const AgregarPartido: React.FC = () => {

	const navigate = useNavigate();
	const [formData, setFormData] = useState({
	  fecha: '',
	  oponente: '',
	  hora_desde: '',
	  hora_hasta: '',
	  categoria: '',
	  precio_entrada: '',
	  id_cancha: '',
	  id_actividad:''
	});
	
	const [error, setError] = useState<string | null>(null);
	const [success, setSuccess] = useState<string | null>(null);


	
	const [actividades, setActividades] = useState<any[]>([]);
	const [canchas, setCanchas] = useState<any[]>([]);
	const [loading, setLoading] = useState(false);
	const [partido, setPartido] = useState<any | null>(null);
	
	useEffect(() => {
	  const fetchData = async () => {
	    try {
	      const [actRes, canchaRes] = await Promise.all([
	        fetch('https://losandesback-production.up.railway.app/actividad?action=listar'),
	        fetch('https://losandesback-production.up.railway.app/cancha?action=listar'),
	      ]);

	      const actividadesData = await actRes.json();
	      const canchasData = await canchaRes.json();

	      setActividades(actividadesData);
	      setCanchas(canchasData);
	    } catch (err) {
	      console.error('Error al cargar datos:', err);
	    }
	  };

	  fetchData();
	}, []);
	
	const handleSubmit = async (e: React.FormEvent) => {
	  e.preventDefault();
	  setLoading(true);
	  setError(null);
	  setSuccess(null);


	  const payload = {
		fecha: formData.fecha?.slice(0,10),
		oponente: formData.oponente,
		hora_desde: formData.hora_desde,
		hora_hasta: formData.hora_hasta,
		categoria: formData.categoria,
		precio_entrada: parseFloat(formData.precio_entrada),
		id_cancha: formData.id_cancha === "oponente" ? null : parseInt(formData.id_cancha),
		id_actividad:parseInt(formData.id_actividad)

	  };

	  try {
	    const res = await fetch('https://losandesback-production.up.railway.app/partido?action=crear', {
	      method: 'POST',
	      headers: { 'Content-Type': 'application/json' },
	      body: JSON.stringify(payload),
	    });

	    const text = await res.text();
	    const data = JSON.parse(text);
	    if (!res.ok) throw new Error(data.message || 'Error al crear el partido');
	  
	  const nuevaActividad = data.actividad || payload;
	  setPartido(nuevaActividad);
	  
	  setSuccess('✅ Partido creado correctamente');


	    
	  } catch (err) {
	    console.error('Error al crear actividad:', err);
		setError('❌ Error al crear el partido.');



	  } finally {
	    setLoading(false);
	  }
	};
	
	const handleChange = (e: React.ChangeEvent<any>) => {
	  setFormData(prev => ({
	    ...prev,
	    [e.target.name]: e.target.value
	  }));
	};

	
  return (
	<div>
	<NavbarAdmin/>
	<div className="page-container">
	  <h2>Nuevo Partido</h2>
	  <form className="form-partido" onSubmit={handleSubmit}>

	    <label>
	      Fecha partido:
	      <input type="date" name="fecha" value={formData.fecha} onChange={handleChange} required />
	    </label>
		
		<label>
		  Oponente:
		  <textarea name="oponente" value={formData.oponente} onChange={handleChange} />
		</label>
		
		<label>
		  Hora inicio:
		<input type="time" name="hora_desde" value={formData.hora_desde} onChange={handleChange} />
		</label>
		
		<label>
		  Hora fin:
		<input type="time" name="hora_hasta" value={formData.hora_hasta} onChange={handleChange} />
		</label>
		
		<label>
		  Categoria:
		  <textarea name="categoria" value={formData.categoria} onChange={handleChange} />
		</label>

		<label>
		  Precio de la entrada:
		  <input type="number" name="precio_entrada" value={formData.precio_entrada}
		    onChange={handleChange} step="0.01" min="0"
		  />
		</label>
		
		<label>
		  Cancha:
		  <select name="id_cancha" value={formData.id_cancha} onChange={handleChange} required>
		    <option value="">Seleccionar cancha</option>
			<option value="oponente">Cancha del oponente</option>
		    {canchas.map(c => (
		      <option key={c.id} value={c.id}>
		        {c.descripcion} (N° {c.nro_cancha})
		      </option>
		    ))}
		  </select>
		</label>

		<label>
		  Actividad:
		  <select name="id_actividad" value={formData.id_actividad} onChange={handleChange} required>
		    <option value="">Seleccionar actividad</option>
		    {actividades.map(a => (
		      <option key={a.id} value={a.id}>
		        {a.nombre} 
		      </option>
		    ))}
		  </select>
		</label>
		
		<div className='opciones-form'>
		<button type="submit" disabled={loading}>
		  {loading ? (
		    'Creando...'
		  ) : (
		    <>
		      <i className="fa-solid fa-plus"></i> Crear Partido
		    </>
		  )}
		</button>

		<button type="button" onClick={() => navigate('/admin-partidos')}>
		  Volver
		</button>
		</div>
		{error && <p className="error-box">{error}</p>}
		{success && <p className="success-box">{success}</p>}
		</form>

	  </div>


	</div>

    );
};

export default AgregarPartido;