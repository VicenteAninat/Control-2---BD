package bda.backend.repositories;

import bda.backend.dto.SectorDTO;
import bda.backend.entities.SectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorRepository extends JpaRepository<SectorEntity, Long> {
    // Metodo para obtener los sectores con más tareas completadas usando agrupación espacial y PostGIS
    @Query(value = """
    SELECT
      s.id,
      s.nombre,
      ST_Y(ST_Centroid(ST_SnapToGrid(s.ubicacion, 0.01, 0.01))) AS latitude,
      ST_X(ST_Centroid(ST_SnapToGrid(s.ubicacion, 0.01, 0.01))) AS longitude,
      COUNT(t.id) AS tareasCompletadas
    FROM tarea t
    JOIN sector s ON t.sector_id = s.id
    WHERE t.completado = false
    GROUP BY s.id, s.nombre, ST_SnapToGrid(s.ubicacion, 0.01, 0.01)
    ORDER BY tareasCompletadas DESC
    LIMIT 10
    """, nativeQuery = true)
    List<Object[]> obtenerSectoresConMasTareas();
}
