package es.MercadonaITDiegoRB.mapper;

import es.MercadonaITDiegoRB.dto.TrabajadorDto;
import es.MercadonaITDiegoRB.entity.TrabajadorEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrabajadorMapperTest {

    private final TrabajadorMapper mapper = Mappers.getMapper(TrabajadorMapper.class);

    @Test
    void mapsEntityToDto() {
        TrabajadorEntity entity = trabajadorEntity();

        TrabajadorDto result = mapper.toDto(entity);

        assertEquals(entity.getDni(), result.getDni());
        assertEquals(entity.getNombre(), result.getNombre());
        assertEquals(entity.getApellidos(), result.getApellidos());
        assertEquals(entity.getTienda(), result.getTienda());
        assertEquals(entity.getHorasDisponibles(), result.getHorasDisponibles());
    }

    @Test
    void mapsDtoToEntity() {
        TrabajadorDto dto = trabajadorDto();

        TrabajadorEntity result = mapper.toEntity(dto);

        assertEquals(dto.getDni(), result.getDni());
        assertEquals(dto.getNombre(), result.getNombre());
        assertEquals(dto.getApellidos(), result.getApellidos());
        assertEquals(dto.getTienda(), result.getTienda());
        assertEquals(dto.getHorasDisponibles(), result.getHorasDisponibles());
    }

    @Test
    void mapsListsInBothDirections() {
        List<TrabajadorDto> dtos = mapper.toDtoList(List.of(trabajadorEntity()));
        List<TrabajadorEntity> entities = mapper.toEntityList(List.of(trabajadorDto()));

        assertEquals("12345678A", dtos.getFirst().getDni());
        assertEquals("12345678A", entities.getFirst().getDni());
    }

    @Test
    void mapsNullValuesToNull() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDtoList(null));
        assertNull(mapper.toEntityList(null));
    }

    private TrabajadorEntity trabajadorEntity() {
        return TrabajadorEntity.builder()
                .dni("12345678A")
                .nombre("Diego")
                .apellidos("Rodriguez")
                .tienda(1L)
                .horasDisponibles(8)
                .build();
    }

    private TrabajadorDto trabajadorDto() {
        return new TrabajadorDto()
                .dni("12345678A")
                .nombre("Diego")
                .apellidos("Rodriguez")
                .tienda(1L)
                .horasDisponibles(8);
    }
}
