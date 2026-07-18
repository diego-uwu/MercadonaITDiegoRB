package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.dto.TurnoDto;
import es.MercadonaITDiegoRB.exception.ApiExceptionHandler;
import es.MercadonaITDiegoRB.exception.HorasDisponiblesExceededException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.service.TurnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TurnosControllerTest {

    private static final String DNI = "12345678A";
    private static final String SECCION = "Horno";
    private static final String VALID_BODY = """
            {
              "dniTrabajador": "12345678A",
              "seccion": "Horno",
              "horas": 4
            }
            """;

    @Mock
    private TurnoService turnoService;

    private MockMvc mockMvc;
    private TurnoDto turnoDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TurnosController(turnoService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
        turnoDto = new TurnoDto()
                .dniTrabajador(DNI)
                .tienda(1L)
                .seccion(SECCION)
                .horas(4);
    }

    @Test
    void getReturnsAllTurnosAndOk() throws Exception {
        TurnoDto pescaderia = new TurnoDto()
                .dniTrabajador(DNI)
                .tienda(1L)
                .seccion("Pescaderia")
                .horas(4);
        when(turnoService.getTurnosByTrabajador(DNI))
                .thenReturn(List.of(turnoDto, pescaderia));

        mockMvc.perform(get("/turno/{DNI}", DNI))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].seccion").value(SECCION))
                .andExpect(jsonPath("$[1].seccion").value("Pescaderia"));

        verify(turnoService).getTurnosByTrabajador(DNI);
    }

    @Test
    void getReturnsEmptyArrayForTrabajadorWithoutTurnos() throws Exception {
        when(turnoService.getTurnosByTrabajador(DNI)).thenReturn(List.of());

        mockMvc.perform(get("/turno/{DNI}", DNI))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getReturnsNotFoundForUnknownTrabajador() throws Exception {
        when(turnoService.getTurnosByTrabajador(DNI))
                .thenThrow(new ResourceNotFoundException("Trabajador", DNI));

        mockMvc.perform(get("/turno/{DNI}", DNI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"));
    }

    @Test
    void putReturnsCreatedTurno() throws Exception {
        when(turnoService.saveTurno(any(TurnoDto.class))).thenReturn(turnoDto);

        mockMvc.perform(put("/turno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dniTrabajador").value(DNI))
                .andExpect(jsonPath("$.seccion").value(SECCION))
                .andExpect(jsonPath("$.horas").value(4));

        verify(turnoService).saveTurno(argThat(request ->
                DNI.equals(request.getDniTrabajador())
                        && SECCION.equals(request.getSeccion())
                        && request.getHoras() == 4
                        && request.getTienda() == null
        ));
    }

    @Test
    void putRejectsMissingRequiredFieldsBeforeCallingService() throws Exception {
        mockMvc.perform(put("/turno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(turnoService);
    }

    @Test
    void putRejectsHoursBelowMinimumBeforeCallingService() throws Exception {
        mockMvc.perform(put("/turno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dniTrabajador": "12345678A",
                                  "seccion": "Horno",
                                  "horas": 0
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(turnoService);
    }

    @Test
    void putRejectsHoursAboveMaximumBeforeCallingService() throws Exception {
        mockMvc.perform(put("/turno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dniTrabajador": "12345678A",
                                  "seccion": "Horno",
                                  "horas": 9
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(turnoService);
    }

    @Test
    void putRejectsDniLongerThanNineCharacters() throws Exception {
        mockMvc.perform(put("/turno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dniTrabajador": "1234567890",
                                  "seccion": "Horno",
                                  "horas": 4
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(turnoService);
    }

    @Test
    void putRejectsSectionLongerThanFiftyCharacters() throws Exception {
        String longSection = "S".repeat(51);

        mockMvc.perform(put("/turno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dniTrabajador": "12345678A",
                                  "seccion": "%s",
                                  "horas": 4
                                }
                                """.formatted(longSection)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(turnoService);
    }

    @Test
    void putReturnsConflictWhenHoursAreExceeded() throws Exception {
        when(turnoService.saveTurno(any(TurnoDto.class)))
                .thenThrow(new HorasDisponiblesExceededException(DNI, 8, 10));

        mockMvc.perform(put("/turno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Horas disponibles excedidas"))
                .andExpect(jsonPath("$.detail").value(
                        org.hamcrest.Matchers.containsString(DNI)
                ));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/turno/{DNI}/{seccion}", DNI, SECCION))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(turnoService).deleteTurno(DNI, SECCION);
    }

    @Test
    void deleteReturnsNotFoundForUnknownTurno() throws Exception {
        doThrow(new ResourceNotFoundException("Turno", DNI + "/" + SECCION))
                .when(turnoService).deleteTurno(DNI, SECCION);

        mockMvc.perform(delete("/turno/{DNI}/{seccion}", DNI, SECCION))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"));
    }
}
