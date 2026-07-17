package es.MercadonaITDiegoRB.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String resource, String field, Object value) {
        super(resource + " con " + field + " " + value + " ya existe");
    }
}
