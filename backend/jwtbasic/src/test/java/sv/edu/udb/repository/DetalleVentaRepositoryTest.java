package sv.edu.udb.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sv.edu.udb.model.DetalleVenta;
import sv.edu.udb.model.Producto;
import sv.edu.udb.model.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DetalleVentaRepositoryTest {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    @DisplayName("Guardar y buscar detalles por ventaId")
    void testFindByVentaId() {
        // Crear producto completo
        Producto producto1 = Producto.builder()
                .nombre("Labial")
                .precio(new BigDecimal("5.50"))
                .stock(100)
                .estado(Producto.EstadoProducto.disponible)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        Producto producto2 = Producto.builder()
                .nombre("Rímel")
                .precio(new BigDecimal("10.00"))
                .stock(200)
                .estado(Producto.EstadoProducto.disponible)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        producto1 = productoRepository.save(producto1);
        producto2 = productoRepository.save(producto2);

        // Crear venta completa
        Venta venta = Venta.builder()
                .total(new BigDecimal("21.00"))
                .fecha(LocalDateTime.now())
                .estado(Venta.EstadoVenta.entregado)
                .build();
        venta = ventaRepository.save(venta);

        // Crear detalles
        DetalleVenta detalle1 = DetalleVenta.builder()
                .venta(venta)
                .producto(producto1)
                .cantidad(2)
                .precioUnitario(new BigDecimal("5.50"))
                .subtotal(new BigDecimal("11.00"))
                .build();
        DetalleVenta detalle2 = DetalleVenta.builder()
                .venta(venta)
                .producto(producto2)
                .cantidad(1)
                .precioUnitario(new BigDecimal("10.00"))
                .subtotal(new BigDecimal("10.00"))
                .build();

        detalleVentaRepository.save(detalle1);
        detalleVentaRepository.save(detalle2);

        List<DetalleVenta> encontrados = detalleVentaRepository.findByVenta_Id(venta.getId());

        assertThat(encontrados).hasSize(2);
        assertThat(encontrados)
                .extracting(DetalleVenta::getProducto)
                .extracting(Producto::getNombre)
                .containsExactlyInAnyOrder("Labial", "Rímel");
    }


    @Test
    @DisplayName("Buscar detalles por ventaId inexistente retorna vacío")
    void testFindByVentaIdInexistente() {
        List<DetalleVenta> encontrados = detalleVentaRepository.findByVenta_Id(9999L);
        assertThat(encontrados).isEmpty();
    }

    @Test
    @DisplayName("Guardar detalles para diferentes ventas y filtrar correctamente")
    void testDetallesPorMultiplesVentas() {
        Venta venta1 = ventaRepository.save(Venta.builder()
                .total(new BigDecimal("12.00"))
                .fecha(LocalDateTime.now())
                .estado(Venta.EstadoVenta.entregado)
                .build());
        Venta venta2 = ventaRepository.save(Venta.builder()
                .total(new BigDecimal("24.00"))
                .fecha(LocalDateTime.now())
                .estado(Venta.EstadoVenta.entregado)
                .build());
        Producto producto = productoRepository.save(Producto.builder()
                .nombre("Base")
                .precio(new BigDecimal("12.00"))
                .stock(100)
                .estado(Producto.EstadoProducto.disponible)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build());

        DetalleVenta d1 = DetalleVenta.builder().venta(venta1).producto(producto).cantidad(1).precioUnitario(new BigDecimal("12.00")).subtotal(new BigDecimal("12.00")).build();
        DetalleVenta d2 = DetalleVenta.builder().venta(venta2).producto(producto).cantidad(2).precioUnitario(new BigDecimal("12.00")).subtotal(new BigDecimal("24.00")).build();

        detalleVentaRepository.save(d1);
        detalleVentaRepository.save(d2);

        List<DetalleVenta> detallesVenta1 = detalleVentaRepository.findByVenta_Id(venta1.getId());
        List<DetalleVenta> detallesVenta2 = detalleVentaRepository.findByVenta_Id(venta2.getId());

        assertThat(detallesVenta1).hasSize(1);
        assertThat(detallesVenta2).hasSize(1);
        assertThat(detallesVenta1.get(0).getCantidad()).isEqualTo(1);
        assertThat(detallesVenta2.get(0).getCantidad()).isEqualTo(2);
    }
}
