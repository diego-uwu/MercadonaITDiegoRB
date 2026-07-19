package es.MercadonaITDiegoRB.exception;

public class HorasDisponiblesExceededException extends RuntimeException {

    public HorasDisponiblesExceededException(
            String dni,
            long horasDisponibles,
            long horasResultantes
    ) {
        super(
                "El trabajador " + dni
                        + " tendría " + horasResultantes
                        + " horas asignadas, superando sus "
                        + horasDisponibles + " horas disponibles"
        );
    }
}
