package bda.backend.services;

import bda.backend.config.SectorRequest;
import bda.backend.dto.SectorDTO;
import bda.backend.dto.SectorGrillaDTO;
import bda.backend.entities.SectorEntity;
import bda.backend.dto.SectorGrillaDTO;
import bda.backend.repositories.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;

    public void guardarSector(SectorRequest sector) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point ubicacion = geometryFactory.createPoint(
                new org.locationtech.jts.geom.Coordinate(sector.longitude(), sector.latitude())
        );
        ubicacion.setSRID(4326);
        if (sector.id() != null) {
            SectorEntity sectorEntity = sectorRepository.findById(sector.id())
                    .orElseThrow(() -> new IllegalArgumentException("El sector no existe"));
            sectorEntity.setNombre(sector.nombre());
            sectorEntity.setDescripcion(sector.descripcion());
            sectorEntity.setUbicacion(ubicacion);
            sectorRepository.save(sectorEntity);
            return;
        }
        SectorEntity sectorEntity = new SectorEntity();
        sectorEntity.setNombre(sector.nombre());
        sectorEntity.setDescripcion(sector.descripcion());
        sectorEntity.setUbicacion(ubicacion);

        sectorRepository.save(sectorEntity);
    }

    public void eliminarSector(Long id) {
        sectorRepository.deleteById(id);
    }

    public List<SectorDTO> obtenerSectores() {
        return sectorRepository.findAll().stream().map(sector -> {
            SectorDTO dto = new SectorDTO();
            dto.setId(sector.getId());
            dto.setNombre(sector.getNombre());
            dto.setDescripcion(sector.getDescripcion());
            dto.setLatitude(sector.getUbicacion().getY());
            dto.setLongitude(sector.getUbicacion().getX());
            return dto;
        }).toList();
    }

    public List<SectorGrillaDTO> obtenerSectoresConMasTareas() {
        return sectorRepository.obtenerSectoresConMasTareas().stream()
                .map(obj -> new SectorGrillaDTO(
                        ((Number) obj[0]).longValue(),
                        (String) obj[1],
                        ((Number) obj[2]).doubleValue(),
                        ((Number) obj[3]).doubleValue(),
                        ((Number) obj[4]).intValue()
                ))
                .toList();
    }
}
