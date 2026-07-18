package es.MercadonaITDiegoRB.controller;

import es.MercadonaITDiegoRB.api.TiendaApi;
import es.MercadonaITDiegoRB.dto.TiendaDto;
import es.MercadonaITDiegoRB.service.TiendaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TiendaController implements TiendaApi {

    private final TiendaService tiendaService;

    @Override
    public ResponseEntity<TiendaDto> insertTienda(TiendaDto tiendaDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tiendaService.insertTienda(tiendaDto));
    }

    @Override
    public ResponseEntity<Void> deleteTienda(Long tiendaId) {
        tiendaService.deleteTienda(tiendaId);
        return ResponseEntity.noContent().build();
    }
}
