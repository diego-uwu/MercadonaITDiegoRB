package es.MercadonaITDiegoRB.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trabajador_aptitud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AptitudTrabajadorEntity {

    @EmbeddedId
    private AptitudTrabajadorId id;
}
