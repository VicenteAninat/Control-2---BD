package org.bda.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Table(name = "tareas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class TareaEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Date fechaVencimiento;
    private Boolean completado;
    private Long usuarioId;

    @Column(name = "sector_id")
    private Long sector_id;
}
