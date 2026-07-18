package es.MercadonaITDiegoRB.mapper;

import es.MercadonaITDiegoRB.dto.TurnoDto;
import es.MercadonaITDiegoRB.entity.TurnoEntity;
import es.MercadonaITDiegoRB.entity.TurnoId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TurnoMapperTest {

    private final TurnoMapper mapper = Mappers.getMapper(TurnoMapper.class);

    @Test
    void mapsEmbeddedIdEntityToFlatDto() {
        TurnoEntity entity = turnoEntity();

        TurnoDto result = mapper.toDto(entity);

        assertEquals(entity.getId().getTrabajador(), result.getDniTrabajador());
        assertEquals(entity.getId().getTienda(), result.getTienda());
        assertEquals(entity.getId().getSeccion(), result.getSeccion());
        assertEquals(entity.getHorasAsignadas(), result.getHoras());
    }

    @Test
    void mapsFlatDtoToEntityWithEmbeddedId() {
        TurnoDto dto = turnoDto();

        TurnoEntity result = mapper.toEntity(dto);

        assertNotNull(result.getId());
        assertEquals(dto.getDniTrabajador(), result.getId().getTrabajador());
        assertEquals(dto.getTienda(), result.getId().getTienda());
        assertEquals(dto.getSeccion(), result.getId().getSeccion());
        assertEquals(dto.getHoras(), result.getHorasAsignadas());
    }

    @Test
    void mapsListsInBothDirections() {
        List<TurnoDto> dtos = mapper.toDtoList(List.of(turnoEntity()));
        List<TurnoEntity> entities = mapper.toEntityList(List.of(turnoDto()));

        assertEquals("Horno", dtos.getFirst().getSeccion());
        assertEquals("Horno", entities.getFirst().getId().getSeccion());
    }

    @Test
    void mapsNullValuesToNull() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDtoList(null));
        assertNull(mapper.toEntityList(null));
    }

    private TurnoEntity turnoEntity() {
        return TurnoEntity.builder()
                .id(new TurnoId("12345678A", 1L, "Horno"))
                .horasAsignadas(4)
                .build();
    }

    private TurnoDto turnoDto() {
        return new TurnoDto()
                .dniTrabajador("12345678A")
                .tienda(1L)
                .seccion("Horno")
                .horas(4);
    }
}
