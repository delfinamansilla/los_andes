import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import NavbarAdmin from './NavbarAdmin';
import NavbarSocio from './NavbarSocio';
import Footer from './Footer';
import '../styles/MiCuenta.css';
import { API_URL } from "../config";


const MiCuenta: React.FC = () => {
  const navigate = useNavigate();
  const [usuario, setUsuario] = useState<any>(null);

  const [nombreCompleto, setNombreCompleto] = useState('');
  const [dni, setDni] = useState('');
  const [telefono, setTelefono] = useState('');
  const [mail, setMail] = useState('');
  const [fechaNacimiento, setFechaNacimiento] = useState('');

  const [mostrarFormulario, setMostrarFormulario] = useState(false);
  const [contraseñaActual, setContraseñaActual] = useState('');
  const [nuevaContraseña, setNuevaContraseña] = useState('');
  const [confirmarNuevaContraseña, setConfirmarNuevaContraseña] = useState('');

  const [mensajeExito, setMensajeExito] = useState('');
  const [mensajeError, setMensajeError] = useState('');

  useEffect(() => {
    const usr = JSON.parse(localStorage.getItem('usuario') || '{}');
    if (!usr || !usr.id) {
      navigate('/login');
    } else {
      setUsuario(usr);
      setNombreCompleto(usr.nombre_completo || '');
      setDni(usr.dni || '');
      setTelefono(usr.telefono || '');
      setMail(usr.mail || '');
      setFechaNacimiento(usr.fecha_nacimiento || '');
    }
  }, [navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!usuario) return;

    const params = new URLSearchParams();
    params.append('action', 'actualizar');
    params.append('id', usuario.id);
    params.append('nombre_completo', nombreCompleto);
    params.append('dni', dni);
    params.append('telefono', telefono);
    params.append('mail', mail);
    params.append('contrasenia', usuario.contrasenia); 
    params.append('rol', usuario.rol);
    params.append('estado', 'true');
    if (fechaNacimiento) params.append('fecha_nacimiento', fechaNacimiento);

    try {
      const res = await fetch(`${API_URL}/usuario`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString(),
      });

      if (res.ok) {
        setMensajeExito('Datos actualizados correctamente');
        const updatedUser = { ...usuario, nombre_completo: nombreCompleto, dni, telefono, mail, fecha_nacimiento: fechaNacimiento };
        localStorage.setItem('usuario', JSON.stringify(updatedUser));
        setUsuario(updatedUser);
        setTimeout(() => setMensajeExito(''), 3000);
      } else {
        const errorData = await res.json();
        setMensajeError(errorData.error || 'Error al actualizar');
        setTimeout(() => setMensajeError(''), 3000);
      }
    } catch (err) {
      console.error(err);
      setMensajeError('🚫 Error al conectar con el servidor');
      setTimeout(() => setMensajeError(''), 3000);
    }
  };

  const handleCambiarContrasenia = async (e: React.FormEvent) => {
    e.preventDefault();
    if (nuevaContraseña !== confirmarNuevaContraseña) {
      setMensajeError('Las contraseñas no coinciden');
      setTimeout(() => setMensajeError(''), 3000);
      return;
    }

    try {
      const params = new URLSearchParams();
      params.append('action', 'actualizar');
      params.append('id', usuario.id);
      params.append('nombre_completo', nombreCompleto);
      params.append('dni', dni);
      params.append('telefono', telefono);
      params.append('mail', mail);
      params.append('contrasenia', nuevaContraseña);
      params.append('rol', usuario.rol);
      params.append('estado', 'true');
      if (fechaNacimiento) params.append('fecha_nacimiento', fechaNacimiento);

      const res = await fetch(`${API_URL}/usuario`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString(),
      });

      if (res.ok) {
        setMensajeExito('Contraseña cambiada correctamente');
        const updatedUser = { ...usuario, contrasenia: nuevaContraseña };
        localStorage.setItem('usuario', JSON.stringify(updatedUser));
        setUsuario(updatedUser);
        setMostrarFormulario(false);
        setTimeout(() => setMensajeExito(''), 3000);
      } else {
        const errorData = await res.json();
        setMensajeError(errorData.error || 'Error al actualizar la contraseña');
        setTimeout(() => setMensajeError(''), 3000);
      }
    } catch (err) {
      console.error(err);
      setMensajeError('Error al conectar con el servidor');
      setTimeout(() => setMensajeError(''), 3000);
    }
  };

  if (!usuario) return <p>Cargando datos del usuario...</p>;

  const Navbar = usuario.rol === 'administrador' ? NavbarAdmin : NavbarSocio;

  return (
    <div className="pag-modificar">
      <Navbar />
      <div className="contenido-centrado">
        <div className="form-modificar">
          <h1>Datos de la cuenta</h1>
          {mensajeExito && <p className="mensaje-exito">{mensajeExito}</p>}
          {mensajeError && <p className="mensaje-error">{mensajeError}</p>}
          {!mostrarFormulario ? (
            <form onSubmit={handleSubmit} className="modificar">
              <label>Nombre Completo:</label>
              <input value={nombreCompleto} onChange={(e) => setNombreCompleto(e.target.value)} />
              <label>DNI:</label>
              <input value={dni} onChange={(e) => setDni(e.target.value)} />
              <label>Teléfono:</label>
              <input value={telefono} onChange={(e) => setTelefono(e.target.value)} />
              <label>Email:</label>
              <input type="email" value={mail} onChange={(e) => setMail(e.target.value)} />
              <label>Fecha de Nacimiento:</label>
              <input type="date" value={fechaNacimiento} onChange={(e) => setFechaNacimiento(e.target.value)} />
              <div className="botones-container">
                <button type="submit">Modificar datos</button>
                <button type="button" onClick={() => setMostrarFormulario(true)} className="btn-cambiar-contrasena">
                  Cambiar contraseña
                </button>
				{usuario.rol !== 'administrador' && (
			      <button 
			        type="button" 
			        onClick={() => navigate(`/credencial/${usuario.id}`)}
			        className="btn-ver-credencial">
			        Ver Credencial
			      </button>
			    )}
              </div>
            </form>
          ) : (
            <form onSubmit={handleCambiarContrasenia} className="modificar">
              <label>Contraseña actual:</label>
              <input type="text" value={contraseñaActual} onChange={(e) => setContraseñaActual(e.target.value)} />
              <label>Nueva contraseña:</label>
              <input type="text" value={nuevaContraseña} onChange={(e) => setNuevaContraseña(e.target.value)} />
              <label>Confirmar nueva contraseña:</label>
              <input type="text" value={confirmarNuevaContraseña} onChange={(e) => setConfirmarNuevaContraseña(e.target.value)} />
              <div className="botones-container">
                <button type="submit">Guardar nueva contraseña</button>
                <button type="button" onClick={() => setMostrarFormulario(false)} className="btn-cambiar-contrasena">
                  Cancelar
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
	  <Footer />
    </div>
  );
};

export default MiCuenta;

