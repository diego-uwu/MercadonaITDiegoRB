package es.MercadonaITDiegoRB.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionHandlerTest {

    private ApiExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ApiExceptionHandler();
    }

    @Test
    void mapsResourceNotFoundToNotFound() {
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Trabajador", "12345678A");

        ProblemDetail result = handler.handleResourceNotFound(exception);

        assertProblemDetail(
                result,
                404,
                "Recurso no encontrado",
                exception.getMessage()
        );
    }

    @Test
    void mapsResourceAlreadyExistsToConflict() {
        ResourceAlreadyExistsException exception =
                new ResourceAlreadyExistsException("Trabajador", "DNI", "12345678A");

        ProblemDetail result = handler.handleResourceAlreadyExists(exception);

        assertProblemDetail(
                result,
                409,
                "El recurso ya existe",
                exception.getMessage()
        );
    }

    @Test
    void mapsInvalidReferenceToBadRequest() {
        InvalidReferenceException exception =
                new InvalidReferenceException("Trabajador", "tienda", 99L);

        ProblemDetail result = handler.handleInvalidReference(exception);

        assertProblemDetail(
                result,
                400,
                "Referencia inválida",
                exception.getMessage()
        );
    }

    @Test
    void mapsExceededHoursToConflict() {
        HorasDisponiblesExceededException exception =
                new HorasDisponiblesExceededException("12345678A", 8, 10);

        ProblemDetail result = handler.handleHorasDisponiblesExceeded(exception);

        assertProblemDetail(
                result,
                409,
                "Horas disponibles excedidas",
                exception.getMessage()
        );
    }

    @Test
    void mapsUnqualifiedTrabajadorToConflict() {
        TrabajadorNoCualificadoException exception = new TrabajadorNoCualificadoException(
                "12345678A",
                "Horno",
                java.util.List.of("Repostería")
        );

        ProblemDetail result = handler.handleTrabajadorNoCualificado(exception);

        assertProblemDetail(
                result,
                409,
                "Trabajador no cualificado",
                exception.getMessage()
        );
    }

    @Test
    void mapsDataIntegrityViolationToBadRequest() {
        ProblemDetail result = handler.handleDataIntegrityViolation();

        assertProblemDetail(
                result,
                400,
                "Datos inválidos",
                "La petición viola una restricción de integridad de datos"
        );
    }

    @Test
    void mapsExternalApiFailureToBadGateway() {
        ExternalApiException exception = new ExternalApiException(
                1L,
                new RuntimeException("Connection refused")
        );

        ProblemDetail result = handler.handleExternalApi(exception);

        assertProblemDetail(
                result,
                502,
                "Error al consultar la API de tiendas",
                exception.getMessage()
        );
    }

    private void assertProblemDetail(
            ProblemDetail problemDetail,
            int status,
            String title,
            String detail
    ) {
        assertEquals(status, problemDetail.getStatus());
        assertEquals(title, problemDetail.getTitle());
        assertEquals(detail, problemDetail.getDetail());
    }
}
