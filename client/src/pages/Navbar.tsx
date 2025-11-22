import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Navbar.css';
import logoClub from '../assets/los_andes.png';

const Navbar: React.FC = () => {
  const navigate = useNavigate();

  return (
    <nav className="navbar">
      <div className="navbar-logo" onClick={() => navigate('/login')}>
        <img src={logoClub} alt="logo" />
      </div>
    </nav>
  );
};

export default Navbar;