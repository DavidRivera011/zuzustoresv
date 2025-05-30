package sv.edu.udb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.model.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVenta_Id(Long ventaId);
}

