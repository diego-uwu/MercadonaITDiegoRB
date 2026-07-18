package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.dto.TurnoDto;
import es.MercadonaITDiegoRB.entity.TrabajadorEntity;
import es.MercadonaITDiegoRB.entity.TurnoEntity;
import es.MercadonaITDiegoRB.entity.TurnoId;
import es.MercadonaITDiegoRB.exception.HorasDisponiblesExceededException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.mapper.TurnoMapper;
import es.MercadonaITDiegoRB.repository.TrabajadorRepository;
import es.MercadonaITDiegoRB.repository.TurnoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
@AllArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final TurnoMapper turnoMapper;

    @Transactional(readOnly = true)
    public List<TurnoDto> getTurnosByTrabajador(String dni) {
        if (!trabajadorRepository.existsById(dni)) {
            throw new ResourceNotFoundException("Trabajador", dni);
        }

        return turnoRepository.findAllByIdTrabajadorOrderByIdSeccionAsc(dni)
                .stream()
                .map(turnoMapper::toDto)
                .toList();
    }

    @Transactional
    public TurnoDto saveTurno(TurnoDto turnoDto) {
        String dni = turnoDto.getDniTrabajador();
        String seccion = turnoDto.getSeccion();
        TrabajadorEntity trabajador = getTrabajadorForUpdate(dni);
        TurnoId turnoId = new TurnoId(dni, trabajador.getTienda(), seccion);

        Optional<TurnoEntity> existingTurno = turnoRepository.findById(turnoId);
        long horasActuales = turnoRepository.sumHorasAsignadasByTrabajador(dni);
        long horasAnteriores = existingTurno
                .map(TurnoEntity::getHorasAsignadas)
                .orElse(0);
        long horasResultantes = horasActuales
                - horasAnteriores
                + turnoDto.getHoras();

        if (horasResultantes > trabajador.getHorasDisponibles()) {
            throw new HorasDisponiblesExceededException(
                    dni,
                    trabajador.getHorasDisponibles(),
                    horasResultantes
            );
        }

        TurnoEntity turno = existingTurno.orElseGet(() -> TurnoEntity.builder()
                .id(turnoId)
                .build());
        turno.setHorasAsignadas(turnoDto.getHoras());

        return turnoMapper.toDto(turnoRepository.saveAndFlush(turno));
    }

    @Transactional
    public void deleteTurno(String dni, String seccion) {
        TrabajadorEntity trabajador = getTrabajadorForUpdate(dni);
        TurnoId turnoId = new TurnoId(dni, trabajador.getTienda(), seccion);

        if (!turnoRepository.existsById(turnoId)) {
            throw new ResourceNotFoundException("Turno", turnoId);
        }

        turnoRepository.deleteById(turnoId);
    }

    private TrabajadorEntity getTrabajadorForUpdate(String dni) {
        return trabajadorRepository.findByIdForUpdate(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador", dni));
    }
}
