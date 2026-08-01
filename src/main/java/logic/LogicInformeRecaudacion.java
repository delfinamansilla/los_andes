package logic;

import data.DataUsuario;
import data.DataCuota;
import data.DataPagoCuota;
import data.DataMonto_cuota;
import entities.InformeRecaudacion;
import entities.Usuario;
import entities.Cuota;
import entities.PagoCuota;
import entities.Monto_cuota;

import java.time.LocalDate;
import java.util.LinkedList;


public class LogicInformeRecaudacion {

    private DataUsuario du;
    private DataCuota dc;
    private DataPagoCuota dpc;
    private DataMonto_cuota dmc;


    public LogicInformeRecaudacion() {
        du = new DataUsuario();
        dc = new DataCuota();
        dpc = new DataPagoCuota();
        dmc = new DataMonto_cuota();
    }


    public LinkedList<InformeRecaudacion> generarInforme(int mes, int anio) {

        LinkedList<InformeRecaudacion> informe = new LinkedList<>();

        LinkedList<PagoCuota> pagos = dpc.getAll();

        for (PagoCuota pago : pagos) {

            LocalDate fecha = pago.getFecha_pago();

            if (fecha.getMonthValue() == mes && fecha.getYear() == anio) {

                Usuario usuario = du.getById(pago.getId_usuario());

                Cuota cuota = dc.getById(pago.getId_cuota());

                LinkedList<Monto_cuota> montos = dmc.getMontosPorCuota(pago.getId_cuota());

                Monto_cuota monto = null;

                if (!montos.isEmpty()) {
                    monto = montos.get(0);
                }


                InformeRecaudacion ir = new InformeRecaudacion();

                if (usuario != null) {
                    ir.setNombreUsuario(usuario.getNombreCompleto());
                }

                if (cuota != null) {
                    ir.setCuota(cuota.getNro_cuota());
                }

                if (monto != null) {
                    ir.setMonto(monto.getMonto());
                }

                ir.setFechaPago(
                	    pago.getFecha_pago() != null
                	        ? pago.getFecha_pago().toString()
                	        : ""
                	);
                ir.setNroTransaccion(pago.getNro_transaccion());

                informe.add(ir);
            }
        }

        return informe;
    }


    public double calcularTotal(LinkedList<InformeRecaudacion> informe) {

        double total = 0;

        for (InformeRecaudacion i : informe) {
            total += i.getMonto();
        }

        return total;
    }
}