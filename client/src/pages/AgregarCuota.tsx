import React, { useState } from 'react';
import NavbarAdmin from './NavbarAdmin'; 
import '../styles/AgregarCuota.css'; 

const AgregarCuota: React.FC = () => {
  const [fechaVencimiento, setFechaVencimiento] = useState('');
  const [monto, setMonto] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!fechaVencimiento || !monto) {
      setError('❌ La fecha de vencimiento y el monto son obligatorios.');
      return;
    }

    try {
      // 1. Calculamos el número de mes actual (1 = Enero, etc.)
      const nroCuotaActual = new Date().getMonth() + 1;

      // --- PASO 1: CREAR LA CUOTA ---
      const paramsCuota = new URLSearchParams();
      paramsCuota.append('action', 'crear');
      paramsCuota.append('nro_cuota', nroCuotaActual.toString());
      paramsCuota.append('fecha_vencimiento', fechaVencimiento);
      // No enviamos id_usuario, el Servlet lo manejará internamente

      const resCuota = await fetch('http://localhost:8080/club/cuota', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: paramsCuota.toString(),
      });

      if (!resCuota.ok) {
        const errData = await resCuota.json();
        throw new Error(errData.error || 'Error al crear la cuota');
      }

      // Obtenemos la respuesta JSON que DEBE incluir el ID generado
      const cuotaCreada = await resCuota.json();
      const idCuotaGenerado = cuotaCreada.id;

      if (!idCuotaGenerado) {
        throw new Error('El servidor creó la cuota pero no devolvió el ID.');
      }

      // --- PASO 2: CREAR EL MONTO ASOCIADO ---
      const paramsMonto = new URLSearchParams();
      paramsMonto.append('action', 'crear');
      paramsMonto.append('monto', monto);
      paramsMonto.append('id_cuota', idCuotaGenerado.toString());
      
      const resMonto = await fetch('http://localhost:8080/club/montocuota', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: paramsMonto.toString(),
      });

      const dataMonto = await resMonto.json();

      if (resMonto.ok) {
        setSuccess(`✅ ¡Listo! Cuota del mes ${nroCuotaActual} creada con monto $${monto}.`);
        // Limpiar campos
        setFechaVencimiento('');
        setMonto('');
      } else {
        setError(`⚠️ Cuota creada (ID: ${idCuotaGenerado}), pero error en el monto: ${dataMonto.error}`);
      }

    } catch (err: any) {
      console.error(err);
      setError(err.message || '🚫 Error de conexión.');
    }
  };

  return (
    <div className="agregar-cuota-page">
      <NavbarAdmin />
      <div className="content-area">
        <div className="form-container">
          
          <h2>Generar Cuota Mensual</h2>
          <p>Defina el vencimiento y el valor para la cuota del mes actual.</p>

          <form onSubmit={handleSubmit}>
            
            <div className="form-group">
              <label htmlFor="fechaVencimiento">Fecha de Vencimiento</label>
              <input
                id="fechaVencimiento"
                type="date"
                className="inp"
                value={fechaVencimiento}
                onChange={(e) => setFechaVencimiento(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="monto">Monto ($)</label>
              <input
                id="monto"
                type="number"
                step="0.01"
                className="inp"
                placeholder="Ej: 15000"
                value={monto}
                onChange={(e) => setMonto(e.target.value)}
              />
            </div>

            <button type="submit" className="btn_agregar">
              Guardar Cuota y Monto
            </button>
          </form>

          {error && <p className="error-box">{error}</p>}
          {success && <p className="success-box">{success}</p>}
        </div>
      </div>
    </div>
  );
};

export default AgregarCuota;