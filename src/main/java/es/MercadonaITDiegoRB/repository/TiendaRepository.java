package es.MercadonaITDiegoRB.repository;

import es.MercadonaITDiegoRB.entity.TiendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiendaRepository extends JpaRepository<TiendaEntity, Long> {
}
