package es.MercadonaITDiegoRB.repository;

import es.MercadonaITDiegoRB.entity.TurnoEntity;
import es.MercadonaITDiegoRB.entity.TurnoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<TurnoEntity, TurnoId> {

    List<TurnoEntity> findAllByIdTrabajadorOrderByIdSeccionAsc(String dni);

    @Query("""
            SELECT COALESCE(SUM(t.horasAsignadas), 0)
            FROM TurnoEntity t
            WHERE t.id.trabajador = :dni
            """)
    Long sumHorasAsignadasByTrabajador(@Param("dni") String dni);
}
