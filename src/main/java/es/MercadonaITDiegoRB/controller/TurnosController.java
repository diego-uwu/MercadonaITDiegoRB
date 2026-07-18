package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.api.TurnosApi;
import es.MercadonaITDiegoRB.dto.TurnoDto;
import es.MercadonaITDiegoRB.service.TurnoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class TurnosController implements TurnosApi {

    private final TurnoService turnoService;

    @Override
    public ResponseEntity<List<TurnoDto>> getTurnosByTrabajador(String DNI) {
        return ResponseEntity.ok(turnoService.getTurnosByTrabajador(DNI));
    }

    @Override
    public ResponseEntity<Void> turnoDNISeccionDelete(String DNI, String seccion) {
        turnoService.deleteTurno(DNI, seccion);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<TurnoDto> upsertTurno(TurnoDto turnoDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(turnoService.saveTurno(turnoDto));
    }
}
