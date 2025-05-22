package bda.backend.repositories;

import bda.backend.entities.SectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorRepository extends JpaRepository<SectorEntity, Long> {
}
