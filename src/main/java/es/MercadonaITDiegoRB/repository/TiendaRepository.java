package es.MercadonaITDiegoRB.repository;

import es.MercadonaITDiegoRB.entity.TiendaEntity;
import es.MercadonaITDiegoRB.repository.projection.TiendaDetalleRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TiendaRepository extends JpaRepository<TiendaEntity, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO tienda_seccion (tienda, seccion)
            SELECT :tiendaId, nombre
            FROM seccion
            """, nativeQuery = true)
    int insertAllSecciones(@Param("tiendaId") Long tiendaId);

    @Modifying
    @Query(value = "DELETE FROM tienda_seccion WHERE tienda = :tiendaId", nativeQuery = true)
    int deleteSecciones(@Param("tiendaId") Long tiendaId);

    @Query(value = """
            SELECT ts.seccion AS "seccion",
                   s.horas_necesarias AS "horasNecesarias",
                   sa.aptitud AS "aptitud"
            FROM tienda_seccion ts
            JOIN seccion s
              ON s.nombre = ts.seccion
            LEFT JOIN seccion_aptitud sa
              ON sa.seccion = ts.seccion
            WHERE ts.tienda = :tiendaId
            ORDER BY ts.seccion, sa.aptitud NULLS LAST
            """, nativeQuery = true)
    List<TiendaDetalleRow> findDetalleRows(@Param("tiendaId") Long tiendaId);
}
