package es.MercadonaITDiegoRB.repository;

import es.MercadonaITDiegoRB.entity.AptitudTrabajadorEntity;
import es.MercadonaITDiegoRB.entity.AptitudTrabajadorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AptitudRepository extends JpaRepository<AptitudTrabajadorEntity, AptitudTrabajadorId> {

}
