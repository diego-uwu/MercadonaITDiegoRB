package es.MercadonaITDiegoRB.repository;

import es.MercadonaITDiegoRB.entity.TurnoEntity;
import es.MercadonaITDiegoRB.entity.TurnoId;
import es.MercadonaITDiegoRB.repository.projection.EstadoTiendaRow;
import es.MercadonaITDiegoRB.repository.projection.SeccionIncompletaRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<TurnoEntity, TurnoId> {

    List<TurnoEntity> findAllByIdTrabajadorOrderByIdSeccionAsc(String dni);

    @Query(value = """
            SELECT ts.seccion AS "seccion",
                   tr.nombre AS "nombre",
                   tr.apellidos AS "apellidos",
                   trs.horas_asignadas AS "horasAsignadas"
            FROM tienda_seccion ts
            LEFT JOIN trabajador_seccion trs
                   ON trs.tienda = ts.tienda
                  AND trs.seccion = ts.seccion
            LEFT JOIN trabajador tr
                   ON tr.dni = trs.trabajador
                  AND tr.tienda = trs.tienda
            WHERE ts.tienda = :tiendaId
            ORDER BY ts.seccion,
                     tr.apellidos NULLS LAST,
                     tr.nombre NULLS LAST
            """, nativeQuery = true)
    List<EstadoTiendaRow> findEstadoTiendaRows(@Param("tiendaId") Long tiendaId);

    @Query(value = """
            SELECT ts.seccion AS "seccion",
                   s.horas_necesarias AS "horasNecesarias",
                   COALESCE(SUM(trs.horas_asignadas), 0) AS "horasAsignadas",
                   s.horas_necesarias - COALESCE(SUM(trs.horas_asignadas), 0) AS "horasRestantes"
            FROM tienda_seccion ts
            JOIN seccion s
              ON s.nombre = ts.seccion
            LEFT JOIN trabajador_seccion trs
              ON trs.tienda = ts.tienda
             AND trs.seccion = ts.seccion
            WHERE ts.tienda = :tiendaId
            GROUP BY ts.seccion, s.horas_necesarias
            HAVING COALESCE(SUM(trs.horas_asignadas), 0) < s.horas_necesarias
            ORDER BY ts.seccion
            """, nativeQuery = true)
    List<SeccionIncompletaRow> findSeccionesIncompletasRows(
            @Param("tiendaId") Long tiendaId
    );

    @Query("""
            SELECT COALESCE(SUM(t.horasAsignadas), 0)
            FROM TurnoEntity t
            WHERE t.id.trabajador = :dni
            """)
    Long sumHorasAsignadasByTrabajador(@Param("dni") String dni);
}
