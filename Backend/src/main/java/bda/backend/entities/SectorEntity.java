package bda.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "sectores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class SectorEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    private String descripcion;
    private Point ubicacion;
}
