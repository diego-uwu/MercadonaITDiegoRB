package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.exception.ApiExceptionHandler;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.service.DocumentosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DocumentosControllerTest {

    private static final long TIENDA_ID = 1L;

    @Mock
    private DocumentosService documentosService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new DocumentosController(documentosService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void returnsEstadoTiendaPdfAsAttachment() throws Exception {
        byte[] pdf = "%PDF-report".getBytes();
        when(documentosService.getEstadoTiendaReport(TIENDA_ID)).thenReturn(pdf);

        mockMvc.perform(get("/documento/estado/{tiendaId}", TIENDA_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdf))
                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, pdf.length))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"estado-tienda-1.pdf\""
                ));

        verify(documentosService).getEstadoTiendaReport(TIENDA_ID);
    }

    @Test
    void returnsNotFoundWhenTiendaDoesNotExist() throws Exception {
        when(documentosService.getEstadoTiendaReport(TIENDA_ID))
                .thenThrow(new ResourceNotFoundException("Tienda", TIENDA_ID));

        mockMvc.perform(get("/documento/estado/{tiendaId}", TIENDA_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"));
    }

    @Test
    void returnsSeccionesIncompletasPdfAsAttachment() throws Exception {
        byte[] pdf = "%PDF-incomplete-sections".getBytes();
        when(documentosService.getSeccionesIncompletasReport(TIENDA_ID))
                .thenReturn(pdf);

        mockMvc.perform(get(
                        "/documento/secciones-incompletas/{tiendaId}",
                        TIENDA_ID
                ))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdf))
                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, pdf.length))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"secciones-incompletas-tienda-1.pdf\""
                ));

        verify(documentosService).getSeccionesIncompletasReport(TIENDA_ID);
    }

    @Test
    void returnsNotFoundForSeccionesIncompletasWhenTiendaDoesNotExist() throws Exception {
        when(documentosService.getSeccionesIncompletasReport(TIENDA_ID))
                .thenThrow(new ResourceNotFoundException("Tienda", TIENDA_ID));

        mockMvc.perform(get(
                        "/documento/secciones-incompletas/{tiendaId}",
                        TIENDA_ID
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"));
    }
}
