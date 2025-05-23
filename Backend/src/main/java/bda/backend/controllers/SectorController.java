package bda.backend.controllers;

import bda.backend.config.SectorRequest;
import bda.backend.entities.SectorEntity;
import bda.backend.services.SectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
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

    @PostMapping("/")
    public List<SectorEntity> obtenerSectores() {
        return sectorService.obtenerSectores();
    }
}