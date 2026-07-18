package es.MercadonaITDiegoRB.mapper;

import es.MercadonaITDiegoRB.dto.TiendaDto;
import es.MercadonaITDiegoRB.entity.TiendaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TiendaMapper extends EntityMapper<TiendaDto, TiendaEntity> {
}
