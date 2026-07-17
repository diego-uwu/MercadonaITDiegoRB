package es.MercadonaITDiegoRB.repository;

import es.MercadonaITDiegoRB.entity.TrabajadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrabajadorRepository extends JpaRepository<TrabajadorEntity, String> {
}
