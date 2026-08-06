import React, { useEffect, useState } from "react";
import NavbarAdmin from "./NavbarAdmin";
import { API_URL } from "../config";
import "../styles/SociosPendientes.css";

interface Usuario {
    id: number;
    nombreCompleto: string;
    dni: string;
    mail: string;
    telefono: string;
}

const AdminsPendientes = () => {
    const [admins, setAdmins] = useState<Usuario[]>([]);

    const cargarAdmins = () => {
        fetch(API_URL + "/usuario?action=admins_pendientes") 
            .then(r => r.json())
            .then(data => {
                const adaptados = data.map((u: any) => ({
                    ...u,
                    nombreCompleto: u.nombre_completo
                }));
                setAdmins(adaptados);
            });
    }

    useEffect(() => { cargarAdmins(); }, []);

    const aprobar = (id: number) => {
        const params = new URLSearchParams();
        params.append("action", "aprobar");
        params.append("id", id.toString());

        fetch(API_URL + "/usuario", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params.toString()
        }).then(() => cargarAdmins());
    }

    return (
        <div className="socios-pendientes-page">
            <NavbarAdmin />
            <div className="socios-pendientes-content">
                <div className="socios-pendientes-container">
                    <h2>Administradores pendientes de aprobación</h2>
                    <table className="socios-table">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>DNI</th>
                                <th>Mail</th>
                                <th>Teléfono</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {admins.map(a => (
                                <tr key={a.id}>
                                    <td>{a.nombreCompleto}</td>
                                    <td>{a.dni}</td>
                                    <td>{a.mail}</td>
                                    <td>{a.telefono}</td>
                                    <td className="acciones-socio">
                                        <button className="btn-aprobar" onClick={() => aprobar(a.id)}>
                                            Aprobar
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default AdminsPendientes;