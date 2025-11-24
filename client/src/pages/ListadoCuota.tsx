import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';
import '../styles/CuotasUsuario.css'; 

interface Cuota {
  id: number;
  nro_cuota: number;
  fecha_vencimiento: string;
  fecha_cuota: string;
}

interface MontoCuota {
  id_cuota: number;
  monto: number;
}

const ListadoCuotas: React.FC = () => {
  const navigate = useNavigate();

  const [cuotas, setCuotas] = useState<Cuota[]>([]);
  const [montos, setMontos] = useState<MontoCuota[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const [resCuotas, resMontos] = await Promise.all([
        fetch('https://losandesback-production.up.railway.app/cuota?action=listar'),
        fetch('https://losandesback-production.up.railway.app/montocuota?action=listar')
      ]);

      if (!resCuotas.ok) throw new Error('Error al cargar el listado de cuotas');
      if (!resMontos.ok) throw new Error('Error al cargar el listado de montos');

      const dataCuotas = await resCuotas.json();
      const dataMontos = await resMontos.json();

      setCuotas(dataCuotas);
      setMontos(dataMontos);
    } catch (err: any) {
      console.error(err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getMonto = (idCuota: number) => {
    const encontrado = montos.find(m => m.id_cuota === idCuota);
    return encontrado ? encontrado.monto : 0;
  };


  if (loading) return <div className="cuotas-page-container"><p style={{color:'white', marginTop:50}}>Cargando listado...</p></div>;

  if (error) {
    return (
      <div className="cuotas-page-container">
        <NavbarAdmin />
        <div className="error-msg">
          <p>{error}</p>
          <button onClick={() => navigate('/')} className="btn-back">Volver al Inicio</button>
        </div>
      </div>
    );
  }

  return (
    <div className="cuotas-page-container">
      <NavbarAdmin />

      <div className="header-bar">
        <h2>Listado General de Cuotas</h2>
        <div className="user-info">
           
        </div>
      </div>

      <div className="table-container">
        {cuotas.length === 0 ? (
          <p style={{ color: 'white', textAlign: 'center' }}>No hay cuotas registradas en el sistema.</p>
        ) : (
          <table className="styled-table">
            <thead>
              <tr>
                <th>Concepto</th>
				<th>Fecha Emisión</th>
                <th>Fecha de Vencimiento</th>
                <th>Monto Base ($)</th>
              </tr>
            </thead>
            <tbody>
              {cuotas.map((c) => {
                const monto = getMonto(c.id);
                
                return (
                  <tr key={c.id}>
                    <td className="text-bold">Cuota Mensual {c.nro_cuota}</td>
					<td>{new Date(c.fecha_cuota).toLocaleDateString()}</td>
                    <td>{new Date(c.fecha_vencimiento).toLocaleDateString()}</td>
                    <td className="text-monto">
                      {monto > 0 
                        ? `$ ${monto.toLocaleString('es-AR', { minimumFractionDigits: 2 })}` 
                        : <span style={{color:'#999', fontSize:'0.9rem'}}>Sin monto asignado</span>
                      }
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>

      <div style={{ display: 'flex', gap: '20px' }}>
        <button onClick={() => navigate('/agregar-cuota')} className="btn-pay" style={{marginTop: '40px'}}>
             Crear Nueva Cuota
        </button>
        
        <button onClick={() => navigate('/inicio-admin')} className="btn-back">
            Volver al Inicio
        </button>
      </div>

    </div>
  );
};

export default ListadoCuotas;