import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Registro from './pages/Registro';
import InicioAdmin from './pages/InicioAdmin';
import AgregarProfesor from './pages/AgregarProfesor';
import InicioSocio from './pages/InicioSocio';
import MiCuenta from './pages/MiCuenta';
import ListadoProfesor from './pages/ListadoProfesor';
import ModificarProfesor from './pages/ModificarProfesor';
import ListaActividades from './pages/ListaActividades';
import AgregarActividad from './pages/AgregarActividad';
>>>>>>> 50e700d5ef72affd55ec2230cb38b56027877b75
import InicioSocio from './pages/InicioSocio';
import MiCuenta from './pages/MiCuenta';
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/registro" element={<Registro />} />
		<Route path="/inicio-admin" element={<InicioAdmin />} />
		<Route path="/agregar-profesor" element={<AgregarProfesor/>} />
		<Route path="/inicio-socio" element={<InicioSocio />} />
		<Route path="/modificar-usuario" element={<MiCuenta />} />
		<Route path="/modificar-profesor" element={<ModificarProfesor />} />
		<Route path="/listado-profesor" element={<ListadoProfesor />} />
		<Route path="/actividades" element={<ListaActividades />} />
		<Route path="/actividades/nueva" element={<AgregarActividad />} />
        <Route path="*" element={<
		<Route path="/inicio-socio" element={<InicioSocio />} />
		<Route path="/modificar-usuario" element={<MiCuenta />} />
		<Route path="*" element={<h2>404 - Página no encontrada</h2>} />
      </Routes>
    </Router>
  );
}

export default App;
