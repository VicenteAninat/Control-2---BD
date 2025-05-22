package bda.backend.controllers;

import bda.backend.entities.UsuarioEntity;
import bda.backend.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/guardar")
    public void crearUsuario(@RequestBody UsuarioEntity usuario) {
        usuarioService.guardarUsuario(usuario);
    }

    @PostMapping("/eliminar")
    public void eliminarUsuario(@RequestBody Long id) {
        usuarioService.eliminarUsuario(id);
    }

    @PostMapping("/")
    public List<UsuarioEntity> obtenerUsuario(@RequestBody Long id) {
        return usuarioService.obtenerUsuario(id);
    }
}
