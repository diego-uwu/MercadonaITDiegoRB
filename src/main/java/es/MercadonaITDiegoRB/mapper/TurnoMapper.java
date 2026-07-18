package es.MercadonaITDiegoRB.mapper;

import es.MercadonaITDiegoRB.dto.TurnoDto;
import es.MercadonaITDiegoRB.entity.TurnoEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TurnoMapper extends EntityMapper<TurnoDto, TurnoEntity> {

    @Override
    @Mapping(source = "id.trabajador", target = "dniTrabajador")
    @Mapping(source = "id.tienda", target = "tienda")
    @Mapping(source = "id.seccion", target = "seccion")
    @Mapping(source = "horasAsignadas", target = "horas")
    TurnoDto toDto(TurnoEntity entity);

    @Override
    @InheritInverseConfiguration
    TurnoEntity toEntity(TurnoDto dto);
}
