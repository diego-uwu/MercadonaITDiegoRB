package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.dto.TurnoDto;
import es.MercadonaITDiegoRB.entity.TrabajadorEntity;
import es.MercadonaITDiegoRB.entity.TurnoEntity;
import es.MercadonaITDiegoRB.entity.TurnoId;
import es.MercadonaITDiegoRB.exception.HorasDisponiblesExceededException;
import es.MercadonaITDiegoRB.mapper.TurnoMapper;
import es.MercadonaITDiegoRB.repository.TrabajadorRepository;
import es.MercadonaITDiegoRB.repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    private TurnoMapper turnoMapper;

    private TurnoService turnoService;
    private TrabajadorEntity trabajador;
    private TurnoId turnoId;

    @BeforeEach
    void setUp() {
        turnoService = new TurnoService(
                turnoRepository,
                trabajadorRepository,
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
    void rejectsNewTurnoWhenResultingHoursExceedAvailableHours() {
        mockTrabajadorForUpdate();
        TurnoDto request = new TurnoDto()
                .dniTrabajador(DNI)
                .seccion(SECCION)
                .horas(2);
        when(turnoRepository.findById(turnoId)).thenReturn(Optional.empty());
        when(turnoRepository.sumHorasAsignadasByTrabajador(DNI)).thenReturn(8L);

        assertThrows(
                HorasDisponiblesExceededException.class,
                () -> turnoService.saveTurno(request)
        );

        verify(turnoRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updatesExistingTurnoWhenReducingItsHours() {
        mockTrabajadorForUpdate();
        TurnoDto request = new TurnoDto()
                .dniTrabajador(DNI)
                .seccion(SECCION)
                .horas(2);
        TurnoEntity existingTurno = TurnoEntity.builder()
                .id(turnoId)
                .horasAsignadas(4)
                .build();
        TurnoDto response = new TurnoDto()
                .dniTrabajador(DNI)
                .tienda(TIENDA)
                .seccion(SECCION)
                .horas(2);

        when(turnoRepository.findById(turnoId))
                .thenReturn(Optional.of(existingTurno));
        when(turnoRepository.sumHorasAsignadasByTrabajador(DNI)).thenReturn(8L);
        when(turnoRepository.saveAndFlush(existingTurno)).thenReturn(existingTurno);
        when(turnoMapper.toDto(existingTurno)).thenReturn(response);

        TurnoDto result = turnoService.saveTurno(request);

        assertEquals(2, existingTurno.getHorasAsignadas());
        assertEquals(2, result.getHoras());
        verify(turnoRepository).saveAndFlush(existingTurno);
    }

    @Test
    void returnsAllTurnosForExistingTrabajador() {
        TurnoEntity horno = TurnoEntity.builder()
                .id(turnoId)
                .horasAsignadas(4)
                .build();
        TurnoEntity pescaderia = TurnoEntity.builder()
                .id(new TurnoId(DNI, TIENDA, "Pescaderia"))
                .horasAsignadas(4)
                .build();
        TurnoDto hornoDto = new TurnoDto().seccion(SECCION).horas(4);
        TurnoDto pescaderiaDto = new TurnoDto().seccion("Pescaderia").horas(4);

        when(trabajadorRepository.existsById(DNI)).thenReturn(true);
        when(turnoRepository.findAllByIdTrabajadorOrderByIdSeccionAsc(DNI))
                .thenReturn(List.of(horno, pescaderia));
        when(turnoMapper.toDto(horno)).thenReturn(hornoDto);
        when(turnoMapper.toDto(pescaderia)).thenReturn(pescaderiaDto);

        List<TurnoDto> result = turnoService.getTurnosByTrabajador(DNI);

        assertEquals(List.of(hornoDto, pescaderiaDto), result);
    }

    private void mockTrabajadorForUpdate() {
        when(trabajadorRepository.findByIdForUpdate(DNI))
                .thenReturn(Optional.of(trabajador));
    }
}
