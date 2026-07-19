package es.MercadonaITDiegoRB.utils;

import es.MercadonaITDiegoRB.repository.projection.EstadoTiendaRow;
import es.MercadonaITDiegoRB.repository.projection.SeccionIncompletaRow;
import lombok.experimental.UtilityClass;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;

import java.util.List;

@UtilityClass
public class DocumentBuilderUtils {

    public static final Font BODY_FONT =
            FontFactory.getFont(FontFactory.TIMES_ROMAN, 12);
    public static final Font BOLD_FONT =
            FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);
    public static final Font TITLE_FONT =
            FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.BOLD);
    public static final Font SECTION_FONT =
            FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD);

    public void addTitleEstadoTienda(Document document) {
        Paragraph title = new Paragraph("Empleados por sección", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(12);
        document.add(title);
    }

    public void addTiendaInfo(
            Document document,
            String tiendaNombre,
            String address,
            String city
    ) {
        Paragraph name = new Paragraph();
        name.add(new Chunk("Nombre de la tienda: ", BOLD_FONT));
        name.add(new Chunk(tiendaNombre, BODY_FONT));
        name.setSpacingAfter(4);
        document.add(name);

        Paragraph location = new Paragraph();
        location.add(new Chunk("Dirección: ", BOLD_FONT));
        location.add(new Chunk(address + ", " + city, BODY_FONT));
        location.setSpacingAfter(18);
        document.add(location);
    }

    public void addSeccionTable(
            Document document,
            String seccion,
            List<EstadoTiendaRow> rows
    ) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1});
        table.setSpacingAfter(14);

        PdfPCell sectionHeader = new PdfPCell(new Phrase(seccion.toUpperCase(), SECTION_FONT));
        sectionHeader.setColspan(2);
        sectionHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        sectionHeader.setPadding(8);
        table.addCell(sectionHeader);

        table.addCell(headerCell("TRABAJADOR"));
        table.addCell(headerCell("HORAS ASIGNADAS"));
        table.setHeaderRows(2);

        boolean hasTrabajadores = rows.stream()
                .anyMatch(row -> row.getNombre() != null);

        if (!hasTrabajadores) {
            PdfPCell emptyCell = new PdfPCell(
                    new Phrase("Sin trabajadores asignados", BOLD_FONT)
            );
            emptyCell.setColspan(2);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setPadding(6);
            table.addCell(emptyCell);
        } else {
            rows.stream()
                    .filter(row -> row.getNombre() != null)
                    .forEach(row -> {
                        table.addCell(bodyCell(fullName(row)));
                        table.addCell(bodyCell(row.getHorasAsignadas() + " h"));
                    });
        }

        document.add(table);
    }

    public void addTitleSeccionesIncompletas(Document document) {
        Paragraph title = new Paragraph(
                "Secciones con horas insuficientes",
                TITLE_FONT
        );
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(12);
        document.add(title);
    }

    public void addSeccionesIncompletasTable(
            Document document,
            List<SeccionIncompletaRow> rows
    ) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 1, 1, 1});

        table.addCell(headerCell("SECCIÓN"));
        table.addCell(headerCell("HORAS MÍNIMAS"));
        table.addCell(headerCell("HORAS ASIGNADAS"));
        table.addCell(headerCell("HORAS RESTANTES"));
        table.setHeaderRows(1);

        rows.forEach(row -> {
            table.addCell(bodyCell(row.getSeccion()));
            table.addCell(bodyCell(row.getHorasNecesarias() + " h"));
            table.addCell(bodyCell(row.getHorasAsignadas() + " h"));
            table.addCell(bodyCell(row.getHorasRestantes() + " h"));
        });

        document.add(table);
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BOLD_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell bodyCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BODY_FONT));
        cell.setPadding(6);
        return cell;
    }

    private String fullName(EstadoTiendaRow row) {
        return row.getNombre() + " " + row.getApellidos();
    }
}
