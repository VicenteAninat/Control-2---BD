package bda.backend.services;

import bda.backend.entities.UsuarioEntity;
import bda.backend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public void guardarUsuario(UsuarioEntity usuario) {
        if (usuario == null || usuario.getUsername() == null || usuario.getPassword() == null || usuario.getLocation() == null){
            throw new IllegalArgumentException("Campos obligatorios faltantes");
        }

        // Si el usuario tiene ID y existe, es actualización
        if (usuario.getId() != null && usuarioRepository.existsById(usuario.getId())) {
            UsuarioEntity existente = usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
            existente.setUsername(usuario.getUsername());
            existente.setPassword(usuario.getPassword());
            existente.setLocation(usuario.getLocation());
            usuarioRepository.save(existente);
        } else {
            usuario.setId(null);
            usuarioRepository.save(usuario);
        }
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioEntity> obtenerUsuario(Long id) {
        return usuarioRepository.findAll();
    }
}
