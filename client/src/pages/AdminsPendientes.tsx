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
	const [paginaActual, setPaginaActual] = useState(1);
	const PAGE_SIZE = 10;

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
	
	const totalPaginas = Math.ceil(
	        admins.length / PAGE_SIZE
	    );

    const indiceInicio = (paginaActual - 1) * PAGE_SIZE;

    const indiceFin = indiceInicio + PAGE_SIZE;

    const adminsVisibles = admins.slice(
        indiceInicio,
        indiceFin
    );

    const paginaAnterior = () => {
        if (paginaActual > 1) {
            setPaginaActual(paginaActual - 1);
        }
    };

    const paginaSiguiente = () => {
        if (paginaActual < totalPaginas) {
            setPaginaActual(paginaActual + 1);
        }
    };

    const irAPagina = (pagina: number) => {
        setPaginaActual(pagina);
    };

	return (
	        <div className="socios-pendientes-page">
	            <NavbarAdmin />
	            <div className="socios-pendientes-content">
	                <div className="socios-pendientes-container">
	                    <h2>Administradores pendientes de aprobación</h2>
	                    {admins.length === 0 ? (
	                        <p>No hay administradores pendientes de aprobación.</p>
	                    ) : (
	                        <>
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
	                                    {adminsVisibles.map(a => (
	                                        <tr key={a.id}>
	                                            <td>{a.nombreCompleto}</td>
	                                            <td>{a.dni}</td>
	                                            <td>{a.mail}</td>
	                                            <td>{a.telefono}</td>
	                                            <td className="acciones-socio">
	                                                <button
	                                                    className="btn-aprobar"
	                                                    onClick={() => aprobar(a.id)}
	                                                >
	                                                    Aprobar
	                                                </button>
	                                            </td>
	                                        </tr>
	                                    ))}
	                                </tbody>
	                            </table>
	                            {totalPaginas > 1 && (
	                                <div className="paginacion">
	                                    <button
	                                        className="btn-paginacion"
	                                        onClick={paginaAnterior}
	                                        disabled={paginaActual === 1}
	                                    >
	                                        ‹
	                                    </button>
	                                    {Array.from(
	                                        { length: totalPaginas },
	                                        (_, index) => index + 1
	                                    ).map(pagina => (
	                                        <button
	                                            key={pagina}
	                                            className={
	                                                paginaActual === pagina
	                                                    ? "btn-paginacion activo"
	                                                    : "btn-paginacion"
	                                            }
	                                            onClick={() => irAPagina(pagina)}
	                                        >
	                                            {pagina}
	                                        </button>
	                                    ))}
	                                    <button
	                                        className="btn-paginacion"
	                                        onClick={paginaSiguiente}
	                                        disabled={paginaActual === totalPaginas}
	                                    >
	                                        ›
	                                    </button>
	                                </div>
	                            )}
	                        </>
	                    )}
	                </div>
	            </div>
	        </div>
	    );
	};

export default AdminsPendientes;