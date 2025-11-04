import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Registro from './pages/Registro';
import CanchasAdmin from './pages/CanchasAdmin';
import InicioAdmin from './pages/InicioAdmin';
import AgregarProfesor from './pages/AgregarProfesor';
import ModificarCancha from './pages/ModificarCancha';
import CrearCancha from './pages/CrearCancha';
import InicioSocio from './pages/InicioSocio';
import MiCuenta from './pages/MiCuenta';
import Canchas from './pages/Canchas';
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
		<Route path="/canchas" element={<Canchas />} />
        <Route path="/login" element={<Login />} />
		<Route path="/canchas-admin" element={<CanchasAdmin />} />
        <Route path="/registro" element={<Registro />} />
		<Route path="/inicio-admin" element={<InicioAdmin />} />
		<Route path="/agregar-profesor" element={<AgregarProfesor/>} />
		<Route path="/canchas-admin/modificar" element={<ModificarCancha />} />
		<Route path="/crear" element={<CrearCancha />} />
		<Route path="/inicio-socio" element={<InicioSocio />} />
		<Route path="/modificar-usuario" element={<MiCuenta />} />
        <Route path="*" element={<h2>404 - Página no encontrada</h2>} />
      </Routes>
    </Router>
  );
}

export default App;
