package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.dto.TiendaDto;
import es.MercadonaITDiegoRB.entity.TiendaEntity;
import es.MercadonaITDiegoRB.exception.ResourceAlreadyExistsException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.exception.TiendaConTrabajadoresException;
import es.MercadonaITDiegoRB.mapper.TiendaMapper;
import es.MercadonaITDiegoRB.repository.TiendaRepository;
import es.MercadonaITDiegoRB.repository.TrabajadorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TiendaService {

    private final TiendaRepository tiendaRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final TiendaMapper tiendaMapper;

    @Transactional
    public TiendaDto insertTienda(TiendaDto tiendaDto) {
        Long tiendaId = tiendaDto.getCodigo();
        if (tiendaRepository.existsById(tiendaId)) {
            throw new ResourceAlreadyExistsException("Tienda", "código", tiendaId);
        }

        TiendaEntity tienda = tiendaRepository.saveAndFlush(tiendaMapper.toEntity(tiendaDto));
        tiendaRepository.insertAllSecciones(tiendaId);
        return tiendaMapper.toDto(tienda);
    }

    @Transactional
    public void deleteTienda(Long tiendaId) {
        TiendaEntity tienda = tiendaRepository.findById(tiendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda", tiendaId));

        if (trabajadorRepository.existsByTienda(tiendaId)) {
            throw new TiendaConTrabajadoresException(tiendaId);
        }

        tiendaRepository.deleteSecciones(tiendaId);
        tiendaRepository.delete(tienda);
    }
}
