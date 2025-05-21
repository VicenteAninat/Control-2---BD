package bda.backend.repositories;

import bda.backend.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository  extends JpaRepository<UsuarioEntity, Long> {
    
    UsuarioEntity findByUsername(String username);
}
