package bda.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Table(name = "tarea")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class TareaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Date fechaVencimiento;
    private Boolean completado;

    @Column(name = "usuario_id")
    private Long usuario_id;

    @Column(name = "sector_id")
    private Long sector_id;
}
