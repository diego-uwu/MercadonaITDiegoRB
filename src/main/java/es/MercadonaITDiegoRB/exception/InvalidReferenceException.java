package es.MercadonaITDiegoRB.exception;

public class InvalidReferenceException extends RuntimeException {

    public InvalidReferenceException(String resource, String field, Object value) {
        super(resource + " intenta referenciar un(a) " + field + " que no existe: " + value);
    }
}
