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
public class AptitudTrabajadorId implements Serializable {

    @Column(name = "trabajador", nullable = false, length = 9)
    private String trabajador;

    @Column(name = "aptitud", nullable = false, length = 100)
    private String aptitud;
}
