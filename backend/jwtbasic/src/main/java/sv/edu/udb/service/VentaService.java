package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.edu.udb.dto.VentaRequest;
import sv.edu.udb.model.*;
import sv.edu.udb.repository.ClienteRepository;
import sv.edu.udb.repository.DetalleVentaRepository;
import sv.edu.udb.repository.ProductoRepository;
import sv.edu.udb.repository.VentaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> buscarPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public Venta guardar(Venta venta) {
        return ventaRepository.save(venta);
    }

    public void eliminar(Long id) {
        ventaRepository.deleteById(id);
    }

    public long contarVentasHoy() {
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fin = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        return ventaRepository.countByFechaBetween(inicio, fin);
    }

    public List<Venta> listarPorEmpleado(Usuario empleado) {
        return ventaRepository.findByEmpleado(empleado);
    }

    public Venta crearVenta(VentaRequest request, Usuario empleado) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setEmpleado(empleado);
        venta.setEstado(Venta.EstadoVenta.valueOf(request.getEstado().toLowerCase()));
        venta.setFecha(java.time.LocalDateTime.now());

        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        venta = ventaRepository.save(venta);

        for (VentaRequest.DetalleProductoDTO dp : request.getProductos()) {
            Producto producto = productoRepository.findById(dp.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(dp.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(new java.math.BigDecimal(dp.getCantidad())));
            detalleVentaRepository.save(detalle);

            total = total.add(detalle.getSubtotal());
        }

        venta.setTotal(total);
        return ventaRepository.save(venta);
    }


}
