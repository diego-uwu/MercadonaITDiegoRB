package es.MercadonaITDiegoRB.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TurnoId implements Serializable {

    @Column(name = "trabajador", nullable = false, length = 9)
    private String trabajador;

    @Column(name = "tienda", nullable = false)
    private Long tienda;

    @Column(name = "seccion", nullable = false, length = 50)
    private String seccion;
}
