package bda.backend.repositories;

import bda.backend.entities.TareaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<TareaEntity, Long> {

    List<TareaEntity> findByCompletadoEquals(boolean completado);

    @Query(value = """
    WITH distancias AS (
        SELECT t.*, ST_Distance(s.ubicacion, ST_SetSRID(ST_MakePoint(:longitud, :latitud), 4326)) as distancia
        FROM tarea t
        JOIN sector s ON t.sector_id = s.id
        WHERE t.completado = false
    ),
    min_dist AS (
        SELECT MIN(distancia) as min_distancia FROM distancias
    )
    SELECT * FROM distancias WHERE distancia = (SELECT min_distancia FROM min_dist)
""", nativeQuery = true)
    List<TareaEntity> tareasPendientesMasCercanas(double latitud, double longitud);

    @Query(value = """
    SELECT t.usuario_id, t.sector_id, COUNT(*) as total
    FROM tarea t
    WHERE t.completado = true
    GROUP BY t.usuario_id, t.sector_id
""", nativeQuery = true)
    List<Object[]> countTareasPorUsuarioYSector();

    @Query(value = """
    SELECT s.id, COUNT(t.id) as total
    FROM sector s
    JOIN tarea t ON t.sector_id = s.id
    WHERE t.completado = true
      AND ST_DWithin(
        s.ubicacion,
        ST_SetSRID(ST_MakePoint(:longitud, :latitud), 4326),
        5000
      )
    GROUP BY s.id
    ORDER BY total DESC
    LIMIT 1
""", nativeQuery = true)
    Object[] sectorConMasTareasCompletadasEnRadio(double latitud, double longitud);

    @Query(value = """
    SELECT AVG(ST_Distance(
        s.ubicacion,
        ST_SetSRID(ST_MakePoint(:longitud, :latitud), 4326)
    ))
    FROM tarea t
    JOIN sector s ON t.sector_id = s.id
    WHERE t.completado = true
""", nativeQuery = true)
    Double promedioDistanciaTareasCompletadas(double latitud, double longitud);

    // Obtener tarea por id de usuario
    @Query(value = """
    SELECT t.*
    FROM tarea t
    WHERE t.usuario_id = :usuarioId
""", nativeQuery = true)
    List<TareaEntity> obtenerTareaPorIdUsuario(Long usuarioId);

    // Obtener tarea más cercana a partir de dos coordenadas usando PostGIS y sin id de usuario
    @Query(value = """
    WITH distancias AS (
        SELECT t.*, ST_Distance(s.ubicacion, ST_SetSRID(ST_MakePoint(:longitud, :latitud), 4326)) as distancia
        FROM tarea t
        JOIN sector s ON t.sector_id = s.id
        WHERE t.completado = false
    ),
    min_dist AS (
        SELECT MIN(distancia) as min_distancia FROM distancias
    )
    SELECT * FROM distancias WHERE distancia = (SELECT min_distancia FROM min_dist)
    LIMIT 1
""", nativeQuery = true)
    TareaEntity obtenerTareaMasCercana(double latitud, double longitud);

    // Obtener el promedio de distancia de las tareas completadas por id de usuario
    @Query(value = """
SELECT AVG(ST_Distance(
    s.ubicacion,
    u.location
))
FROM tarea t
JOIN sector s ON t.sector_id = s.id
JOIN usuario u ON t.usuario_id = u.id
WHERE t.completado = true
  AND t.usuario_id = :usuarioId
""", nativeQuery = true)
    Double promedioDistanciaTareasCompletadasPorIdUsuario(Long usuarioId);

    // Mostrar conteo de tareas por sector segun id de usuario ()
    @Query(value = """
SELECT t.sector_id, COUNT(*) as total
FROM tarea t
WHERE t.usuario_id = :usuarioId
GROUP BY t.sector_id
""", nativeQuery = true)
    List<Object[]> mostrarConteoTareasPorSector(Long usuarioId);
}
