package es.MercadonaITDiegoRB.service;

import es.MercadonaITDiegoRB.dto.TrabajadorDto;
import es.MercadonaITDiegoRB.exception.InvalidReferenceException;
import es.MercadonaITDiegoRB.exception.ResourceAlreadyExistsException;
import es.MercadonaITDiegoRB.exception.ResourceNotFoundException;
import es.MercadonaITDiegoRB.mapper.TrabajadorMapper;
import es.MercadonaITDiegoRB.repository.TiendaRepository;
import es.MercadonaITDiegoRB.repository.TrabajadorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final TiendaRepository tiendaRepository;
    private final TrabajadorMapper trabajadorMapper;

    public TrabajadorDto getTrabajadorByDNI(String dni) {
        return trabajadorRepository.findById(dni)
                .map(trabajadorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajador", dni));
    }

    @Transactional
    public TrabajadorDto insertTrabajador(TrabajadorDto trabajadorDto) {
        if (trabajadorRepository.existsById(trabajadorDto.getDni())) {
            throw new ResourceAlreadyExistsException(
                    "Trabajador",
                    "dni",
                    trabajadorDto.getDni()
            );
        }

        if (!tiendaRepository.existsById(trabajadorDto.getTienda())) {
            throw new InvalidReferenceException(
                    "Trabajador",
                    "tienda",
                    trabajadorDto.getTienda()
            );
        }

        return trabajadorMapper.toDto(
                trabajadorRepository.saveAndFlush(
                        trabajadorMapper.toEntity(trabajadorDto)
                )
        );
    }

    public void deleteTrabajador(String dni) {
        if (!trabajadorRepository.existsById(dni)) {
            throw new ResourceNotFoundException("Trabajador", dni);
        }

        trabajadorRepository.deleteById(dni);
    }

    public TrabajadorDto updateTrabajador(TrabajadorDto trabajadorDto){
        if (!trabajadorRepository.existsById(trabajadorDto.getDni())) {
            throw new ResourceNotFoundException("Trabajador", "dni");
        }

        if (!tiendaRepository.existsById(trabajadorDto.getTienda())) {
            throw new InvalidReferenceException(
                    "Trabajador",
                    "tienda",
                    trabajadorDto.getTienda()
            );
        }

        return trabajadorMapper.toDto(
                trabajadorRepository.saveAndFlush(
                        trabajadorMapper.toEntity(trabajadorDto)
                )
        );
    }
}
