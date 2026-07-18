package es.MercadonaITDiegoRB.mapper;

import es.MercadonaITDiegoRB.dto.AptitudTrabajadorDto;
import es.MercadonaITDiegoRB.entity.AptitudTrabajadorEntity;
import es.MercadonaITDiegoRB.entity.AptitudTrabajadorId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AptitudTrabajadorMapperTest {

    private final AptitudTrabajadorMapper mapper =
            Mappers.getMapper(AptitudTrabajadorMapper.class);

    @Test
    void mapsEmbeddedIdEntityToFlatDto() {
        AptitudTrabajadorEntity entity = new AptitudTrabajadorEntity(
                new AptitudTrabajadorId("12345678A", "Hornear Pan")
        );

        AptitudTrabajadorDto result = mapper.toDto(entity);

        assertEquals("12345678A", result.getDniTrabajador());
        assertEquals("Hornear Pan", result.getAptitud());
    }

    @Test
    void mapsFlatDtoToEntityWithEmbeddedId() {
        AptitudTrabajadorDto dto = new AptitudTrabajadorDto()
                .dniTrabajador("12345678A")
                .aptitud("Hornear Pan");

        AptitudTrabajadorEntity result = mapper.toEntity(dto);

        assertNotNull(result.getId());
        assertEquals("12345678A", result.getId().getTrabajador());
        assertEquals("Hornear Pan", result.getId().getAptitud());
    }
}
