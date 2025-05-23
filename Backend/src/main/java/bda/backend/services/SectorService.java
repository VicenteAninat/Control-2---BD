package bda.backend.services;

import bda.backend.config.SectorRequest;
import bda.backend.entities.SectorEntity;
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

        SectorEntity sectorEntity = new SectorEntity();
        sectorEntity.setNombre(sector.nombre());
        sectorEntity.setDescripcion(sector.descripcion());
        sectorEntity.setUbicacion(ubicacion);

        sectorRepository.save(sectorEntity);
    }

    public void eliminarSector(Long id) {
        sectorRepository.deleteById(id);
    }

    public List<SectorEntity> obtenerSectores() {
        return sectorRepository.findAll();
    }
}
