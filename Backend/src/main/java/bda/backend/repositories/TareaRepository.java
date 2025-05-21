package bda.backend.repositories;

import bda.backend.entities.TareaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<TareaEntity, Long> {

    List<TareaEntity> findByCompletadoEquals(boolean completado);
}
