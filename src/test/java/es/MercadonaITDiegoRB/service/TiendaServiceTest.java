package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.dto.TiendaDto;
import es.MercadonaITDiegoRB.entity.TiendaEntity;
import es.MercadonaITDiegoRB.exception.ResourceAlreadyExistsException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.exception.TiendaConTrabajadoresException;
import es.MercadonaITDiegoRB.mapper.TiendaMapper;
import es.MercadonaITDiegoRB.repository.TiendaRepository;
import es.MercadonaITDiegoRB.repository.TrabajadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TiendaServiceTest {

    private static final Long TIENDA_ID = 4L;

    @Mock
    private TiendaRepository tiendaRepository;
    @Mock
    private TrabajadorRepository trabajadorRepository;
    @Mock
    private TiendaMapper tiendaMapper;

    private TiendaService tiendaService;
    private TiendaDto tiendaDto;
    private TiendaEntity tiendaEntity;

    @BeforeEach
    void setUp() {
        tiendaService = new TiendaService(tiendaRepository, trabajadorRepository, tiendaMapper);
        tiendaDto = new TiendaDto().codigo(TIENDA_ID).nombre("Tienda 4");
        tiendaEntity = new TiendaEntity(TIENDA_ID, "Tienda 4");
    }

    @Test
    void insertCreatesTiendaAndAllItsSecciones() {
        when(tiendaRepository.existsById(TIENDA_ID)).thenReturn(false);
        when(tiendaMapper.toEntity(tiendaDto)).thenReturn(tiendaEntity);
        when(tiendaRepository.saveAndFlush(tiendaEntity)).thenReturn(tiendaEntity);
        when(tiendaMapper.toDto(tiendaEntity)).thenReturn(tiendaDto);

        TiendaDto result = tiendaService.insertTienda(tiendaDto);

        assertSame(tiendaDto, result);
        InOrder order = inOrder(tiendaRepository);
        order.verify(tiendaRepository).saveAndFlush(tiendaEntity);
        order.verify(tiendaRepository).insertAllSecciones(TIENDA_ID);
    }

    @Test
    void insertRejectsAnExistingCodigo() {
        when(tiendaRepository.existsById(TIENDA_ID)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> tiendaService.insertTienda(tiendaDto));

        verify(tiendaRepository, never()).saveAndFlush(tiendaEntity);
        verify(tiendaRepository, never()).insertAllSecciones(TIENDA_ID);
        verifyNoInteractions(trabajadorRepository, tiendaMapper);
    }

    @Test
    void deleteRemovesSeccionesBeforeTienda() {
        when(tiendaRepository.findById(TIENDA_ID)).thenReturn(Optional.of(tiendaEntity));
        when(trabajadorRepository.existsByTienda(TIENDA_ID)).thenReturn(false);

        tiendaService.deleteTienda(TIENDA_ID);

        InOrder order = inOrder(tiendaRepository);
        order.verify(tiendaRepository).deleteSecciones(TIENDA_ID);
        order.verify(tiendaRepository).delete(tiendaEntity);
    }

    @Test
    void deleteRejectsTiendaWithTrabajadoresWithoutDeletingAnything() {
        when(tiendaRepository.findById(TIENDA_ID)).thenReturn(Optional.of(tiendaEntity));
        when(trabajadorRepository.existsByTienda(TIENDA_ID)).thenReturn(true);

        assertThrows(TiendaConTrabajadoresException.class, () -> tiendaService.deleteTienda(TIENDA_ID));

        verify(tiendaRepository, never()).deleteSecciones(TIENDA_ID);
        verify(tiendaRepository, never()).delete(tiendaEntity);
    }

    @Test
    void deleteRejectsUnknownTienda() {
        when(tiendaRepository.findById(TIENDA_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tiendaService.deleteTienda(TIENDA_ID));

        verifyNoInteractions(trabajadorRepository);
        verify(tiendaRepository, never()).deleteSecciones(TIENDA_ID);
    }
}
