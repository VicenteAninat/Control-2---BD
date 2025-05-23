package bda.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TareaUsuarioSectorDTO {
    private Long usuarioId;
    private Long sectorId;
    private Long total;
}