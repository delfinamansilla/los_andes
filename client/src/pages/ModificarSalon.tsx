import React, { useState, useEffect } from 'react';
import NavbarAdmin from './NavbarAdmin';
import { useNavigate } from 'react-router-dom';
import '../styles/ModificarSalon.css';

interface Salon {
  id: number;
  nombre: string;
  capacidad: number;
  descripcion: string;
  imagen: string | null;
}

const ModificarSalon: React.FC = () => {
  const navigate = useNavigate();
  const [salon, setSalon] = useState<Salon | null>(null);

  const [mensajeExito, setMensajeExito] = useState('');
  const [mensajeError, setMensajeError] = useState('');

  const [nombre, setNombre] = useState('');
  const [capacidad, setCapacidad] = useState(0);
  const [descripcion, setDescripcion] = useState('');
  const [nuevaImagen, setNuevaImagen] = useState<File | null>(null);

  const [previewImagen, setPreviewImagen] = useState<string | null>(null);

  useEffect(() => {
    const stored = localStorage.getItem('salonSeleccionado');
    if (!stored) {
      navigate('/salones-admin');
      return;
    }
    const s = JSON.parse(stored) as Salon;
    setSalon(s);
    setNombre(s.nombre);
    setCapacidad(s.capacidad);
    setDescripcion(s.descripcion);

    setPreviewImagen(s.imagen);
  }, [navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!salon) return;

    const formData = new FormData();
    formData.append('action', 'actualizar');
    formData.append('id', salon.id.toString());
    formData.append('nombre', nombre);
    formData.append('capacidad', capacidad.toString());
    formData.append('descripcion', descripcion);

    if (nuevaImagen) {
      formData.append('imagen', nuevaImagen);
    }

    try {
      const res = await fetch('https://losandesback-production.up.railway.app/salon', {
        method: 'POST',
        body: formData
      });

      if (res.ok) {
        setMensajeExito('Salón actualizado correctamente');
        localStorage.removeItem('salonSeleccionado');
        setTimeout(() => navigate('/salones-admin'), 2000);
      } else {
        setMensajeError('Error al actualizar el salón');
        setTimeout(() => setMensajeError(''), 3000);
      }
    } catch (err) {
      setMensajeError('Error de conexión con el servidor');
      setTimeout(() => setMensajeError(''), 3000);
    }
  };

  const handleImagenChange = (file: File | null) => {
    setNuevaImagen(file);

    if (file) {
      const reader = new FileReader();
      reader.onload = () => setPreviewImagen(reader.result as string);
      reader.readAsDataURL(file);
    }
  };

  if (!salon) return <p>Cargando salón...</p>;

  return (
    <div className="pag-modificar-salon">
      <NavbarAdmin />

	  <div className="contenido-salon">
	    <h1>Modificar Salón {salon.nombre}</h1>

	    <div className="mensajes-globales">
	      {mensajeExito && <p className="mensaje-exito">{mensajeExito}</p>}
	      {mensajeError && <p className="mensaje-error">{mensajeError}</p>}
	    </div>

	    <div className="form-modificar-salon">
          <form onSubmit={handleSubmit} className="modificar-salon">
            <label>Nombre del salón:</label>
            <input
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
            />

            <label>Capacidad:</label>
            <input
              type="number"
              value={capacidad}
              onChange={(e) => setCapacidad(Number(e.target.value))}
            />

            <label>Descripción:</label>
            <input
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
            />

            <label>Nueva Imagen (opcional):</label>
            <input 
              type="file" 
              accept="image/*" 
              onChange={(e) => handleImagenChange(e.target.files?.[0] || null)} 
            />

            {previewImagen && (
              <div className="imagen-actual">
                <p>Vista previa:</p>
                <img src={previewImagen} alt="Vista previa del salón" />
              </div>
            )}

            <div className="botones-container">
              <button type="submit">Guardar cambios</button>
              <button 
                type="button" 
                onClick={() => navigate('/salones-admin')} 
                className="btn-cancel"
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ModificarSalon;
