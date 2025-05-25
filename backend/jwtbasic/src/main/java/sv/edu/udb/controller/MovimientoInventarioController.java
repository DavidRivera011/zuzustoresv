package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.model.MovimientoInventario;
import sv.edu.udb.service.MovimientoInventarioService;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos-inventario")
@RequiredArgsConstructor
public class MovimientoInventarioController {
    private final MovimientoInventarioService movimientoInventarioService;

    @GetMapping
    public List<MovimientoInventario> listarTodos() {
        return movimientoInventarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventario> buscarPorId(@PathVariable Long id) {
        return movimientoInventarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MovimientoInventario crear(@RequestBody MovimientoInventario movimiento) {
        return movimientoInventarioService.guardar(movimiento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoInventario> actualizar(@PathVariable Long id, @RequestBody MovimientoInventario movimiento) {
        return movimientoInventarioService.buscarPorId(id)
                .map(m -> {
                    movimiento.setId(id);
                    return ResponseEntity.ok(movimientoInventarioService.guardar(movimiento));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (movimientoInventarioService.buscarPorId(id).isPresent()) {
            movimientoInventarioService.eliminar(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
