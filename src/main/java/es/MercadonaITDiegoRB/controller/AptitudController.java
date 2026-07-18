package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.api.AptitudesApi;
import es.MercadonaITDiegoRB.dto.AptitudTrabajadorDto;
import es.MercadonaITDiegoRB.service.AptitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AptitudController implements AptitudesApi {

    private final AptitudService aptitudService;

    @Override
    public ResponseEntity<AptitudTrabajadorDto> addAptitudToTrabajador(String DNI, String aptitud) {
        AptitudTrabajadorDto created = aptitudService.addAptitud(DNI, aptitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<Void> deleteAptitudFromTrabajador(String DNI, String aptitud) {
        aptitudService.deleteAptitud(DNI, aptitud);
        return ResponseEntity.noContent().build();
    }
}
