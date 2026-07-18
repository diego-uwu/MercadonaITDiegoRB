package es.MercadonaITDiegoRB.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trabajador_seccion")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoEntity {

    @EmbeddedId
    private TurnoId id;

    @Min(1)
    @Max(8)
    @Column(name = "horas_asignadas", nullable = false)
    private Integer horasAsignadas;
}
