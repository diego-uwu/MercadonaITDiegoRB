package es.MercadonaITDiegoRB.exception;

public class ExternalApiException extends RuntimeException {

    public ExternalApiException(Long tiendaId, Throwable cause) {
        super(
                "No se pudo obtener la información externa de la tienda " + tiendaId,
                cause
        );
    }

    public ExternalApiException(Long tiendaId) {
        super("La API externa no devolvió información para la tienda " + tiendaId);
    }
}
