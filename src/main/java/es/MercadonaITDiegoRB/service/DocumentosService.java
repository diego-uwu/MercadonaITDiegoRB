package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.client.StoresApiClient;
import es.MercadonaITDiegoRB.client.dto.StoreDto;
import es.MercadonaITDiegoRB.entity.TiendaEntity;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.repository.TiendaRepository;
import es.MercadonaITDiegoRB.repository.TurnoRepository;
import es.MercadonaITDiegoRB.repository.projection.EstadoTiendaRow;
import es.MercadonaITDiegoRB.repository.projection.SeccionIncompletaRow;
import es.MercadonaITDiegoRB.utils.DocumentBuilderUtils;
import lombok.RequiredArgsConstructor;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DocumentosService {

    private final TiendaRepository tiendaRepository;
    private final TurnoRepository turnoRepository;
    private final StoresApiClient storesApiClient;

    public byte[] getEstadoTiendaReport(Long tiendaId) {
        TiendaEntity tienda = tiendaRepository.findById(tiendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda", tiendaId));
        StoreDto store = storesApiClient.getStore(tiendaId);

        List<EstadoTiendaRow> rows = turnoRepository.findEstadoTiendaRows(tiendaId);
        Map<String, List<EstadoTiendaRow>> rowsBySeccion = rows.stream()
                .collect(Collectors.groupingBy(
                        EstadoTiendaRow::getSeccion,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, output);
            document.addTitle("Empleados por sección");
            document.open();

            DocumentBuilderUtils.addTitleEstadoTienda(document);
            DocumentBuilderUtils.addTiendaInfo(
                    document,
                    tienda.getNombre(),
                    store.address(),
                    store.city()
            );

            if (rowsBySeccion.isEmpty()) {
                document.add(new Paragraph(
                        "La tienda no tiene secciones configuradas.",
                        DocumentBuilderUtils.BODY_FONT
                ));
            } else {
                rowsBySeccion.forEach((seccion, sectionRows) ->
                        DocumentBuilderUtils.addSeccionTable(document, seccion, sectionRows)
                );
            }
        } catch (DocumentException exception) {
            throw new IllegalStateException(
                    "No se pudo generar el informe de estado de la tienda " + tiendaId,
                    exception
            );
        }

        return output.toByteArray();
    }

    public byte[] getSeccionesIncompletasReport(Long tiendaId) {
        TiendaEntity tienda = tiendaRepository.findById(tiendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda", tiendaId));
        StoreDto store = storesApiClient.getStore(tiendaId);

        List<SeccionIncompletaRow> rows =
                turnoRepository.findSeccionesIncompletasRows(tiendaId);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, output);
            document.addTitle("Secciones con horas insuficientes");
            document.open();

            DocumentBuilderUtils.addTitleSeccionesIncompletas(document);
            DocumentBuilderUtils.addTiendaInfo(
                    document,
                    tienda.getNombre(),
                    store.address(),
                    store.city()
            );

            if (rows.isEmpty()) {
                document.add(new Paragraph(
                        "Todas las secciones de esta tienda cumplen los requisitos mínimos "
                                + "para operar con normalidad.",
                        DocumentBuilderUtils.BODY_FONT
                ));
            } else {
                DocumentBuilderUtils.addSeccionesIncompletasTable(document, rows);
            }
        } catch (DocumentException exception) {
            throw new IllegalStateException(
                    "No se pudo generar el informe de secciones incompletas de la tienda "
                            + tiendaId,
                    exception
            );
        }

        return output.toByteArray();
    }
}
