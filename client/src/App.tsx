import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Registro from './pages/Registro';
import CanchasAdmin from './pages/CanchasAdmin';
import InicioAdmin from './pages/InicioAdmin';
import AgregarProfesor from './pages/AgregarProfesor';
import CrearSalon from './pages/CrearSalon';
import SalonesAdmin from './pages/SalonesAdmin';
import ModificarSalon from './pages/ModificarSalon';
import Salones from './pages/Salones';
import AlquilarSalon from './pages/AlquilarSalon';
import MisAlquileresSalon from './pages/MisAlquileresSalon';
import VerAlquileresSalon from './pages/VerAlquileresSalon';
import ModificarCancha from './pages/ModificarCancha';
import CrearCancha from './pages/CrearCancha';
import InicioSocio from './pages/InicioSocio';
import MiCuenta from './pages/MiCuenta';
import Canchas from './pages/Canchas';
import ListadoProfesor from './pages/ListadoProfesor';
import ModificarProfesor from './pages/ModificarProfesor';
import ListaActividades from './pages/ListaActividades';
import AgregarActividad from './pages/AgregarActividad';
import MisActividades from './pages/MisActividades';
import InscripcionActividad from './pages/InscripcionActividad';
import ActividadDetalle from './pages/ActividadDetalle';
import AgregarHorario from './pages/AgregarHorario';
import AgregarPartido from './pages/AgregarPartido';
import AdminPartidos from './pages/AdminPartidos';
import PartidoDetalle from './pages/PartidoDetalle';
import ListadoSocio from './pages/ListadoSocio';
import ListadoCuota from './pages/ListadoCuota';
import CuotasUsuario from './pages/CuotasUsuario';
import Credencial from './pages/Credencial';
import MisCuotas from './pages/MisCuotas';
import Info from './pages/Info';
import AgregarCuota from './pages/AgregarCuota';
import CambioContrasenia from './pages/CambioContrasenia';


function App() {
  return (
    <Router>
      <Routes>
	  	<Route path="/info/:tipo" element={<Info />} />
        <Route path="/" element={<Navigate to="/login" />} />
		<Route path="/canchas" element={<Canchas />} />
        <Route path="/login" element={<Login />} />
		<Route path="/canchas-admin" element={<CanchasAdmin />} />
        <Route path="/registro" element={<Registro />} />
		<Route path="/inicio-admin" element={<InicioAdmin />} />
		<Route path="/agregar-profesor" element={<AgregarProfesor/>} />
		<Route path="/canchas-admin/modificar" element={<ModificarCancha />} />
		<Route path="/crear-salon" element={<CrearSalon />} />
		<Route path="/salones-admin" element={<SalonesAdmin />} />
		<Route path="/modificar-salon" element={<ModificarSalon />} />
		<Route path="/salones" element={<Salones />} />
		<Route path="/alquiler_salon/:idSalon" element={<AlquilarSalon />} />
		<Route path="/mis-alquileres-salon" element={<MisAlquileresSalon />} />
		<Route path="/ver-alquileres-salon" element={<VerAlquileresSalon />} />
		<Route path="/crear" element={<CrearCancha />} />
		<Route path="/inicio-socio" element={<InicioSocio />} />
		<Route path="/modificar-usuario" element={<MiCuenta />} />
		<Route path="/modificar-profesor" element={<ModificarProfesor />} />
		<Route path="/listado-profesor" element={<ListadoProfesor />} />
		<Route path="/actividades/nueva" element={<AgregarActividad />} />
		<Route path="/modificar-usuario" element={<MiCuenta />} />
		<Route path="/mis-actividades" element={<MisActividades />} />
		<Route path="/inscripcion-actividad" element={<InscripcionActividad />} />
		<Route path="/actividades" element={<ListaActividades />} />
		<Route path="/actividad-detalle" element={<ActividadDetalle />} />
		<Route path="/agregar-horario" element={<AgregarHorario />} />
		<Route path="/agregar-partido" element={<AgregarPartido />} />
		<Route path="/admin-partidos" element={<AdminPartidos />} />
		<Route path="/partido-detalle" element={<PartidoDetalle />} />
		<Route path="/agregar-cuota" element={<AgregarCuota />} />
		<Route path="/listado-socio" element={<ListadoSocio />} />
		<Route path="/mis-cuotas" element={<MisCuotas />} />
		<Route path="/listado-cuota" element={<ListadoCuota />} />
		<Route path="/credencial/:id" element={<Credencial />} />
		<Route path="/cuotas-usuario" element={<CuotasUsuario />} />
		<Route path="/cambiar-contrasenia" element={<CambioContrasenia />} />
		<Route path="*" element={<h2>404 - Página no encontrada</h2>} />
      </Routes>
    </Router>
  );
}

export default App;
