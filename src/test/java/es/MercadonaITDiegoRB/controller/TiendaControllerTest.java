package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.dto.TiendaDto;
import es.MercadonaITDiegoRB.exception.ApiExceptionHandler;
import es.MercadonaITDiegoRB.exception.ResourceAlreadyExistsException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.exception.TiendaConTrabajadoresException;
import es.MercadonaITDiegoRB.service.TiendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TiendaControllerTest {

    private static final Long TIENDA_ID = 4L;
    private static final String VALID_BODY = """
            {
              "codigo": 4,
              "nombre": "Tienda 4"
            }
            """;

    @Mock
    private TiendaService tiendaService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TiendaController(tiendaService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void postReturnsCreatedTienda() throws Exception {
        TiendaDto tienda = new TiendaDto().codigo(TIENDA_ID).nombre("Tienda 4");
        when(tiendaService.insertTienda(any(TiendaDto.class))).thenReturn(tienda);

        mockMvc.perform(post("/tienda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(TIENDA_ID))
                .andExpect(jsonPath("$.nombre").value("Tienda 4"));
    }

    @Test
    void postReturnsConflictForDuplicateCodigo() throws Exception {
        when(tiendaService.insertTienda(any(TiendaDto.class)))
                .thenThrow(new ResourceAlreadyExistsException("Tienda", "código", TIENDA_ID));

        mockMvc.perform(post("/tienda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void postValidatesRequiredFields() throws Exception {
        mockMvc.perform(post("/tienda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tiendaService);
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/tienda/{tiendaId}", TIENDA_ID))
                .andExpect(status().isNoContent());

        verify(tiendaService).deleteTienda(TIENDA_ID);
    }

    @Test
    void deleteReturnsNotFoundForUnknownTienda() throws Exception {
        doThrow(new ResourceNotFoundException("Tienda", TIENDA_ID))
                .when(tiendaService).deleteTienda(TIENDA_ID);

        mockMvc.perform(delete("/tienda/{tiendaId}", TIENDA_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturnsConflictWhenTiendaHasTrabajadores() throws Exception {
        doThrow(new TiendaConTrabajadoresException(TIENDA_ID))
                .when(tiendaService).deleteTienda(TIENDA_ID);

        mockMvc.perform(delete("/tienda/{tiendaId}", TIENDA_ID))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("La tienda tiene trabajadores asignados"));
    }
}
