package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sv.edu.udb.model.MovimientoInventario;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    long countByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT FUNCTION('DATE_FORMAT', m.fecha, '%b') AS mes, COUNT(*) " +
            "FROM MovimientoInventario m WHERE m.empleado.id = ?1 " +
            "GROUP BY FUNCTION('DATE_FORMAT', m.fecha, '%b'), FUNCTION('DATE_FORMAT', m.fecha, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', m.fecha, '%Y-%m')")
    List<Object[]> countMovimientosPorMesEmpleado(Long empleadoId);

    List<MovimientoInventario> findByEmpleadoId(Long empleadoId);

    @Query("""
        SELECT p.categoria.nombre, COUNT(m)
        FROM MovimientoInventario m
        JOIN m.producto p
        WHERE m.empleado.id = :empleadoId
        GROUP BY p.categoria.nombre
    """)
    List<Object[]> countProductosGestionadosPorCategoria(Long empleadoId);

    @Query("SELECT COUNT(DISTINCT m.producto.id) FROM MovimientoInventario m WHERE m.empleado.id = :empleadoId")
    long countProductosUnicosGestionadosPorEmpleado(Long empleadoId);

}

