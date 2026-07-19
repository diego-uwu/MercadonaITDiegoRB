package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.dto.SeccionDetalleDto;
import es.MercadonaITDiegoRB.dto.TiendaDetalleDto;
import es.MercadonaITDiegoRB.dto.TiendaDto;
import es.MercadonaITDiegoRB.entity.TiendaEntity;
import es.MercadonaITDiegoRB.exception.ResourceAlreadyExistsException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.exception.TiendaConTrabajadoresException;
import es.MercadonaITDiegoRB.mapper.TiendaMapper;
import es.MercadonaITDiegoRB.repository.TiendaRepository;
import es.MercadonaITDiegoRB.repository.TrabajadorRepository;
import es.MercadonaITDiegoRB.repository.projection.TiendaDetalleRow;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional(readOnly = true)
    public TiendaDetalleDto getTienda(Long tiendaId) {
        TiendaEntity tienda = tiendaRepository.findById(tiendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda", tiendaId));

        Map<String, SeccionDetalleDto> secciones = new LinkedHashMap<>();
        List<TiendaDetalleRow> rows = tiendaRepository.findDetalleRows(tiendaId);

        for (TiendaDetalleRow row : rows) {
            SeccionDetalleDto seccion = secciones.computeIfAbsent(
                    row.getSeccion(),
                    nombre -> new SeccionDetalleDto()
                            .nombre(nombre)
                            .horasNecesarias(row.getHorasNecesarias())
                            .aptitudes(new ArrayList<>())
            );

            if (row.getAptitud() != null) {
                seccion.getAptitudes().add(row.getAptitud());
            }
        }

        return new TiendaDetalleDto()
                .codigo(tienda.getCodigo())
                .nombre(tienda.getNombre())
                .secciones(new ArrayList<>(secciones.values()));
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
