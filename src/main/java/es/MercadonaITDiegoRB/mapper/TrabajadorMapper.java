package es.MercadonaITDiegoRB.mapper;

import es.MercadonaITDiegoRB.dto.TrabajadorDto;
import es.MercadonaITDiegoRB.entity.TrabajadorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrabajadorMapper extends EntityMapper<TrabajadorDto, TrabajadorEntity> {

}
