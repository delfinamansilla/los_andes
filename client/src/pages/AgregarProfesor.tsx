import React, { useState,useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const AgregarProfesor: React.FC = () => {
  const [nombreCompleto, setNombreCompleto] = useState('');
  const [telefono, setTelefono] = useState('');
  const [mail, setMail] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    // Validación simple de campos
    if (!nombreCompleto || !telefono || !mail) {
      setError('❌ Todos los campos son obligatorios.');
      return;
    }
	console.log(" PASO 2: La validación de campos pasó correctamente.");


    try {
      const params = new URLSearchParams();
      params.append('action', 'agregar');
      params.append('nombre_completo', nombreCompleto);
      params.append('telefono', telefono);
      params.append('mail', mail);
	  
	  const url = 'http://localhost:8080/profesor';
	  console.log(` PASO 3: Preparando para enviar petición POST a: ${url}`);
	  console.log("          Con estos datos:", params.toString());

      // La URL del endpoint para profesores
      const res = await fetch('http://localhost:8080/club/profesor', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString(),
      });
	  
	  console.log(" PASO 4: Recibida respuesta del servidor.");
	     console.log("          Status de la respuesta:", res.status, res.statusText);
	     console.log("          Respuesta OK?:", res.ok);

	     // Clonamos la respuesta para poder leerla dos veces (una como texto, otra como json)
	     const responseClone = res.clone();
	     
	     // Intentamos leer la respuesta como texto para verla sin importar qué sea
	     const rawText = await responseClone.text();
	     console.log("          RESPUESTA CRUDA (TEXTO):", rawText);

      const data = await res.json();

      if (data && data.status === 'ok') {
        setSuccess('✅ ¡Profesor agregado exitosamente!');
        
        // Limpiar formulario
        setNombreCompleto('');
        setTelefono('');
        setMail('');

        // Redirigir a la lista de profesores después de 2 segundos
        setTimeout(() => {
          navigate('/profesores');
        }, 2000);
      } else {
        setError(data.message || '⚠ Error inesperado al agregar el profesor.');
      }
    } catch (err) {
      console.error(err);
      setError('🚫 Error al conectar con el servidor.');
    }
  };

  return (
    <div className="agregar-profesor-page">
      <div className="form-container">
        <h2>Agregar Nuevo Profesor</h2>
        <p>Complete los datos para registrar un nuevo profesor en el sistema.</p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="nombreCompleto">Nombre Completo</label>
            <input
              id="nombreCompleto"
              type="text"
              className="inp"
              placeholder="Ej: Juan Pérez"
              value={nombreCompleto}
              onChange={(e) => setNombreCompleto(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="telefono">Teléfono</label>
            <input
              id="telefono"
              type="tel"
              className="inp"
              placeholder="Ej: 1122334455"
              value={telefono}
              onChange={(e) => setTelefono(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="mail">Email</label>
            <input
              id="mail"
              type="email"
              className="inp"
              placeholder="Ej: juan.perez@example.com"
              value={mail}
              onChange={(e) => setMail(e.target.value)}
            />
          </div>

          <button type="submit" className="btn_agregar">Agregar Profesor</button>
        </form>

        {/* Mostrar mensajes de estado */}
        {error && <p className="error-box">{error}</p>}
        {success && <p className="success-box">{success}</p>}
      </div>
    </div>
  );
};

export default AgregarProfesor;