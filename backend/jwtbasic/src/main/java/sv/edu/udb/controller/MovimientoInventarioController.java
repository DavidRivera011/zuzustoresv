package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.model.MovimientoInventario;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.service.MovimientoInventarioService;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos-inventario")
@RequiredArgsConstructor
public class MovimientoInventarioController {
    private final MovimientoInventarioService movimientoInventarioService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<MovimientoInventario> listarTodos() {
        return movimientoInventarioService.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovimientoInventario> buscarPorId(@PathVariable Long id) {
        return movimientoInventarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mios")
    @PreAuthorize("hasRole('EMPLEADO')")
    public List<MovimientoInventario> listarMisMovimientos(@AuthenticationPrincipal Object principal) {
        Usuario empleado = getUsuarioFromPrincipal(principal);
        return movimientoInventarioService.listarPorEmpleado(empleado.getId());
    }

    private Usuario getUsuarioFromPrincipal(Object principal) {
        String correo;
        if (principal instanceof Usuario) {
            correo = ((Usuario) principal).getCorreo();
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            correo = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else if (principal instanceof String) {
            correo = (String) principal;
        } else {
            throw new RuntimeException("No se pudo obtener el correo del usuario autenticado");
        }
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public MovimientoInventario crear(@RequestBody MovimientoInventario movimiento) {
        return movimientoInventarioService.guardar(movimiento);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovimientoInventario> actualizar(@PathVariable Long id, @RequestBody MovimientoInventario movimiento) {
        return movimientoInventarioService.buscarPorId(id)
                .map(m -> {
                    movimiento.setId(id);
                    return ResponseEntity.ok(movimientoInventarioService.guardar(movimiento));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (movimientoInventarioService.buscarPorId(id).isPresent()) {
            movimientoInventarioService.eliminar(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
