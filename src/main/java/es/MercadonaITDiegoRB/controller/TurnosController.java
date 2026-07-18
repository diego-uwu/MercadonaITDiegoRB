package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.api.TurnosApi;
import es.MercadonaITDiegoRB.dto.TurnoDto;
import org.springframework.http.ResponseEntity;

public class TurnosController implements TurnosApi {
    @Override
    public ResponseEntity<Void> turnoDNISeccionDelete(String DNI, String seccion) {
        return null;
    }

    @Override
    public ResponseEntity<TurnoDto> turnoDNISeccionPut(String DNI, String seccion, TurnoDto turnoDto) {
        return null;
    }
}
