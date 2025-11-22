package logic;

import com.lowagie.text.Document;
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

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GeneradorArchivos {

    // Colores de tu marca
    private static final Color COLOR_PRINCIPAL = new Color(32, 50, 30); // #20321E
    private static final Color COLOR_FONDO = new Color(221, 216, 202);  // #DDD8CA
    private static final Color COLOR_TEXTO = new Color(60, 60, 60);

    public byte[] generarConstanciaPDF(Salon salon, Usuario usuario, LocalDate fecha, LocalTime desde, LocalTime hasta) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);
        
        doc.open();

        // --- 1. ENCABEZADO CON CAJA DE COLOR ---
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
        
        doc.add(new Paragraph("\n\n")); // Espacio

        // --- 2. TABLA DE DETALLES ---
        // Usamos una tabla para alinear "Etiqueta: Valor" perfectamente
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2}); // La columna de valor es el doble de ancha
        table.setSpacingBefore(10);

        // Formateadores de fecha/hora
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        // Agregar filas
        agregarFila(table, "SOCIO", usuario.getNombreCompleto());
        agregarFila(table, "DNI", usuario.getDni());
        agregarFila(table, "EMAIL", usuario.getMail());
        agregarFila(table, " ", " "); // Espaciador
        agregarFila(table, "SALÓN", salon.getNombre());
        agregarFila(table, "FECHA", fecha.format(dateFmt));
        agregarFila(table, "HORARIO", desde.format(timeFmt) + " a " + hasta.format(timeFmt) + " hs");

        doc.add(table);

        // --- 3. ESTADO ---
        doc.add(new Paragraph("\n"));
        Paragraph pEstado = new Paragraph("ESTADO: CONFIRMADA", new Font(Font.HELVETICA, 16, Font.BOLD, new Color(0, 128, 0)));
        pEstado.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pEstado);

        // --- 4. PIE DE PÁGINA / LINEA DIVISORIA ---
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


            Color colorVerde = new Color(32, 50, 30); // #20321E
            

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

        // --- 1. ENCABEZADO CON CAJA DE COLOR ---
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
        
        doc.add(new Paragraph("\n\n")); // Espacio

        // --- 2. TABLA DE DETALLES ---
        // Usamos una tabla para alinear "Etiqueta: Valor" perfectamente
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2}); // La columna de valor es el doble de ancha
        table.setSpacingBefore(10);

        // Formateadores de fecha/hora
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        // Agregar filas
        agregarFila(table, "SOCIO", usuario.getNombreCompleto());
        agregarFila(table, "DNI", usuario.getDni());
        agregarFila(table, "EMAIL", usuario.getMail());
        agregarFila(table, " ", " "); // Espaciador
        agregarFila(table, "CANCHA", cancha.getDescripcion());
        agregarFila(table, "FECHA", fecha.format(dateFmt));
        agregarFila(table, "HORARIO", desde.format(timeFmt) + " a " + hasta.format(timeFmt) + " hs");

        doc.add(table);

        // --- 3. ESTADO ---
        doc.add(new Paragraph("\n"));
        Paragraph pEstado = new Paragraph("ESTADO: CONFIRMADA", new Font(Font.HELVETICA, 16, Font.BOLD, new Color(0, 128, 0)));
        pEstado.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pEstado);

        // --- 4. PIE DE PÁGINA / LINEA DIVISORIA ---
        doc.add(new Paragraph("\n\n\n"));
        
        // Linea punteada
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

}