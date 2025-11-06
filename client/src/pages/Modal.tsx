import React from "react";
import '../styles/Modal.css'

interface ModalProps {
  titulo?: string; 
  mensaje?: string;
  textoConfirmar?: string;
  textoCancelar?: string;
  onConfirmar: () => void; 
  onCancelar: () => void;  
}

function Modal({
  titulo = "Confirmar acción",
  mensaje = "¿Estás seguro?",
  textoConfirmar = "Confirmar",
  textoCancelar = "Cancelar",
  onConfirmar,
  onCancelar,
}: ModalProps) { 
  return (
    <div className="modal-backdrop">
      <div className="modal">
        <h3>{titulo}</h3>
        <p>{mensaje}</p>
        <div className="modal-buttons">
          <button className="btn-confirm" onClick={onConfirmar}>
            {textoConfirmar}
          </button>
          <button className="btn-cancel" onClick={onCancelar}>
            {textoCancelar}
          </button>
        </div>
      </div>
    </div>
  );
}

export default Modal;