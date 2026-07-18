package es.MercadonaITDiegoRB.repository;

import es.MercadonaITDiegoRB.entity.TrabajadorEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrabajadorRepository extends JpaRepository<TrabajadorEntity, String> {

    boolean existsByTienda(Long tienda);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TrabajadorEntity t WHERE t.dni = :dni")
    Optional<TrabajadorEntity> findByIdForUpdate(@Param("dni") String dni);
}
