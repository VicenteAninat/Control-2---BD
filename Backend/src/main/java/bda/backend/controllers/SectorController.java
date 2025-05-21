package bda.backend.controllers;


import bda.backend.repositories.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SectorController {

    private final SectorRepository sectorRepository;
}
