package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.dto.TurnoDto;
import es.MercadonaITDiegoRB.entity.TrabajadorEntity;
import es.MercadonaITDiegoRB.entity.TurnoEntity;
import es.MercadonaITDiegoRB.entity.TurnoId;
import es.MercadonaITDiegoRB.exception.HorasDisponiblesExceededException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.exception.TrabajadorNoCualificadoException;
import es.MercadonaITDiegoRB.mapper.TurnoMapper;
import es.MercadonaITDiegoRB.repository.AptitudRepository;
import es.MercadonaITDiegoRB.repository.TrabajadorRepository;
import es.MercadonaITDiegoRB.repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    private static final String DNI = "12345678A";
    private static final String SECCION = "Horno";
    private static final long TIENDA = 1L;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private AptitudRepository aptitudRepository;

    @Mock
    private TurnoMapper turnoMapper;

    private TurnoService turnoService;
    private TrabajadorEntity trabajador;
    private TurnoId turnoId;

    @BeforeEach
    void setUp() {
        turnoService = new TurnoService(
                turnoRepository,
                trabajadorRepository,
                aptitudRepository,
                turnoMapper
        );
        trabajador = TrabajadorEntity.builder()
                .dni(DNI)
                .tienda(TIENDA)
                .horasDisponibles(8)
                .build();
        turnoId = new TurnoId(DNI, TIENDA, SECCION);
    }

    @Test
    void returnsAllTurnosForExistingTrabajador() {
        TurnoEntity horno = turno(SECCION, 4);
        TurnoEntity pescaderia = turno("Pescaderia", 4);
        TurnoDto hornoDto = turnoDto(SECCION, 4);
        TurnoDto pescaderiaDto = turnoDto("Pescaderia", 4);

        when(trabajadorRepository.existsById(DNI)).thenReturn(true);
        when(turnoRepository.findAllByIdTrabajadorOrderByIdSeccionAsc(DNI))
                .thenReturn(List.of(horno, pescaderia));
        when(turnoMapper.toDto(horno)).thenReturn(hornoDto);
        when(turnoMapper.toDto(pescaderia)).thenReturn(pescaderiaDto);

        List<TurnoDto> result = turnoService.getTurnosByTrabajador(DNI);

        assertEquals(List.of(hornoDto, pescaderiaDto), result);
    }

    @Test
    void returnsEmptyListWhenExistingTrabajadorHasNoTurnos() {
        when(trabajadorRepository.existsById(DNI)).thenReturn(true);
        when(turnoRepository.findAllByIdTrabajadorOrderByIdSeccionAsc(DNI))
                .thenReturn(List.of());

        List<TurnoDto> result = turnoService.getTurnosByTrabajador(DNI);

        assertTrue(result.isEmpty());
        verifyNoInteractions(turnoMapper);
    }

    @Test
    void rejectsGetTurnosWhenTrabajadorDoesNotExist() {
        when(trabajadorRepository.existsById(DNI)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> turnoService.getTurnosByTrabajador(DNI)
        );

        verifyNoInteractions(turnoRepository, turnoMapper);
    }

    @Test
    void insertsNewTurnoBelowAvailableHours() {
        TurnoDto request = turnoDto(SECCION, 2);
        TurnoDto response = turnoDto(SECCION, 2).tienda(TIENDA);
        mockTrabajadorForUpdate();
        when(turnoRepository.findById(turnoId)).thenReturn(Optional.empty());
        when(turnoRepository.sumHorasAsignadasByTrabajador(DNI)).thenReturn(4L);
        when(turnoRepository.saveAndFlush(any(TurnoEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(turnoMapper.toDto(any(TurnoEntity.class))).thenReturn(response);

        TurnoDto result = turnoService.saveTurno(request);

        ArgumentCaptor<TurnoEntity> captor = ArgumentCaptor.forClass(TurnoEntity.class);
        verify(turnoRepository).saveAndFlush(captor.capture());
        assertEquals(turnoId, captor.getValue().getId());
        assertEquals(2, captor.getValue().getHorasAsignadas());
        assertSame(response, result);
    }

    @Test
    void insertsNewTurnoAtExactAvailableHoursBoundary() {
        TurnoDto request = turnoDto(SECCION, 2);
        TurnoDto response = turnoDto(SECCION, 2);
        mockTrabajadorForUpdate();
        when(turnoRepository.findById(turnoId)).thenReturn(Optional.empty());
        when(turnoRepository.sumHorasAsignadasByTrabajador(DNI)).thenReturn(6L);
        when(turnoRepository.saveAndFlush(any(TurnoEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(turnoMapper.toDto(any(TurnoEntity.class))).thenReturn(response);

        TurnoDto result = turnoService.saveTurno(request);

        assertSame(response, result);
        verify(turnoRepository).saveAndFlush(any(TurnoEntity.class));
    }

    @Test
    void rejectsNewTurnoWhenResultingHoursExceedAvailableHours() {
        TurnoDto request = turnoDto(SECCION, 2);
        mockTrabajadorForUpdate();
        when(turnoRepository.findById(turnoId)).thenReturn(Optional.empty());
        when(turnoRepository.sumHorasAsignadasByTrabajador(DNI)).thenReturn(8L);

        HorasDisponiblesExceededException exception = assertThrows(
                HorasDisponiblesExceededException.class,
                () -> turnoService.saveTurno(request)
        );

        assertTrue(exception.getMessage().contains("10 horas asignadas"));
        verify(turnoRepository, never()).saveAndFlush(any());
        verifyNoInteractions(turnoMapper);
    }

    @Test
    void rejectsTurnoWhenTrabajadorIsMissingRequiredAptitudes() {
        TurnoDto request = turnoDto(SECCION, 2);
        mockTrabajadorForUpdate();
        when(aptitudRepository.findAptitudesFaltantes(DNI, SECCION))
                .thenReturn(List.of("Hornear Pan", "Repostería"));

        TrabajadorNoCualificadoException exception = assertThrows(
                TrabajadorNoCualificadoException.class,
                () -> turnoService.saveTurno(request)
        );

        assertEquals(List.of("Hornear Pan", "Repostería"), exception.getAptitudesFaltantes());
        assertTrue(exception.getMessage().contains("Hornear Pan, Repostería"));
        verifyNoInteractions(turnoRepository, turnoMapper);
    }

    @Test
    void updatesExistingTurnoWhenReducingItsHours() {
        TurnoDto request = turnoDto(SECCION, 2);
        TurnoEntity existingTurno = turno(SECCION, 4);
        TurnoDto response = turnoDto(SECCION, 2);
        mockExistingTurno(existingTurno, 8L);
        when(turnoRepository.saveAndFlush(existingTurno)).thenReturn(existingTurno);
        when(turnoMapper.toDto(existingTurno)).thenReturn(response);

        TurnoDto result = turnoService.saveTurno(request);

        assertEquals(2, existingTurno.getHorasAsignadas());
        assertSame(response, result);
        verify(turnoRepository).saveAndFlush(existingTurno);
    }

    @Test
    void updatesExistingTurnoAtExactAvailableHoursBoundary() {
        TurnoDto request = turnoDto(SECCION, 4);
        TurnoEntity existingTurno = turno(SECCION, 2);
        TurnoDto response = turnoDto(SECCION, 4);
        mockExistingTurno(existingTurno, 6L);
        when(turnoRepository.saveAndFlush(existingTurno)).thenReturn(existingTurno);
        when(turnoMapper.toDto(existingTurno)).thenReturn(response);

        TurnoDto result = turnoService.saveTurno(request);

        assertEquals(4, existingTurno.getHorasAsignadas());
        assertSame(response, result);
    }

    @Test
    void rejectsExistingTurnoUpdateWhenResultingHoursExceedAvailableHours() {
        TurnoDto request = turnoDto(SECCION, 5);
        TurnoEntity existingTurno = turno(SECCION, 4);
        mockExistingTurno(existingTurno, 8L);

        assertThrows(
                HorasDisponiblesExceededException.class,
                () -> turnoService.saveTurno(request)
        );

        assertEquals(4, existingTurno.getHorasAsignadas());
        verify(turnoRepository, never()).saveAndFlush(any());
        verifyNoInteractions(turnoMapper);
    }

    @Test
    void rejectsSaveWhenTrabajadorDoesNotExist() {
        TurnoDto request = turnoDto(SECCION, 2);
        when(trabajadorRepository.findByIdForUpdate(DNI))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> turnoService.saveTurno(request)
        );

        verifyNoInteractions(turnoRepository, turnoMapper);
    }

    @Test
    void deletesExistingTurno() {
        mockTrabajadorForUpdate();
        when(turnoRepository.existsById(turnoId)).thenReturn(true);

        turnoService.deleteTurno(DNI, SECCION);

        verify(turnoRepository).deleteById(turnoId);
    }

    @Test
    void rejectsDeleteWhenTurnoDoesNotExist() {
        mockTrabajadorForUpdate();
        when(turnoRepository.existsById(turnoId)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> turnoService.deleteTurno(DNI, SECCION)
        );

        verify(turnoRepository, never()).deleteById(any());
    }

    @Test
    void rejectsDeleteWhenTrabajadorDoesNotExist() {
        when(trabajadorRepository.findByIdForUpdate(DNI))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> turnoService.deleteTurno(DNI, SECCION)
        );

        verifyNoInteractions(turnoRepository, turnoMapper);
    }

    private void mockTrabajadorForUpdate() {
        when(trabajadorRepository.findByIdForUpdate(DNI))
                .thenReturn(Optional.of(trabajador));
    }

    private void mockExistingTurno(TurnoEntity existingTurno, long horasActuales) {
        mockTrabajadorForUpdate();
        when(turnoRepository.findById(turnoId))
                .thenReturn(Optional.of(existingTurno));
        when(turnoRepository.sumHorasAsignadasByTrabajador(DNI))
                .thenReturn(horasActuales);
    }

    private TurnoEntity turno(String seccion, int horas) {
        return TurnoEntity.builder()
                .id(new TurnoId(DNI, TIENDA, seccion))
                .horasAsignadas(horas)
                .build();
    }

    private TurnoDto turnoDto(String seccion, int horas) {
        return new TurnoDto()
                .dniTrabajador(DNI)
                .seccion(seccion)
                .horas(horas);
    }
}
