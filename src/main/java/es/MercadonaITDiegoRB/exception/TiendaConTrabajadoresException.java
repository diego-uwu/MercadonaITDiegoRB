package es.MercadonaITDiegoRB.exception;

public class TiendaConTrabajadoresException extends RuntimeException {

    public TiendaConTrabajadoresException(Long tiendaId) {
        super("La tienda " + tiendaId + " no se puede eliminar porque todavía tiene trabajadores asignados");
    }
}
