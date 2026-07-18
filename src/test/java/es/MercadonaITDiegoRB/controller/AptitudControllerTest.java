package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.dto.AptitudTrabajadorDto;
import es.MercadonaITDiegoRB.service.AptitudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AptitudControllerTest {

    private static final String DNI = "12345678A";
    private static final String APTITUD = "Hornear Pan";

    @Mock
    private AptitudService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AptitudController(service))
                .build();
    }

    @Test
    void returnsCreatedAssignment() throws Exception {
        AptitudTrabajadorDto response = new AptitudTrabajadorDto()
                .dniTrabajador(DNI)
                .aptitud(APTITUD);
        when(service.addAptitud(DNI, APTITUD)).thenReturn(response);

        mockMvc.perform(post(
                        "/trabajador/{DNI}/aptitudes/{aptitud}",
                        DNI,
                        APTITUD
                ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dniTrabajador").value(DNI))
                .andExpect(jsonPath("$.aptitud").value(APTITUD));

        verify(service).addAptitud(DNI, APTITUD);
    }

    @Test
    void deletesAssignmentAndReturnsNoContent() throws Exception {
        mockMvc.perform(delete(
                        "/trabajador/{DNI}/aptitudes/{aptitud}",
                        DNI,
                        APTITUD
                ))
                .andExpect(status().isNoContent());

        verify(service).deleteAptitud(DNI, APTITUD);
    }
}
