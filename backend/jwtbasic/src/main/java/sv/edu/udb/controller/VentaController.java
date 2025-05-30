package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.dto.VentaRequest;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.model.Venta;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.service.VentaService;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {
    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Venta> listarTodas() {
        return ventaService.listarTodas();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Venta> buscarPorId(@PathVariable Long id) {
        return ventaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLEADO','ADMIN')")
    public ResponseEntity<?> crearVenta(@RequestBody VentaRequest request, Authentication authentication) {
        String correo;
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            correo = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            correo = (String) principal;
        } else {
            throw new RuntimeException("No se pudo obtener el correo del usuario autenticado");
        }

        Usuario empleado = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        ventaService.crearVenta(request, empleado);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO','ADMIN')")
    public ResponseEntity<Venta> actualizar(@PathVariable Long id, @RequestBody Venta venta) {
        return ventaService.buscarPorId(id)
                .map(v -> {
                    venta.setId(id);
                    return ResponseEntity.ok(ventaService.guardar(venta));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (ventaService.buscarPorId(id).isPresent()) {
            ventaService.eliminar(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/ventas-hoy")
    public long contarVentasHoy() {
        return ventaService.contarVentasHoy();
    }

    @GetMapping("/mis-ventas")
    @PreAuthorize("hasAnyRole('EMPLEADO','ADMIN')")
    public List<Venta> listarPorEmpleado(Authentication authentication) {
        System.out.println("Entró a /api/ventas/mis-ventas");
        String correo;
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            correo = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            correo = (String) principal;
        } else {
            throw new RuntimeException("No se pudo obtener el correo del usuario autenticado");
        }

        Usuario empleado = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        return ventaService.listarPorEmpleado(empleado);
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('EMPLEADO','ADMIN')")
    public ResponseEntity<?> actualizarEstadoVenta(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String estadoNuevo = body.get("estado");
        if (estadoNuevo == null) {
            return ResponseEntity.badRequest().body("Estado no proporcionado.");
        }
        return ventaService.buscarPorId(id).map(venta -> {
            if (
                    venta.getEstado() == Venta.EstadoVenta.entregado ||
                            venta.getEstado() == Venta.EstadoVenta.cancelado ||
                            venta.getEstado() == Venta.EstadoVenta.devolucion
            ) {
                return ResponseEntity.badRequest().body("No se puede modificar el estado de una venta finalizada.");
            }
            try {
                venta.setEstado(Venta.EstadoVenta.valueOf(estadoNuevo.toLowerCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Estado no válido.");
            }
            ventaService.guardar(venta);
            return ResponseEntity.ok("Estado actualizado correctamente.");
        }).orElse(ResponseEntity.notFound().build());
    }


}
