package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.model.Venta;
import sv.edu.udb.model.Venta.EstadoVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    long countByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    long countByEmpleadoIdAndEstado(Long empleadoId, Venta.EstadoVenta estado);

    // Suma el total de las ventas según el estado
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.estado = ?1")
    BigDecimal sumaTotalByEstado(EstadoVenta estado);

    // Ventas entregadas por mes (últimos 5 meses)
    @Query("SELECT FUNCTION('DATE_FORMAT', v.fecha, '%b') AS mes, COUNT(*) " +
            "FROM Venta v WHERE v.estado = 'entregado' " +
            "GROUP BY FUNCTION('DATE_FORMAT', v.fecha, '%b'), FUNCTION('DATE_FORMAT', v.fecha, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', v.fecha, '%Y-%m')")
    List<Object[]> countVentasPorMes();

    // Ventas devueltas por mes
    @Query("SELECT FUNCTION('DATE_FORMAT', v.fecha, '%b') AS mes, COUNT(*) " +
            "FROM Venta v WHERE v.estado = 'devolucion' " +
            "GROUP BY FUNCTION('DATE_FORMAT', v.fecha, '%b'), FUNCTION('DATE_FORMAT', v.fecha, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', v.fecha, '%Y-%m')")
    List<Object[]> countDevolucionesPorMes();

    // Ventas canceladas por mes
    @Query("SELECT FUNCTION('DATE_FORMAT', v.fecha, '%b') AS mes, COUNT(*) " +
            "FROM Venta v WHERE v.estado = 'cancelado' " +
            "GROUP BY FUNCTION('DATE_FORMAT', v.fecha, '%b'), FUNCTION('DATE_FORMAT', v.fecha, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', v.fecha, '%Y-%m')")
    List<Object[]> countCancelacionesPorMes();

    // Top productos vendidos (por cantidad)
    @Query("SELECT p.nombre, SUM(dv.cantidad) FROM DetalleVenta dv " +
            "JOIN dv.producto p " +
            "GROUP BY p.nombre " +
            "ORDER BY SUM(dv.cantidad) DESC")
    List<Object[]> topProductosVendidos();

    @Query("SELECT FUNCTION('DATE_FORMAT', v.fecha, '%b') AS mes, COUNT(*) " +
            "FROM Venta v WHERE v.empleado.id = ?1 AND v.estado = 'entregado' " +
            "GROUP BY FUNCTION('DATE_FORMAT', v.fecha, '%b'), FUNCTION('DATE_FORMAT', v.fecha, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', v.fecha, '%Y-%m')")
    List<Object[]> countVentasPorMesEmpleado(Long empleadoId);

    long countByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDateTime inicio, LocalDateTime fin);

    //CANCELACIONES
    @Query("SELECT FUNCTION('DATE_FORMAT', v.fecha, '%b') AS mes, COUNT(*) " +
            "FROM Venta v WHERE v.empleado.id = ?1 AND v.estado = 'cancelado' " +
            "GROUP BY FUNCTION('DATE_FORMAT', v.fecha, '%b'), FUNCTION('DATE_FORMAT', v.fecha, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', v.fecha, '%Y-%m')")
    List<Object[]> countCancelacionesPorMesEmpleado(Long empleadoId);

    List<Venta> findByEmpleado(Usuario empleado);
}
