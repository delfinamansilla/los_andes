import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Registro from './pages/Registro';
import InicioAdmin from './pages/InicioAdmin';
<<<<<<< HEAD
import AgregarProfesor from './pages/AgregarProfesor';
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
<<<<<<< HEAD
		<Route path="/agregar-profesor" element={<AgregarProfesor/>} />
=======
		<Route path="/inicio-socio" element={<InicioSocio />} />
		<Route path="/modificar-usuario" element={<MiCuenta />} />
>>>>>>> 95ffe351dd49db298ffd820f3b78e432ddba63ed
        <Route path="*" element={<h2>404 - Página no encontrada</h2>} />
      </Routes>
    </Router>
  );
}

export default App;
