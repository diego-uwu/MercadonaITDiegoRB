package es.MercadonaITDiegoRB.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "trabajador",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_trabajador_tienda",
                columnNames = {"dni", "tienda"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorEntity {

    @Id
    @Column(name = "dni", nullable = false, length = 9)
    private String dni;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellidos", nullable = false, length = 150)
    private String apellidos;

    @Column(name = "tienda", nullable = false)
    private Long tienda;

    @Min(0)
    @Max(8)
    @Column(name = "horas_disponibles", nullable = false, precision = 2)
    private Integer horasDisponibles;
}
