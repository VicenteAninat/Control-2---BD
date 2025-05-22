package bda.backend.services;

import bda.backend.entities.SectorEntity;
import bda.backend.repositories.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;

    public void guardarSector(SectorEntity sector) {
        // Validación de campos obligatorios
        if (sector == null || sector.getNombre() == null || sector.getDescripcion() == null ||
                sector.getUbicacion() == null) {
            throw new IllegalArgumentException("Campos obligatorios faltantes");
        }

        // Si el sector tiene ID y existe, es actualización
        if (sector.getId() != null && sectorRepository.existsById(sector.getId())) {
            SectorEntity existente = sectorRepository.findById(sector.getId())
                    .orElseThrow(() -> new IllegalArgumentException("El sector no existe"));
            existente.setNombre(sector.getNombre());
            existente.setDescripcion(sector.getDescripcion());
            existente.setUbicacion(sector.getUbicacion());
            sectorRepository.save(existente);
        } else {
            sector.setId(null);
            sectorRepository.save(sector);
        }
    }

    public void eliminarSector(Long id) {
        if (!sectorRepository.existsById(id)) {
            throw new IllegalArgumentException("El sector no existe");
        }
        sectorRepository.deleteById(id);
    }

    public List<SectorEntity> obtenerSectores() {
        return sectorRepository.findAll();
    }
}
