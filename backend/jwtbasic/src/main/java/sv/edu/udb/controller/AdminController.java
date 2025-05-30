package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.model.Producto;
import sv.edu.udb.repository.ProductoRepository;
import sv.edu.udb.repository.VentaRepository;
import sv.edu.udb.model.Venta.EstadoVenta;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    @GetMapping("/estadisticas")
    public Map<String, Object> getEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();

        // Usar Enum como parámetro (NO String)
        BigDecimal ganancias = ventaRepository.sumaTotalByEstado(EstadoVenta.entregado);
        BigDecimal devoluciones = ventaRepository.sumaTotalByEstado(EstadoVenta.devolucion);
        BigDecimal cancelaciones = ventaRepository.sumaTotalByEstado(EstadoVenta.cancelado);

        // Compras
        BigDecimal compras = BigDecimal.ZERO;

        // Ingresos = total de ventas entregadas
        BigDecimal ingresos = ventaRepository.sumaTotalByEstado(EstadoVenta.entregado);

        // Cálculo de ganancias descontando devoluciones y cancelaciones
        BigDecimal totalGanancias = ingresos
                .subtract(devoluciones == null ? BigDecimal.ZERO : devoluciones)
                .subtract(cancelaciones == null ? BigDecimal.ZERO : cancelaciones);

        estadisticas.put("ganancias", totalGanancias == null ? 0 : totalGanancias);
        estadisticas.put("devoluciones", devoluciones == null ? 0 : devoluciones);
        estadisticas.put("compras", compras);
        estadisticas.put("ingresos", ingresos == null ? 0 : ingresos);

        // NUEVO: valor total del stock
        BigDecimal valorStock = productoRepository
                .findAll()
                .stream()
                .map(p -> p.getPrecio().multiply(new BigDecimal(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        estadisticas.put("valorStock", valorStock == null ? 0 : valorStock);

        return estadisticas;
    }

    @GetMapping("/ventas-mes")
    public Map<String, Object> ventasPorMes() {
        // Sacar los datos crudos
        List<Object[]> ventas = ventaRepository.countVentasPorMes();
        List<Object[]> devs = ventaRepository.countDevolucionesPorMes();
        List<Object[]> cancel = ventaRepository.countCancelacionesPorMes();

        // Extraer todos los meses y hacerlos únicos y ordenados por aparición
        Set<String> mesesSet = new LinkedHashSet<>();
        for (Object[] v : ventas) mesesSet.add((String) v[0]);
        for (Object[] v : devs) mesesSet.add((String) v[0]);
        for (Object[] v : cancel) mesesSet.add((String) v[0]);
        List<String> labels = new ArrayList<>(mesesSet);

        // Utilidades para pasar datos a map por mes
        Map<String, Integer> mapVentas = new HashMap<>();
        Map<String, Integer> mapDev = new HashMap<>();
        Map<String, Integer> mapCancel = new HashMap<>();

        for (Object[] v : ventas) mapVentas.put((String) v[0], ((Number) v[1]).intValue());
        for (Object[] d : devs) mapDev.put((String) d[0], ((Number) d[1]).intValue());
        for (Object[] c : cancel) mapCancel.put((String) c[0], ((Number) c[1]).intValue());

        List<Integer> ventasData = new ArrayList<>();
        List<Integer> devData = new ArrayList<>();
        List<Integer> cancelData = new ArrayList<>();

        for (String mes : labels) {
            ventasData.add(mapVentas.getOrDefault(mes, 0));
            devData.add(mapDev.getOrDefault(mes, 0));
            cancelData.add(mapCancel.getOrDefault(mes, 0));
        }

        List<Map<String, Object>> datasets = List.of(
                Map.of(
                        "label", "Ventas",
                        "backgroundColor", "#191970",
                        "data", ventasData
                ),
                Map.of(
                        "label", "Devoluciones",
                        "backgroundColor", "#6c63ff",
                        "data", devData
                ),
                Map.of(
                        "label", "Cancelaciones",
                        "backgroundColor", "#f44336",
                        "data", cancelData
                )
        );
        return Map.of("labels", labels, "datasets", datasets);
    }

    @GetMapping("/top-productos")
    public Map<String, Object> topProductos() {
        List<Object[]> topProductos = ventaRepository.topProductosVendidos();
        List<String> labels = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();

        for (Object[] p : topProductos) {
            labels.add((String) p[0]);
            cantidades.add(((Number) p[1]).intValue());
        }

        List<Map<String, Object>> tabla = new ArrayList<>();
        for (Object[] p : topProductos) {
            tabla.add(Map.of(
                    "nombre", p[0],
                    "cantidad", p[1]
            ));
        }

        return Map.of(
                "labels", labels,
                "values", cantidades,
                "tabla", tabla
        );
    }


    @GetMapping("/stock-bajo")
    public List<Map<String, Object>> stockBajo() {
        List<Producto> productos = productoRepository.findByStockLessThan(2);
        List<Map<String, Object>> alerta = new ArrayList<>();
        for (Producto p : productos) {
            alerta.add(Map.of(
                    "ordenID", p.getId(),
                    "nombre", p.getNombre(),
                    "stock", p.getStock(),
                    "estado", p.getEstado().name()
            ));
        }
        return alerta;
    }
}

