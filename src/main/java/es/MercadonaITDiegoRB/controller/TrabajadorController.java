package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.api.TrabajadorApi;
import es.MercadonaITDiegoRB.dto.TrabajadorDto;
import es.MercadonaITDiegoRB.service.TrabajadorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class TrabajadorController implements TrabajadorApi {

    private final TrabajadorService trabajadorService;

    @Override
    public ResponseEntity<Void> deleteTrabajador(String DNI) {
        trabajadorService.deleteTrabajador(DNI);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    public ResponseEntity<TrabajadorDto> getTrabajador(String DNI) {
        return ResponseEntity.ok(trabajadorService.getTrabajadorByDNI(DNI));
    }

    @Override
    public ResponseEntity<TrabajadorDto> updateTrabajador(TrabajadorDto trabajadorDto) {
        return ResponseEntity.ok(trabajadorService.updateTrabajador(trabajadorDto));
    }

    @Override
    public ResponseEntity<TrabajadorDto> insertTrabajador(TrabajadorDto trabajadorDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(trabajadorService.insertTrabajador(trabajadorDto));
    }
}
