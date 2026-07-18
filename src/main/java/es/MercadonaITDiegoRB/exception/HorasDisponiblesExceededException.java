package es.MercadonaITDiegoRB.exception;

public class HorasDisponiblesExceededException extends RuntimeException {

    public HorasDisponiblesExceededException(
            String dni,
            long horasDisponibles,
            long horasResultantes
    ) {
        super(
                "El turno dejaría al trabajador " + dni
                        + " con " + horasResultantes
                        + " horas asignadas, superando sus "
                        + horasDisponibles + " horas disponibles"
        );
    }
}
