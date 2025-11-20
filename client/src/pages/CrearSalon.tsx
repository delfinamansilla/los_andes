import React, { useState } from 'react';
import NavbarAdmin from './NavbarAdmin';
import { useNavigate } from 'react-router-dom';
import '../styles/CrearSalon.css';

const CrearSalon: React.FC = () => {
  const navigate = useNavigate();

  const [nombre, setNombre] = useState('');
  const [capacidad, setCapacidad] = useState(0);
  const [descripcion, setDescripcion] = useState('');
  const [imagen, setImagen] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);

  const [mensajeExito, setMensajeExito] = useState('');
  const [mensajeError, setMensajeError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('action', 'registrar');
    formData.append('nombre', nombre);
    formData.append('capacidad', capacidad.toString());
    formData.append('descripcion', descripcion);

    if (imagen) {
      formData.append('imagen', imagen);
    }

    try {
      const res = await fetch('http://localhost:8080/club/salon', {
        method: 'POST',
        body: formData,
      });

      if (res.ok) {
        setMensajeExito('✅ Salón creado correctamente');
        setTimeout(() => navigate('/inicio-admin'), 2000);
      } else {
        setMensajeError('❌ Error al crear el salón');
        setTimeout(() => setMensajeError(''), 3000);
      }
    } catch (err) {
      console.error(err);
      setMensajeError('🚫 Error de conexión con el servidor');
      setTimeout(() => setMensajeError(''), 3000);
    }
  };

  return (
    <div className="pag-crear-salon">
      <NavbarAdmin />

      <div className="contenido-crear-salon">
        <div className="form-crear-salon">
		<h1>Crear Nuevo Salón</h1>

		<div className="mensajes-globales">
		  {mensajeExito && <p className="mensaje-exito">{mensajeExito}</p>}
		  {mensajeError && <p className="mensaje-error">{mensajeError}</p>}
		</div>

	

          <form onSubmit={handleSubmit} className="crear-salon">
            <label>Nombre del Salón:</label>
            <input
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              required
            />

            <label>Capacidad:</label>
            <input
              type="number"
              value={capacidad}
              onChange={(e) => setCapacidad(Number(e.target.value))}
              required
            />

            <label>Descripción:</label>
            <input
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              required
            />

            <label>Imagen (opcional):</label>
            <input
              type="file"
              accept="image/*"
              onChange={(e) => {
                const file = e.target.files ? e.target.files[0] : null;
                setImagen(file);

                if (file) {
                  const url = URL.createObjectURL(file);
                  setPreview(url);
                } else {
                  setPreview(null);
                }
              }}
            />

            {/* Vista previa */}
            {preview && (
              <div className="preview-container">
                <p>Vista previa de la imagen:</p>
                <img src={preview} alt="Vista previa" className="preview-img" />
              </div>
            )}

            <div className="botones-container-salon">
              <button type="submit">Crear Salón</button>

              <button
                type="button"
                onClick={() => navigate('/salones-admin')}
                className="btn-cancel-salon"
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

export default CrearSalon;
