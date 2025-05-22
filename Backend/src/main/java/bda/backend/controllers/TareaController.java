package bda.backend.controllers;

import bda.backend.entities.TareaEntity;
import bda.backend.services.TareaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tarea")
@RequiredArgsConstructor
public class TareaController {

    private final TareaService tareaService;

    @PostMapping("/guardar")
    public void crearTarea(@RequestBody TareaEntity tarea) {
        tareaService.guardarTarea(tarea);
    }

    @DeleteMapping("/eliminar")
    public void eliminarTarea(@RequestBody Long id) {
        tareaService.eliminarTarea(id);
    }

    @PostMapping("/completar")
    public void completarTarea(@RequestBody Long id) {
        tareaService.completarTarea(id);
    }

    @PostMapping("/")
    public List<TareaEntity> obtenerTodas() {
        return tareaService.obtenerTareas();
    }

    @PostMapping("/filtro-estado")
    public List<TareaEntity> obtenerTareasPorEstado(@RequestParam boolean completado) {
        return tareaService.obtenerTareasPorEstado(completado);
    }

    @PostMapping("/filtro-clave")
    public List<TareaEntity> obtenerTareasPorClave(@RequestParam String clave) {
        return tareaService.obtenerTareasPorClave(clave);
    }

}
