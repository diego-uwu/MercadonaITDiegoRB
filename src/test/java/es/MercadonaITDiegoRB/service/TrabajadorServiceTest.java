package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.dto.TrabajadorDto;
import es.MercadonaITDiegoRB.entity.TrabajadorEntity;
import es.MercadonaITDiegoRB.exception.InvalidReferenceException;
import es.MercadonaITDiegoRB.exception.ResourceAlreadyExistsException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.mapper.TrabajadorMapper;
import es.MercadonaITDiegoRB.repository.TiendaRepository;
import es.MercadonaITDiegoRB.repository.TrabajadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrabajadorServiceTest {

    private static final String DNI = "12345678A";
    private static final long TIENDA = 1L;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private TiendaRepository tiendaRepository;

    @Mock
    private TrabajadorMapper trabajadorMapper;

    private TrabajadorService trabajadorService;
    private TrabajadorDto trabajadorDto;
    private TrabajadorEntity trabajadorEntity;

    @BeforeEach
    void setUp() {
        trabajadorService = new TrabajadorService(
                trabajadorRepository,
                tiendaRepository,
                trabajadorMapper
        );
        trabajadorDto = new TrabajadorDto()
                .dni(DNI)
                .nombre("Diego")
                .apellidos("Rodríguez Barrera")
                .tienda(TIENDA)
                .horasDisponibles(8);
        trabajadorEntity = TrabajadorEntity.builder()
                .dni(DNI)
                .nombre("Diego")
                .apellidos("Rodríguez Barrera")
                .tienda(TIENDA)
                .horasDisponibles(8)
                .build();
    }

    @Test
    void returnsTrabajadorWhenDniExists() {
        when(trabajadorRepository.findById(DNI))
                .thenReturn(Optional.of(trabajadorEntity));
        when(trabajadorMapper.toDto(trabajadorEntity)).thenReturn(trabajadorDto);

        TrabajadorDto result = trabajadorService.getTrabajadorByDNI(DNI);

        assertSame(trabajadorDto, result);
        verify(trabajadorMapper).toDto(trabajadorEntity);
    }

    @Test
    void throwsWhenGettingUnknownTrabajador() {
        when(trabajadorRepository.findById(DNI)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> trabajadorService.getTrabajadorByDNI(DNI)
        );

        assertEquals(
                "Trabajador con identificador " + DNI + " no encontrado",
                exception.getMessage()
        );
        verifyNoInteractions(trabajadorMapper);
    }

    @Test
    void insertsTrabajadorWhenDniAndTiendaAreValid() {
        when(trabajadorRepository.existsById(DNI)).thenReturn(false);
        when(tiendaRepository.existsById(TIENDA)).thenReturn(true);
        when(trabajadorMapper.toEntity(trabajadorDto)).thenReturn(trabajadorEntity);
        when(trabajadorRepository.saveAndFlush(trabajadorEntity))
                .thenReturn(trabajadorEntity);
        when(trabajadorMapper.toDto(trabajadorEntity)).thenReturn(trabajadorDto);

        TrabajadorDto result = trabajadorService.insertTrabajador(trabajadorDto);

        assertSame(trabajadorDto, result);
        verify(trabajadorRepository).saveAndFlush(trabajadorEntity);
    }

    @Test
    void rejectsInsertWhenDniAlreadyExists() {
        when(trabajadorRepository.existsById(DNI)).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> trabajadorService.insertTrabajador(trabajadorDto)
        );

        assertEquals(
                "Trabajador con DNI " + DNI + " ya existe",
                exception.getMessage()
        );
        verifyNoInteractions(tiendaRepository, trabajadorMapper);
        verify(trabajadorRepository, never()).saveAndFlush(trabajadorEntity);
    }

    @Test
    void rejectsInsertWhenTiendaDoesNotExist() {
        when(trabajadorRepository.existsById(DNI)).thenReturn(false);
        when(tiendaRepository.existsById(TIENDA)).thenReturn(false);

        InvalidReferenceException exception = assertThrows(
                InvalidReferenceException.class,
                () -> trabajadorService.insertTrabajador(trabajadorDto)
        );

        assertEquals(
                "Trabajador intenta referenciar un(a) tienda que no existe: " + TIENDA,
                exception.getMessage()
        );
        verifyNoInteractions(trabajadorMapper);
        verify(trabajadorRepository, never()).saveAndFlush(trabajadorEntity);
    }

    @Test
    void deletesTrabajadorWhenDniExists() {
        when(trabajadorRepository.existsById(DNI)).thenReturn(true);

        trabajadorService.deleteTrabajador(DNI);

        verify(trabajadorRepository).deleteById(DNI);
    }

    @Test
    void rejectsDeleteWhenTrabajadorDoesNotExist() {
        when(trabajadorRepository.existsById(DNI)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> trabajadorService.deleteTrabajador(DNI)
        );

        verify(trabajadorRepository, never()).deleteById(DNI);
    }

    @Test
    void updatesTrabajadorWhenDniAndTiendaAreValid() {
        when(trabajadorRepository.existsById(DNI)).thenReturn(true);
        when(tiendaRepository.existsById(TIENDA)).thenReturn(true);
        when(trabajadorMapper.toEntity(trabajadorDto)).thenReturn(trabajadorEntity);
        when(trabajadorRepository.saveAndFlush(trabajadorEntity))
                .thenReturn(trabajadorEntity);
        when(trabajadorMapper.toDto(trabajadorEntity)).thenReturn(trabajadorDto);

        TrabajadorDto result = trabajadorService.updateTrabajador(trabajadorDto);

        assertSame(trabajadorDto, result);
        verify(trabajadorRepository).saveAndFlush(trabajadorEntity);
    }

    @Test
    void rejectsUpdateWhenTrabajadorDoesNotExist() {
        when(trabajadorRepository.existsById(DNI)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> trabajadorService.updateTrabajador(trabajadorDto)
        );

        verifyNoInteractions(tiendaRepository, trabajadorMapper);
        verify(trabajadorRepository, never()).saveAndFlush(trabajadorEntity);
    }

    @Test
    void rejectsUpdateWhenTiendaDoesNotExist() {
        when(trabajadorRepository.existsById(DNI)).thenReturn(true);
        when(tiendaRepository.existsById(TIENDA)).thenReturn(false);

        assertThrows(
                InvalidReferenceException.class,
                () -> trabajadorService.updateTrabajador(trabajadorDto)
        );

        verifyNoInteractions(trabajadorMapper);
        verify(trabajadorRepository, never()).saveAndFlush(trabajadorEntity);
    }
}
