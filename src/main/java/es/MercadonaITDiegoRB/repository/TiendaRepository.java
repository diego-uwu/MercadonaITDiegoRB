package es.MercadonaITDiegoRB.repository;

import es.MercadonaITDiegoRB.entity.TiendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
