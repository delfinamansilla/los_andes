import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';
import '../styles/CuotasUsuario.css'; 

interface AlquilerCancha {
  id: number;
  fecha_alquiler: string;
  hora_desde: string;
  hora_hasta: string;
  id_cancha: number;
  id_usuario: number;
}

interface Socio {
  id: number;
  nombre_completo: string;
  dni: string;
  mail: string;
}

const AlquileresCanchasUsuario: React.FC = () => {
  const navigate = useNavigate();
  const [alquileres, setAlquileres] = useState<AlquilerCancha[]>([]);
  const [socio, setSocio] = useState<Socio | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    const socioStored = localStorage.getItem('usuarioSeleccionadoParaAlquileres');
    
    if (!socioStored) {
      setError('No se ha seleccionado un usuario. Vuelva al listado.');
      setLoading(false);
      return;
    }

    const socioParsed: Socio = JSON.parse(socioStored);
    setSocio(socioParsed);

    try {
      const res = await fetch(`https://losandesback-production.up.railway.app/alquiler_cancha?action=listar_por_usuario&id_usuario=${socioParsed.id}`);
      
      if (!res.ok) throw new Error('Error al conectar con el servidor.');
      
      const data = await res.json();
      
      const dataOrdenada = data.sort((a: AlquilerCancha, b: AlquilerCancha) => 
        new Date(b.fecha_alquiler).getTime() - new Date(a.fecha_alquiler).getTime()
      );

      setAlquileres(dataOrdenada);

    } catch (err: any) {
      console.error(err);
      setError('No se pudieron cargar los alquileres.');
    } finally {
      setLoading(false);
    }
  };

  const handleVolver = () => {
    localStorage.removeItem('usuarioSeleccionadoParaAlquileres');
    navigate('/listado-socio');
  };

  const formatHora = (hora?: string) => {
    if (!hora) return ""; 
    return hora.substring(0, 5); 
  };

  if (loading) return (
    <div className="cuotas-page-container">
       <NavbarAdmin />
       <p style={{color:'white', marginTop:50}}>Cargando historial...</p>
    </div>
  );
  
  if (error) {
      return (
        <div className="cuotas-page-container">
            <NavbarAdmin />
            <div className="error-msg" style={{color: '#ff6b6b', background: 'rgba(0,0,0,0.8)', padding: 20, borderRadius: 10, marginTop: 50}}>
                <p>{error}</p>
                <button onClick={handleVolver} className="btn-back">Volver</button>
            </div>
        </div>
      );
  }

  return (
    <div className="cuotas-page-container">
      <NavbarAdmin />
      
      <div className="header-bar">
        <h2>Historial de Canchas</h2>
        {socio && (
           <div className="user-info">
              <span>{socio.nombre_completo}</span>
              <span style={{margin: '0 10px', color: '#466245'}}>|</span>
              <span>DNI: {socio.dni}</span>
           </div>
        )}
      </div>

      <div className="table-container">
        {alquileres.length === 0 ? (
            <p style={{color: 'white', textAlign: 'center', marginTop: 20}}>Este socio no ha alquilado canchas aún.</p>
        ) : (
            <table className="styled-table">
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Horario</th>
                        <th>Cancha</th>
                        <th>Estado</th>
                    </tr>
                </thead>

                <tbody>
                    {alquileres.map((alquiler) => {
                        const fechaFin = new Date(alquiler.fecha_alquiler + 'T' + alquiler.hora_hasta);
                        const hoy = new Date();
                        const esPasado = fechaFin < hoy;

                        return (
                            <tr key={alquiler.id}>
                                <td className="text-bold">
                                    <span style={{marginRight: 8}}></span>
                                    {new Date(alquiler.fecha_alquiler + 'T00:00:00').toLocaleDateString()}
                                </td>
                                
                                <td className="text-monto">
                                    {formatHora(alquiler.hora_desde)} - {formatHora(alquiler.hora_hasta)} hs
                                </td>
                                
                                <td>
                                    <span style={{fontWeight: 'bold', color: '#20321E'}}>
                                        Cancha N° {alquiler.id_cancha}
                                    </span>
                                </td>
                                
                                <td>
                                    {esPasado ? (
                                        <span className="status-badge" style={{backgroundColor: '#e2e3e5', color: '#383d41', border: '1px solid #d6d8db'}}>
                                            Finalizado
                                        </span>
                                    ) : (
                                        <span className="status-badge status-pagado">
                                            Confirmado
                                        </span>
                                    )}
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        )}
      </div>

      <button onClick={handleVolver} className="btn-back">
          ⬅ Volver al Listado
      </button>

    </div>
  );
};

export default AlquileresCanchasUsuario;