package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.api.DocumentosApi;
import es.MercadonaITDiegoRB.service.DocumentosService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class DocumentosController implements DocumentosApi {

    private final DocumentosService documentosService;

    @Override
    public ResponseEntity<Resource> getEstadoTiendaReport(Long tiendaId) {
        byte[] pdf = documentosService.getEstadoTiendaReport(tiendaId);
        Resource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("estado-tienda-" + tiendaId + ".pdf")
                                .build()
                                .toString()
                )
                .body(resource);
    }

    @Override
    public ResponseEntity<Resource> getSeccionesIncompletasReport(Long tiendaId) {
        byte[] pdf = documentosService.getSeccionesIncompletasReport(tiendaId);
        Resource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("secciones-incompletas-tienda-" + tiendaId + ".pdf")
                                .build()
                                .toString()
                )
                .body(resource);
    }
}
