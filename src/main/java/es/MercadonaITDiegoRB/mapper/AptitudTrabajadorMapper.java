package es.MercadonaITDiegoRB.mapper;

import es.MercadonaITDiegoRB.dto.AptitudTrabajadorDto;
import es.MercadonaITDiegoRB.entity.AptitudTrabajadorEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AptitudTrabajadorMapper
        extends EntityMapper<AptitudTrabajadorDto, AptitudTrabajadorEntity> {

    @Override
    @Mapping(source = "id.trabajador", target = "dniTrabajador")
    @Mapping(source = "id.aptitud", target = "aptitud")
    AptitudTrabajadorDto toDto(AptitudTrabajadorEntity entity);

    @Override
    @InheritInverseConfiguration
    AptitudTrabajadorEntity toEntity(AptitudTrabajadorDto dto);
}
