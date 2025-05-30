package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.model.Producto;
import sv.edu.udb.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public List<Producto> listarTodos() {
        return productoService.listarTodos();
    }

    @GetMapping("/public")
    public List<Producto> listarPublicos() {
        return productoService.listarDisponibles();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Producto crear(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return productoService.buscarPorId(id)
                .map(prod -> {
                    producto.setId(id);
                    return ResponseEntity.ok(productoService.guardar(producto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoService.buscarPorId(id).isPresent()) {
            productoService.eliminar(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

