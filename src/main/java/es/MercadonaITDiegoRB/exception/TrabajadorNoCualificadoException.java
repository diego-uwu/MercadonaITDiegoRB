package es.MercadonaITDiegoRB.exception;

import java.util.List;

public class TrabajadorNoCualificadoException extends RuntimeException {

    private final List<String> aptitudesFaltantes;

    public TrabajadorNoCualificadoException(
            String dni,
            String seccion,
            List<String> aptitudesFaltantes
    ) {
        super("El trabajador " + dni + " no está cualificado para la sección " + seccion
                + ". Aptitudes faltantes: " + String.join(", ", aptitudesFaltantes));
        this.aptitudesFaltantes = List.copyOf(aptitudesFaltantes);
    }

    public List<String> getAptitudesFaltantes() {
        return aptitudesFaltantes;
    }
}
