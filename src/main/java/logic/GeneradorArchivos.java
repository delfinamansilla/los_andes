package logic;

import com.lowagie.text.Document;
import entities.InformeRecaudacion;
import java.util.LinkedList;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import entities.Cancha;
import entities.Cuota;
import entities.Salon;
import entities.Usuario;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import entities.InformeRecaudacion;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.lowagie.text.Image;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GeneradorArchivos {
    private static final Color COLOR_PRINCIPAL = new Color(32, 50, 30);
    private static final Color COLOR_FONDO = new Color(221, 216, 202); 
    private static final Color COLOR_TEXTO = new Color(60, 60, 60);

    public byte[] generarConstanciaPDF(Salon salon, Usuario usuario, LocalDate fecha, LocalTime desde, LocalTime hasta) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);
        
        doc.open();
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        
        PdfPCell cellHeader = new PdfPCell();
        cellHeader.setBackgroundColor(COLOR_PRINCIPAL);
        cellHeader.setPadding(20);
        cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellHeader.setBorder(Rectangle.NO_BORDER);

        Font fontTitulo = new Font(Font.HELVETICA, 24, Font.BOLD, COLOR_FONDO);
        Font fontSubtitulo = new Font(Font.HELVETICA, 14, Font.NORMAL, COLOR_FONDO);
        
        cellHeader.addElement(new Paragraph("Club Deportivo Los Andes", fontTitulo));
        cellHeader.addElement(new Paragraph("Comprobante de Reserva", fontSubtitulo));
        
        headerTable.addCell(cellHeader);
        doc.add(headerTable);
        
        doc.add(new Paragraph("\n\n")); 
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2}); 
        table.setSpacingBefore(10);

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        agregarFila(table, "SOCIO", usuario.getNombreCompleto());
        agregarFila(table, "DNI", usuario.getDni());
        agregarFila(table, "EMAIL", usuario.getMail());
        agregarFila(table, " ", " "); // Espaciador
        agregarFila(table, "SALÓN", salon.getNombre());
        agregarFila(table, "FECHA", fecha.format(dateFmt));
        agregarFila(table, "HORARIO", desde.format(timeFmt) + " a " + hasta.format(timeFmt) + " hs");

        doc.add(table);

        doc.add(new Paragraph("\n"));
        Paragraph pEstado = new Paragraph("ESTADO: CONFIRMADA", new Font(Font.HELVETICA, 16, Font.BOLD, new Color(0, 128, 0)));
        pEstado.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pEstado);

        doc.add(new Paragraph("\n\n\n"));

        Paragraph linea = new Paragraph("-----------------------------------------------------------------------------------");
        linea.setAlignment(Element.ALIGN_CENTER);
        linea.getFont().setColor(Color.GRAY);
        doc.add(linea);

        Font fontFooter = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);
        Paragraph footer = new Paragraph("Por favor presente este comprobante digital o impreso en portería para ingresar.\nGenerado el: " + LocalDate.now(), fontFooter);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);

        doc.close();
        return baos.toByteArray();
    }

    private void agregarFila(PdfPTable table, String label, String value) {
        Font fontLabel = new Font(Font.HELVETICA, 12, Font.BOLD, COLOR_PRINCIPAL);
        Font fontValue = new Font(Font.HELVETICA, 12, Font.NORMAL, COLOR_TEXTO);

        PdfPCell cellLabel = new PdfPCell(new Phrase(label, fontLabel));
        cellLabel.setBorder(Rectangle.BOTTOM);
        cellLabel.setBorderColor(new Color(230, 230, 230));
        cellLabel.setPaddingBottom(8);
        cellLabel.setPaddingTop(8);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, fontValue));
        cellValue.setBorder(Rectangle.BOTTOM);
        cellValue.setBorderColor(new Color(230, 230, 230));
        cellValue.setPaddingBottom(8);
        cellValue.setPaddingTop(8);

        table.addCell(cellLabel);
        table.addCell(cellValue);
        
        
    }
    public byte[] generarReciboPago(Usuario u, Cuota c, double monto) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            Document document = new Document(PageSize.A5.rotate()); 
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();


            Color colorVerde = new Color(32, 50, 30); 
            

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, colorVerde);
            Paragraph titulo = new Paragraph("COMPROBANTE DE PAGO", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            
            document.add(new Paragraph(" ")); 
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            
            Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 12);


            addCell(table, "Fecha de Pago:", fontLabel);
            addCell(table, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontData);
            
            addCell(table, "Recibo N°:", fontLabel);
            addCell(table, "0001-" + String.format("%06d", c.getId()), fontData);


            addCell(table, "Socio:", fontLabel);
            addCell(table, u.getNombreCompleto(), fontData);
            
            addCell(table, "DNI:", fontLabel);
            addCell(table, u.getDni(), fontData);


            addCell(table, "Concepto:", fontLabel);
            addCell(table, "Cuota Mensual N° " + c.getNro_cuota() + " - Vto: " + c.getFecha_vencimiento(), fontData);

            document.add(table);
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("----------------------------------------------------------------"));


            Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, colorVerde);
            Paragraph total = new Paragraph("TOTAL ABONADO: $ " + String.format("%.2f", monto), fontTotal);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            

            Font fontPie = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY);
            Paragraph pie = new Paragraph("Club Los Andes - Gracias por su pago.", fontPie);
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(20);
            document.add(pie);

            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return baos.toByteArray();
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    public byte[] generarConstanciaCanchaPDF(Cancha cancha, Usuario usuario, LocalDate fecha, LocalTime desde, LocalTime hasta) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);
        
        doc.open();

        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        
        PdfPCell cellHeader = new PdfPCell();
        cellHeader.setBackgroundColor(COLOR_PRINCIPAL);
        cellHeader.setPadding(20);
        cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellHeader.setBorder(Rectangle.NO_BORDER);

        Font fontTitulo = new Font(Font.HELVETICA, 24, Font.BOLD, COLOR_FONDO);
        Font fontSubtitulo = new Font(Font.HELVETICA, 14, Font.NORMAL, COLOR_FONDO);
        
        cellHeader.addElement(new Paragraph("Club Deportivo Los Andes", fontTitulo));
        cellHeader.addElement(new Paragraph("Comprobante de Reserva", fontSubtitulo));
        
        headerTable.addCell(cellHeader);
        doc.add(headerTable);
        
        doc.add(new Paragraph("\n\n")); 

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2}); 
        table.setSpacingBefore(10);

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        agregarFila(table, "SOCIO", usuario.getNombreCompleto());
        agregarFila(table, "DNI", usuario.getDni());
        agregarFila(table, "EMAIL", usuario.getMail());
        agregarFila(table, " ", " "); // Espaciador
        agregarFila(table, "CANCHA", cancha.getDescripcion());
        agregarFila(table, "FECHA", fecha.format(dateFmt));
        agregarFila(table, "HORARIO", desde.format(timeFmt) + " a " + hasta.format(timeFmt) + " hs");

        doc.add(table);

        doc.add(new Paragraph("\n"));
        Paragraph pEstado = new Paragraph("ESTADO: CONFIRMADA", new Font(Font.HELVETICA, 16, Font.BOLD, new Color(0, 128, 0)));
        pEstado.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pEstado);

        doc.add(new Paragraph("\n\n\n"));
        
        Paragraph linea = new Paragraph("-----------------------------------------------------------------------------------");
        linea.setAlignment(Element.ALIGN_CENTER);
        linea.getFont().setColor(Color.GRAY);
        doc.add(linea);

        Font fontFooter = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);
        Paragraph footer = new Paragraph("Por favor presente este comprobante digital o impreso en portería para ingresar.\nGenerado el: " + LocalDate.now(), fontFooter);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);

        doc.close();
        return baos.toByteArray();
    }
    public byte[] generarInformeRecaudacionPDF(
            LinkedList<InformeRecaudacion> informe,
            LocalDate desde,
            LocalDate hasta,
            String rutaLogo
    ) throws Exception {


        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        
        Document doc =
                new Document(PageSize.A4,50,50,50,50);


        PdfWriter.getInstance(doc, baos);


        doc.open();
        Image logo = Image.getInstance(rutaLogo);

        logo.scaleToFit(120, 120);
        logo.setAlignment(Element.ALIGN_CENTER);

        doc.add(logo);

        doc.add(new Paragraph("\n"));


        Font tituloClub =
                new Font(Font.HELVETICA,28,Font.BOLD,COLOR_PRINCIPAL);

        Paragraph club =
                new Paragraph("CLUB DEPORTIVO LOS ANDES", tituloClub);

        club.setAlignment(Element.ALIGN_CENTER);
        club.setSpacingAfter(10);

        doc.add(club);


        Paragraph linea = new Paragraph(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        linea.setAlignment(Element.ALIGN_CENTER);
        linea.getFont().setColor(COLOR_PRINCIPAL);

        doc.add(linea);




        Font tituloInforme =
                new Font(Font.HELVETICA,22,Font.BOLD,COLOR_PRINCIPAL);

        Paragraph informeTitulo =
                new Paragraph("INFORME DE RECAUDACIÓN", tituloInforme);

        informeTitulo.setAlignment(Element.ALIGN_CENTER);
        informeTitulo.setSpacingBefore(10);

        informeTitulo.setAlignment(Element.ALIGN_CENTER);

        doc.add(informeTitulo);



        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));



        Font normal =
                new Font(Font.HELVETICA,13);

        Paragraph periodo =
                new Paragraph(

        "Período comprendido entre el "
        + fechaLarga(desde)
        + " ("
        + desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        + ") y el "
        + fechaLarga(hasta)
        + " ("
        + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        + ").",

        normal);

        periodo.setAlignment(Element.ALIGN_CENTER);

        doc.add(periodo);



        doc.add(new Paragraph("\n"));

        Paragraph emision =
                new Paragraph(

        "Fecha de emisión: "
        + fechaLarga(LocalDate.now())
        + " ("
        + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        + ")",

        normal);

        emision.setAlignment(Element.ALIGN_CENTER);

        doc.add(emision);



        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("\n"));


        Paragraph pie =
                new Paragraph(

        "Administración\nClub Deportivo Los Andes",

        FontFactory.getFont(
        FontFactory.HELVETICA_OBLIQUE,
        11,
        Color.GRAY));

        pie.setAlignment(Element.ALIGN_CENTER);

        doc.add(pie);



        double total = 0;

        for (InformeRecaudacion i : informe) {
            total += i.getMonto();
        }

        int cantidadPagos = informe.size();

        double promedio = 0;

        if (cantidadPagos > 0) {
            promedio = total / cantidadPagos;
        }
        
        doc.add(new Paragraph("\n"));

        

        doc.add(new Paragraph("\n"));
        doc.newPage();

        Paragraph tituloGraficos = new Paragraph(
                "Gráficos del período",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
        );

        tituloGraficos.setAlignment(Element.ALIGN_CENTER);

        doc.add(tituloGraficos);

        doc.add(new Paragraph("\n"));

        Image grafico = generarGraficoBarras(informe);
        

        doc.add(grafico);

        doc.add(new Paragraph("\n"));

        Image graficoTorta = generarGraficoTorta(informe);

        doc.add(graficoTorta);

        doc.add(new Paragraph("\n"));
        doc.newPage();

        PdfPTable resumen =
                new PdfPTable(2);

        resumen.setWidthPercentage(80);
        resumen.setHorizontalAlignment(Element.ALIGN_CENTER);
        resumen.setSpacingBefore(20);


        agregarFila(
            resumen,
            "Cantidad de pagos",
            String.valueOf(informe.size())
        );


        agregarFila(
            resumen,
            "Total recaudado",
            "$ " + total
        );


        doc.add(resumen);



        doc.add(new Paragraph("\n"));



        PdfPTable tabla = new PdfPTable(new float[]{2.5f, 1f, 1.5f, 1.5f, 2f});


        Font headerFont =
                FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    11,
                    Color.WHITE
                );


        PdfPCell header;


        header = new PdfPCell(new Phrase("Socio", headerFont));
        header.setBackgroundColor(COLOR_PRINCIPAL);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(header);


        header = new PdfPCell(new Phrase("Cuota", headerFont));
        header.setBackgroundColor(COLOR_PRINCIPAL);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(header);


        header = new PdfPCell(new Phrase("Monto", headerFont));
        header.setBackgroundColor(COLOR_PRINCIPAL);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(header);


        header = new PdfPCell(new Phrase("Fecha", headerFont));
        header.setBackgroundColor(COLOR_PRINCIPAL);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(header);


        header = new PdfPCell(new Phrase("Transacción", headerFont));
        header.setBackgroundColor(COLOR_PRINCIPAL);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(header);



        for(InformeRecaudacion i : informe){


            addCell(tabla,
                i.getNombreUsuario(),
                new Font(Font.HELVETICA,10)
            );


            addCell(tabla,
                String.valueOf(i.getCuota()),
                new Font(Font.HELVETICA,10)
            );


            addCell(tabla,
                "$ "+i.getMonto(),
                new Font(Font.HELVETICA,10)
            );


            addCell(tabla,
                i.getFechaPago(),
                new Font(Font.HELVETICA,10)
            );


            String trans =
                i.getNroTransaccion();


            if(trans == null || trans.isEmpty()){
                trans="Pago efectivo";
            }


            addCell(tabla,
                trans,
                new Font(Font.HELVETICA,10)
            );

        }

        tabla.setSpacingAfter(20);
        doc.add(tabla);



        doc.close();


        return baos.toByteArray();

    }
    private Image generarGraficoBarras(LinkedList<InformeRecaudacion> informe) throws Exception {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Double> recaudacion = new LinkedHashMap<>();

        for (InformeRecaudacion i : informe) {

        	LocalDate fecha = LocalDate.parse(i.getFechaPago());

        	String mes = nombreMes(fecha.getMonthValue()) + " " + fecha.getYear();

            recaudacion.put(
                    mes,
                    recaudacion.getOrDefault(mes, 0.0) + i.getMonto()
            );
        }

        for (String mes : recaudacion.keySet()) {

            dataset.addValue(
                    recaudacion.get(mes),
                    "Recaudación",
                    mes
            );
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Recaudación por mes",
                "Mes",
                "Monto ($)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);

        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, COLOR_PRINCIPAL);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ChartUtils.writeChartAsPNG(
                baos,
                chart,
                550,
                300
        );

        Image img = Image.getInstance(baos.toByteArray());

        img.scaleToFit(500, 280);

        return img;
    }
    private Image generarGraficoTorta(LinkedList<InformeRecaudacion> informe) throws Exception {
    	System.out.println("ENTRO AL GRAFICO DE TORTA");

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        Map<Integer, Double> recaudacionPorCuota = new LinkedHashMap<>();
        System.out.println(recaudacionPorCuota);

        for (InformeRecaudacion i : informe) {

            int cuota = i.getCuota();

            recaudacionPorCuota.put(
                    cuota,
                    recaudacionPorCuota.getOrDefault(cuota, 0.0) + i.getMonto()
            );
        }

        for (Integer cuota : recaudacionPorCuota.keySet()) {

            dataset.setValue(
                    "Cuota " + cuota,
                    recaudacionPorCuota.get(cuota)
            );
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribución de la recaudación por cuota",
                dataset,
                true,
                true,
                false
        );

        PiePlot<?> plot = (PiePlot<?>) chart.getPlot();

plot.setBackgroundPaint(Color.WHITE);
plot.setOutlinePaint(null);


// Colores institucionales Los Andes
Color[] colores = {
        COLOR_PRINCIPAL,
        COLOR_FONDO,
        new Color(90, 120, 80),
        new Color(160, 150, 120),
        new Color(110, 110, 110)
};


int index = 0;

for (Comparable<?> key : dataset.getKeys()) {

    plot.setSectionPaint(
            key,
            colores[index % colores.length]
    );

    index++;
}

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ChartUtils.writeChartAsPNG(
                baos,
                chart,
                500,
                320
        );

        Image img = Image.getInstance(baos.toByteArray());

        img.scaleToFit(450, 300);

        return img;
    }
    private String nombreMes(int mes){

        switch(mes){

            case 1: return "Enero";
            case 2: return "Febrero";
            case 3: return "Marzo";
            case 4: return "Abril";
            case 5: return "Mayo";
            case 6: return "Junio";
            case 7: return "Julio";
            case 8: return "Agosto";
            case 9: return "Septiembre";
            case 10: return "Octubre";
            case 11: return "Noviembre";
            default: return "Diciembre";

        }

    }
    private String fechaLarga(LocalDate fecha){

        String mes;

        switch(fecha.getMonthValue()){

            case 1: mes="enero"; break;
            case 2: mes="febrero"; break;
            case 3: mes="marzo"; break;
            case 4: mes="abril"; break;
            case 5: mes="mayo"; break;
            case 6: mes="junio"; break;
            case 7: mes="julio"; break;
            case 8: mes="agosto"; break;
            case 9: mes="septiembre"; break;
            case 10: mes="octubre"; break;
            case 11: mes="noviembre"; break;
            default: mes="diciembre";

        }

        return fecha.getDayOfMonth()
                + " de "
                + mes
                + " de "
                + fecha.getYear();

    }

}