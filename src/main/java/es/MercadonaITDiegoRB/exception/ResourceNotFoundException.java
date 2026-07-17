package es.MercadonaITDiegoRB.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " con identificador " + id + " no encontrado");
    }
}
