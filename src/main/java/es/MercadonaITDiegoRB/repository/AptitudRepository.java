package es.MercadonaITDiegoRB.repository;

import es.MercadonaITDiegoRB.entity.AptitudTrabajadorEntity;
import es.MercadonaITDiegoRB.entity.AptitudTrabajadorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AptitudRepository extends JpaRepository<AptitudTrabajadorEntity, AptitudTrabajadorId> {

    @Query(value = "SELECT COUNT(*) FROM aptitud WHERE nombre = :aptitud", nativeQuery = true)
    long countByAptitudNombre(@Param("aptitud") String aptitud);

    @Query(value = """
            SELECT sa.aptitud
            FROM seccion_aptitud sa
            WHERE sa.seccion = :seccion
              AND NOT EXISTS (
                  SELECT 1
                  FROM trabajador_aptitud ta
                  WHERE ta.trabajador = :dni
                    AND ta.aptitud = sa.aptitud
              )
            ORDER BY sa.aptitud
            """, nativeQuery = true)
    List<String> findAptitudesFaltantes(
            @Param("dni") String dni,
            @Param("seccion") String seccion
    );
}
