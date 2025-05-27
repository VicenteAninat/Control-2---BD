package bda.backend.controllers;

import bda.backend.config.SectorRequest;
import bda.backend.dto.SectorDTO;
import bda.backend.dto.SectorGrillaDTO;
import bda.backend.services.SectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sector")
@RequiredArgsConstructor
public class SectorController {

    private final SectorService sectorService;

    @PostMapping("/guardar")
    public void crearSector(@RequestBody SectorRequest sector) {
        sectorService.guardarSector(sector);
    }

    @PostMapping("/eliminar")
    public void eliminarSector(@RequestBody Long id) {
        sectorService.eliminarSector(id);
    }

    @GetMapping("/")
    public List<SectorDTO> obtenerSectores() {
        return sectorService.obtenerSectores();
    }

    @GetMapping("/sectores-con-mas-tareas")
    public List<SectorGrillaDTO> obtenerSectoresConMasTareas() {
        return sectorService.obtenerSectoresConMasTareas();
    }
}