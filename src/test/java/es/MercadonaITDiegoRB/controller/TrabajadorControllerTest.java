package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.dto.TrabajadorDto;
import es.MercadonaITDiegoRB.exception.ApiExceptionHandler;
import es.MercadonaITDiegoRB.exception.InvalidReferenceException;
import es.MercadonaITDiegoRB.exception.ResourceAlreadyExistsException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.service.TrabajadorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrabajadorControllerTest {

    private static final String DNI = "12345678A";
    private static final String VALID_BODY = """
            {
              "dni": "12345678A",
              "nombre": "Diego",
              "apellidos": "Rodriguez",
              "tienda": 1,
              "horasDisponibles": 8
            }
            """;

    @Mock
    private TrabajadorService trabajadorService;

    private MockMvc mockMvc;
    private TrabajadorDto trabajadorDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrabajadorController(trabajadorService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
        trabajadorDto = new TrabajadorDto()
                .dni(DNI)
                .nombre("Diego")
                .apellidos("Rodriguez")
                .tienda(1L)
                .horasDisponibles(8);
    }

    @Test
    void getReturnsTrabajadorAndOk() throws Exception {
        when(trabajadorService.getTrabajadorByDNI(DNI)).thenReturn(trabajadorDto);

        mockMvc.perform(get("/trabajador/{DNI}", DNI))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dni").value(DNI))
                .andExpect(jsonPath("$.tienda").value(1))
                .andExpect(jsonPath("$.horasDisponibles").value(8));

        verify(trabajadorService).getTrabajadorByDNI(DNI);
    }

    @Test
    void getReturnsNotFoundProblemDetail() throws Exception {
        when(trabajadorService.getTrabajadorByDNI(DNI))
                .thenThrow(new ResourceNotFoundException("Trabajador", DNI));

        mockMvc.perform(get("/trabajador/{DNI}", DNI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.detail").value(
                        "Trabajador con identificador " + DNI + " no encontrado"
                ));
    }

    @Test
    void postReturnsCreatedTrabajador() throws Exception {
        when(trabajadorService.insertTrabajador(trabajadorDto)).thenReturn(trabajadorDto);

        mockMvc.perform(post("/trabajador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value(DNI));

        verify(trabajadorService).insertTrabajador(trabajadorDto);
    }

    @Test
    void postRejectsInvalidBodyBeforeCallingService() throws Exception {
        mockMvc.perform(post("/trabajador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trabajadorService);
    }

    @Test
    void postRejectsDniLongerThanNineCharacters() throws Exception {
        mockMvc.perform(post("/trabajador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dni": "1234567890",
                                  "nombre": "Diego",
                                  "apellidos": "Rodriguez",
                                  "tienda": 1,
                                  "horasDisponibles": 8
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trabajadorService);
    }

    @Test
    void postReturnsConflictForDuplicateDni() throws Exception {
        when(trabajadorService.insertTrabajador(trabajadorDto))
                .thenThrow(new ResourceAlreadyExistsException("Trabajador", "DNI", DNI));

        mockMvc.perform(post("/trabajador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("El recurso ya existe"));
    }

    @Test
    void postReturnsBadRequestForInvalidTiendaReference() throws Exception {
        when(trabajadorService.insertTrabajador(trabajadorDto))
                .thenThrow(new InvalidReferenceException("Trabajador", "tienda", 1L));

        mockMvc.perform(post("/trabajador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Referencia inválida"));
    }

    @Test
    void postReturnsBadRequestForDatabaseConstraintViolation() throws Exception {
        when(trabajadorService.insertTrabajador(trabajadorDto))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("constraint"));

        mockMvc.perform(post("/trabajador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Datos inválidos"));
    }

    @Test
    void putReturnsUpdatedTrabajador() throws Exception {
        when(trabajadorService.updateTrabajador(trabajadorDto)).thenReturn(trabajadorDto);

        mockMvc.perform(put("/trabajador/{DNI}", DNI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value(DNI));

        verify(trabajadorService).updateTrabajador(trabajadorDto);
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/trabajador/{DNI}", DNI))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(trabajadorService).deleteTrabajador(DNI);
    }

    @Test
    void deleteReturnsNotFoundWhenServiceRejectsIt() throws Exception {
        doThrow(new ResourceNotFoundException("Trabajador", DNI))
                .when(trabajadorService).deleteTrabajador(DNI);

        mockMvc.perform(delete("/trabajador/{DNI}", DNI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"));
    }
}
