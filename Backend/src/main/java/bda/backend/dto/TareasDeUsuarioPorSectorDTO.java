package bda.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TareasDeUsuarioPorSectorDTO {
    private Long sectorId;
    private Long totalTareas;
}
