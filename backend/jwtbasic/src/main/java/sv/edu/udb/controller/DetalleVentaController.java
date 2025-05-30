package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.model.DetalleVenta;
import sv.edu.udb.service.DetalleVentaService;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-ventas")
@RequiredArgsConstructor
public class DetalleVentaController {
    private final DetalleVentaService detalleVentaService;

    @GetMapping
    public List<DetalleVenta> listarTodos() {
        return detalleVentaService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVenta> buscarPorId(@PathVariable Long id) {
        return detalleVentaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/por-venta/{ventaId}")
    @PreAuthorize("hasAnyRole('EMPLEADO','ADMIN')")
    public List<DetalleVenta> buscarPorVenta(@PathVariable Long ventaId) {
        return detalleVentaService.buscarPorVentaId(ventaId);
    }

    @PostMapping
    public DetalleVenta crear(@RequestBody DetalleVenta detalleVenta) {
        return detalleVentaService.guardar(detalleVenta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleVenta> actualizar(@PathVariable Long id, @RequestBody DetalleVenta detalleVenta) {
        return detalleVentaService.buscarPorId(id)
                .map(d -> {
                    detalleVenta.setId(id);
                    return ResponseEntity.ok(detalleVentaService.guardar(detalleVenta));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (detalleVentaService.buscarPorId(id).isPresent()) {
            detalleVentaService.eliminar(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
