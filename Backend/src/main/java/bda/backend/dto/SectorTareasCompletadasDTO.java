package bda.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SectorTareasCompletadasDTO {
    private Long sectorId;
    private Long total;
}