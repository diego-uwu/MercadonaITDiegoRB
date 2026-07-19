package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.dto.AptitudTrabajadorDto;
import es.MercadonaITDiegoRB.entity.AptitudTrabajadorEntity;
import es.MercadonaITDiegoRB.entity.AptitudTrabajadorId;
import es.MercadonaITDiegoRB.exception.InvalidReferenceException;
import es.MercadonaITDiegoRB.exception.ResourceAlreadyExistsException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.mapper.AptitudTrabajadorMapper;
import es.MercadonaITDiegoRB.repository.AptitudRepository;
import es.MercadonaITDiegoRB.repository.TrabajadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AptitudServiceTest {

    private static final String DNI = "12345678A";
    private static final String APTITUD = "Hornear Pan";

    @Mock
    private AptitudRepository aptitudRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private AptitudTrabajadorMapper mapper;

    private AptitudService service;
    private AptitudTrabajadorId id;

    @BeforeEach
    void setUp() {
        service = new AptitudService(
                aptitudRepository,
                trabajadorRepository,
                mapper
        );
        id = new AptitudTrabajadorId(DNI, APTITUD);
    }

    @Test
    void addsAptitudAndReturnsMappedDto() {
        AptitudTrabajadorEntity saved = new AptitudTrabajadorEntity(id);
        AptitudTrabajadorDto response = new AptitudTrabajadorDto()
                .dniTrabajador(DNI)
                .aptitud(APTITUD);
        when(aptitudRepository.existsById(id)).thenReturn(false);
        when(trabajadorRepository.existsById(DNI)).thenReturn(true);
        when(aptitudRepository.countByAptitudNombre(APTITUD)).thenReturn(1L);
        when(aptitudRepository.save(any(AptitudTrabajadorEntity.class))).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(response);

        AptitudTrabajadorDto result = service.addAptitud(DNI, APTITUD);

        assertSame(response, result);
        verify(aptitudRepository).save(any(AptitudTrabajadorEntity.class));
        verify(mapper).toDto(saved);
    }

    @Test
    void rejectsDuplicateAssignment() {
        when(aptitudRepository.existsById(id)).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.addAptitud(DNI, APTITUD)
        );

        verifyNoInteractions(trabajadorRepository, mapper);
        verify(aptitudRepository, never()).save(any(AptitudTrabajadorEntity.class));
    }

    @Test
    void rejectsUnknownTrabajador() {
        when(aptitudRepository.existsById(id)).thenReturn(false);
        when(trabajadorRepository.existsById(DNI)).thenReturn(false);

        assertThrows(
                InvalidReferenceException.class,
                () -> service.addAptitud(DNI, APTITUD)
        );

        verify(aptitudRepository, never()).save(any(AptitudTrabajadorEntity.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void rejectsUnknownAptitud() {
        when(aptitudRepository.existsById(id)).thenReturn(false);
        when(trabajadorRepository.existsById(DNI)).thenReturn(true);
        when(aptitudRepository.countByAptitudNombre(APTITUD)).thenReturn(0L);

        assertThrows(
                InvalidReferenceException.class,
                () -> service.addAptitud(DNI, APTITUD)
        );

        verify(aptitudRepository, never()).save(any(AptitudTrabajadorEntity.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void deletesExistingAptitudAssignment() {
        when(aptitudRepository.existsById(id)).thenReturn(true);

        service.deleteAptitud(DNI, APTITUD);

        verify(aptitudRepository).deleteById(id);
    }

    @Test
    void rejectsDeleteWhenAssignmentDoesNotExist() {
        when(aptitudRepository.existsById(id)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteAptitud(DNI, APTITUD)
        );

        verify(aptitudRepository, never()).deleteById(any());
    }
}
