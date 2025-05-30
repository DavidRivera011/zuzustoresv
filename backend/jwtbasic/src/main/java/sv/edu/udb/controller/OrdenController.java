package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.dto.OrdenRequest;
import sv.edu.udb.dto.OrdenResponse;
import sv.edu.udb.service.OrdenService;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    public ResponseEntity<OrdenResponse> crearOrden(@RequestBody OrdenRequest ordenRequest) {
        return ResponseEntity.ok(
                ordenService.mapOrdenToResponse(
                        ordenService.crearOrden(ordenRequest)
                )
        );
    }

    // SOLO ADMIN O EMPLEADO pueden ver todas las órdenes
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @GetMapping
    public ResponseEntity<List<OrdenResponse>> listarOrdenes() {
        return ResponseEntity.ok(ordenService.listarOrdenes());
    }

    // SOLO ADMIN O EMPLEADO pueden ver órdenes por cliente
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<OrdenResponse>> listarOrdenesPorCliente(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.listarOrdenesPorCliente(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> editarEstadoOrden(@PathVariable Long id, @RequestBody String estado) {
        ordenService.editarEstadoOrden(id, estado.replace("\"", "")); // elimina comillas del string JSON
        return ResponseEntity.ok().build();
    }

}
