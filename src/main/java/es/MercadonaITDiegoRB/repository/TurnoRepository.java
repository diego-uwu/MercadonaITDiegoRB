package es.MercadonaITDiegoRB.repository;

import es.MercadonaITDiegoRB.entity.TurnoEntity;
import es.MercadonaITDiegoRB.entity.TurnoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnoRepository extends JpaRepository<TurnoEntity, TurnoId> {

}
