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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AptitudService {

    private final AptitudRepository aptitudRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final AptitudTrabajadorMapper aptitudTrabajadorMapper;

    public AptitudTrabajadorDto addAptitud(String DNI, String aptitud) {
        AptitudTrabajadorId id = new AptitudTrabajadorId(DNI, aptitud);

        if (aptitudRepository.existsById(id)) {
            throw new ResourceAlreadyExistsException(
                    "Trabajador",
                    "DNI y aptitud",
                    id.getTrabajador() + " y " + id.getAptitud()
            );
        }

        if (!trabajadorRepository.existsById(DNI)){
            throw new InvalidReferenceException(
                    "La aptitud",
                    "DNI de trabajador",
                    DNI
            );
        }

        if (aptitudRepository.countByAptitudNombre(aptitud) == 0) {
            throw new InvalidReferenceException(
                    "El trabajador",
                    "aptitud",
                    aptitud
            );
        }

        AptitudTrabajadorEntity saved = aptitudRepository.save(
                new AptitudTrabajadorEntity(id)
        );

        return aptitudTrabajadorMapper.toDto(saved);
    }

    public void deleteAptitud(String DNI, String aptitud) {
        AptitudTrabajadorId id = new AptitudTrabajadorId(DNI, aptitud);

        if (!aptitudRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aptitud del trabajador", id.getTrabajador() + " y " + id.getAptitud());
        }

        aptitudRepository.deleteById(id);
    }
}
