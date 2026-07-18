package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.entity.TiendaEntity;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.repository.TiendaRepository;
import es.MercadonaITDiegoRB.repository.TurnoRepository;
import es.MercadonaITDiegoRB.repository.projection.EstadoTiendaRow;
import es.MercadonaITDiegoRB.repository.projection.SeccionIncompletaRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.parser.PdfTextExtractor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentosServiceTest {

    private static final long TIENDA_ID = 1L;

    @Mock
    private TiendaRepository tiendaRepository;

    @Mock
    private TurnoRepository turnoRepository;

    private DocumentosService documentosService;

    @BeforeEach
    void setUp() {
        documentosService = new DocumentosService(tiendaRepository, turnoRepository);
    }

    @Test
    void generatesEstadoReportWithOneTablePerSection() throws Exception {
        TiendaEntity tienda = new TiendaEntity(TIENDA_ID, "Madrid centro");
        when(tiendaRepository.findById(TIENDA_ID)).thenReturn(Optional.of(tienda));
        when(turnoRepository.findEstadoTiendaRows(TIENDA_ID)).thenReturn(List.of(
                row("Cajas", null, null, null),
                row("Horno", "Ana", "Garcia", 2),
                row("Horno", "Diego", "Rodriguez", 4)
        ));

        byte[] pdf = documentosService.getEstadoTiendaReport(TIENDA_ID);
        String text = extractText(pdf);

        assertTrue(pdf.length > 0);
        assertArrayEquals("%PDF".getBytes(), java.util.Arrays.copyOf(pdf, 4));
        assertTrue(text.contains("Madrid centro"));
        assertTrue(text.contains("CAJAS"));
        assertTrue(text.contains("Sin trabajadores asignados"));
        assertTrue(text.contains("HORNO"));
        assertTrue(text.contains("Ana Garcia"));
        assertTrue(text.contains("2 h"));
        assertTrue(text.contains("Diego Rodriguez"));
        assertTrue(text.contains("4 h"));
    }

    @Test
    void generatesMessageWhenTiendaHasNoConfiguredSections() throws Exception {
        TiendaEntity tienda = new TiendaEntity(TIENDA_ID, "Madrid centro");
        when(tiendaRepository.findById(TIENDA_ID)).thenReturn(Optional.of(tienda));
        when(turnoRepository.findEstadoTiendaRows(TIENDA_ID)).thenReturn(List.of());

        byte[] pdf = documentosService.getEstadoTiendaReport(TIENDA_ID);

        assertTrue(extractText(pdf).contains(
                "La tienda no tiene secciones configuradas."
        ));
    }

    @Test
    void rejectsReportWhenTiendaDoesNotExist() {
        when(tiendaRepository.findById(TIENDA_ID)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> documentosService.getEstadoTiendaReport(TIENDA_ID)
        );

        verifyNoInteractions(turnoRepository);
    }

    @Test
    void generatesSeccionesIncompletasReportWithRemainingHours() throws Exception {
        TiendaEntity tienda = new TiendaEntity(TIENDA_ID, "Madrid centro");
        when(tiendaRepository.findById(TIENDA_ID)).thenReturn(Optional.of(tienda));
        when(turnoRepository.findSeccionesIncompletasRows(TIENDA_ID)).thenReturn(List.of(
                incompleteRow("Cajas", 16, 10, 6),
                incompleteRow("Horno", 8, 0, 8)
        ));

        byte[] pdf = documentosService.getSeccionesIncompletasReport(TIENDA_ID);
        String text = extractText(pdf);

        assertTrue(pdf.length > 0);
        assertArrayEquals("%PDF".getBytes(), java.util.Arrays.copyOf(pdf, 4));
        assertTrue(text.contains("Madrid centro"));
        assertTrue(text.contains("Cajas"));
        assertTrue(text.contains("16 h"));
        assertTrue(text.contains("10 h"));
        assertTrue(text.contains("6 h"));
        assertTrue(text.contains("Horno"));
        assertTrue(text.contains("0 h"));
        assertTrue(text.contains("8 h"));
    }

    @Test
    void generatesOperationalMessageWhenNoSectionIsIncomplete() throws Exception {
        TiendaEntity tienda = new TiendaEntity(TIENDA_ID, "Madrid centro");
        when(tiendaRepository.findById(TIENDA_ID)).thenReturn(Optional.of(tienda));
        when(turnoRepository.findSeccionesIncompletasRows(TIENDA_ID))
                .thenReturn(List.of());

        byte[] pdf = documentosService.getSeccionesIncompletasReport(TIENDA_ID);

        assertTrue(extractText(pdf).contains(
                "Todas las secciones de esta tienda cumplen los requisitos mínimos "
                        + "para operar con normalidad."
        ));
    }

    @Test
    void rejectsSeccionesIncompletasReportWhenTiendaDoesNotExist() {
        when(tiendaRepository.findById(TIENDA_ID)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> documentosService.getSeccionesIncompletasReport(TIENDA_ID)
        );

        verifyNoInteractions(turnoRepository);
    }

    private String extractText(byte[] pdf) throws Exception {
        PdfReader reader = new PdfReader(pdf);
        try {
            return new PdfTextExtractor(reader).getTextFromPage(1);
        } finally {
            reader.close();
        }
    }

    private EstadoTiendaRow row(
            String seccion,
            String nombre,
            String apellidos,
            Integer horas
    ) {
        return new EstadoTiendaRow() {
            @Override
            public String getSeccion() {
                return seccion;
            }

            @Override
            public String getNombre() {
                return nombre;
            }

            @Override
            public String getApellidos() {
                return apellidos;
            }

            @Override
            public Integer getHorasAsignadas() {
                return horas;
            }
        };
    }

    private SeccionIncompletaRow incompleteRow(
            String seccion,
            Integer horasNecesarias,
            Integer horasAsignadas,
            Integer horasRestantes
    ) {
        return new SeccionIncompletaRow() {
            @Override
            public String getSeccion() {
                return seccion;
            }

            @Override
            public Integer getHorasNecesarias() {
                return horasNecesarias;
            }

            @Override
            public Integer getHorasAsignadas() {
                return horasAsignadas;
            }

            @Override
            public Integer getHorasRestantes() {
                return horasRestantes;
            }
        };
    }
}
