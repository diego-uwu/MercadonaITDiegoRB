package es.MercadonaITDiegoRB.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tienda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TiendaEntity {

    @Id
    @Column(name = "codigo", nullable = false)
    private Long codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
}
