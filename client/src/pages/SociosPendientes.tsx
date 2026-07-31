import React, { useEffect, useState } from "react";
import NavbarAdmin from "./NavbarAdmin";
import { API_URL } from "../config";
import "../styles/SociosPendientes.css";

interface Usuario{
    id:number;
    nombreCompleto:string;
    dni:string;
    mail:string;
    telefono:string;
}

const SociosPendientes = () =>{

    const [socios,setSocios]=useState<Usuario[]>([]);

 
		const cargarSocios=()=>{

		    fetch(API_URL + "/usuario?action=pendientes")
		    .then(r=>r.json())
		    .then(data=>{

		        const sociosAdaptados = data.map((u:any)=>({
		            ...u,
		            nombreCompleto: u.nombre_completo
		        }));

		        setSocios(sociosAdaptados);

		    });

    }

    useEffect(()=>{
        cargarSocios();
    },[]);

    const aprobar=(id:number)=>{

        const params=new URLSearchParams();
        params.append("action","aprobar");
        params.append("id",id.toString());

        fetch(API_URL+"/usuario",{
            method:"POST",
            headers:{
                "Content-Type":"application/x-www-form-urlencoded"
            },
            body:params.toString()
        })
        .then(()=>{
            cargarSocios();
        });

    }

	return(

	    <div className="socios-pendientes-page">

	        <NavbarAdmin/>

	        <div className="socios-pendientes-content">

	            <div className="socios-pendientes-container">

	                <h2>Socios pendientes de aprobación</h2>

	                <table className="socios-table">

                <thead>

                    <tr>

                        <th>Nombre</th>
                        <th>DNI</th>
                        <th>Mail</th>
                        <th>Teléfono</th>
                        <th></th>

                    </tr>

                </thead>

                <tbody>

                    {socios.map(s=>(
                        <tr key={s.id}>

                            <td>{s.nombreCompleto}</td>
                            <td>{s.dni}</td>
                            <td>{s.mail}</td>
                            <td>{s.telefono}</td>

							<td className="acciones-socio">

							    <button
							        className="btn-aprobar"
							        onClick={()=>aprobar(s.id)}
							    >
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
export default SociosPendientes;