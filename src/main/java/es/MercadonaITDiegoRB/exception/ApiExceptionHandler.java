package es.MercadonaITDiegoRB.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException exception) {
        return createProblemDetail(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                exception.getMessage()
        );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ProblemDetail handleResourceAlreadyExists(ResourceAlreadyExistsException exception) {
        return createProblemDetail(
                HttpStatus.CONFLICT,
                "El recurso ya existe",
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidReferenceException.class)
    public ProblemDetail handleInvalidReference(InvalidReferenceException exception) {
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Referencia inválida",
                exception.getMessage()
        );
    }

    @ExceptionHandler(HorasDisponiblesExceededException.class)
    public ProblemDetail handleHorasDisponiblesExceeded(
            HorasDisponiblesExceededException exception
    ) {
        return createProblemDetail(
                HttpStatus.CONFLICT,
                "Horas disponibles excedidas",
                exception.getMessage()
        );
    }

    @ExceptionHandler(TiendaConTrabajadoresException.class)
    public ProblemDetail handleTiendaConTrabajadores(TiendaConTrabajadoresException exception) {
        return createProblemDetail(
                HttpStatus.CONFLICT,
                "La tienda tiene trabajadores asignados",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation() {
        return createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos",
                "La petición viola una restricción de integridad de datos"

        );
    }

    @ExceptionHandler(ExternalApiException.class)
    public ProblemDetail handleExternalApi(ExternalApiException exception) {
        return createProblemDetail(
                HttpStatus.BAD_GATEWAY,
                "Error al consultar la API de tiendas",
                exception.getMessage()
        );
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
