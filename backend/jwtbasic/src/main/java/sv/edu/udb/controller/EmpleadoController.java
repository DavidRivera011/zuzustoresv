package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.model.Venta;
import sv.edu.udb.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/empleado")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLEADO')")
public class EmpleadoController {

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

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

    @GetMapping("/estadisticas")
    public Map<String, Object> getEstadisticasEmpleado(@AuthenticationPrincipal Object principal) {
        Usuario empleado = getUsuarioFromPrincipal(principal);
        Map<String, Object> estadisticas = new HashMap<>();

        // Rango de hoy
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = LocalDate.now().atTime(23, 59, 59);

        long movimientosHoy = movimientoInventarioRepository.countByEmpleadoIdAndFechaBetween(empleado.getId(), inicio, fin);
        long ventasHoy = ventaRepository.countByEmpleadoIdAndFechaBetween(empleado.getId(), inicio, fin);
        long productosGestionados = movimientoInventarioRepository.countProductosUnicosGestionadosPorEmpleado(empleado.getId());
        long totalVentasEntregadas = ventaRepository.countByEmpleadoIdAndEstado(empleado.getId(), Venta.EstadoVenta.entregado);

        estadisticas.put("movimientosHoy", movimientosHoy);
        estadisticas.put("productosGestionados", productosGestionados);
        estadisticas.put("ventasHoy", ventasHoy);
        estadisticas.put("totalVentasEntregadas", totalVentasEntregadas);

        return estadisticas;
    }


    @GetMapping("/ventas-mes")
    public Map<String, Object> getVentasPorMes(@AuthenticationPrincipal Object principal) {
        Usuario empleado = getUsuarioFromPrincipal(principal);

        // Ventas entregadas
        List<Object[]> ventas = ventaRepository.countVentasPorMesEmpleado(empleado.getId());
        // Movimientos de inventario
        List<Object[]> movs = movimientoInventarioRepository.countMovimientosPorMesEmpleado(empleado.getId());
        // Cancelaciones de ventas
        List<Object[]> cancelaciones = ventaRepository.countCancelacionesPorMesEmpleado(empleado.getId());

        // Unir todos los meses únicos
        Set<String> mesesSet = new LinkedHashSet<>();
        for (Object[] v : ventas) mesesSet.add((String) v[0]);
        for (Object[] m : movs) mesesSet.add((String) m[0]);
        for (Object[] c : cancelaciones) mesesSet.add((String) c[0]);
        List<String> labels = new ArrayList<>(mesesSet);

        // Mapear cantidades
        Map<String, Integer> ventasMap = new HashMap<>();
        Map<String, Integer> movsMap = new HashMap<>();
        Map<String, Integer> cancMap = new HashMap<>();
        for (Object[] v : ventas) ventasMap.put((String) v[0], ((Number) v[1]).intValue());
        for (Object[] m : movs) movsMap.put((String) m[0], ((Number) m[1]).intValue());
        for (Object[] c : cancelaciones) cancMap.put((String) c[0], ((Number) c[1]).intValue());

        List<Integer> ventasData = new ArrayList<>();
        List<Integer> movsData = new ArrayList<>();
        List<Integer> cancData = new ArrayList<>();
        for (String mes : labels) {
            ventasData.add(ventasMap.getOrDefault(mes, 0));
            cancData.add(cancMap.getOrDefault(mes, 0));
            movsData.add(movsMap.getOrDefault(mes, 0));
        }

        List<Map<String, Object>> datasets = List.of(
                Map.of("label", "Ventas", "backgroundColor", "#00c85e", "data", ventasData),
                Map.of("label", "Cancelaciones", "backgroundColor", "#ffe216", "data", cancData),
                Map.of("label", "Movimientos", "backgroundColor", "#363d78", "data", movsData)
        );

        return Map.of("labels", labels, "datasets", datasets);
    }


    @GetMapping("/productos-gestionados")
    public Map<String, Object> getProductosGestionados(@AuthenticationPrincipal Object principal) {
        Usuario empleado = getUsuarioFromPrincipal(principal);

        List<Object[]> results = movimientoInventarioRepository.countProductosGestionadosPorCategoria(empleado.getId());

        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (Object[] row : results) {
            labels.add((String) row[0]);
            values.add(((Number) row[1]).intValue());
        }

        return Map.of("labels", labels, "values", values);
    }

}
