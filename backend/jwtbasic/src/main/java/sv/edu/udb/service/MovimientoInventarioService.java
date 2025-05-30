package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.edu.udb.model.MovimientoInventario;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.model.Producto;
import sv.edu.udb.repository.MovimientoInventarioRepository;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public List<MovimientoInventario> listarTodos() {
        return movimientoInventarioRepository.findAll();
    }

    public Optional<MovimientoInventario> buscarPorId(Long id) {
        return movimientoInventarioRepository.findById(id);
    }

    public List<MovimientoInventario> listarPorEmpleado(Long empleadoId) {
        return movimientoInventarioRepository.findByEmpleadoId(empleadoId);
    }

    /**
     * Guarda un nuevo movimiento de inventario.
     * Calcula el stock_resultante ANTES de guardar el movimiento.
     * El stock de productos se actualiza vía trigger en la base de datos.
     */
    public MovimientoInventario guardar(MovimientoInventario movimiento) {
        // Verifica y recupera el empleado
        if (movimiento.getEmpleado() != null && movimiento.getEmpleado().getId() != null) {
            Usuario empleado = usuarioRepository.findById(movimiento.getEmpleado().getId())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
            movimiento.setEmpleado(empleado);
        }

        // Verifica y recupera el producto
        if (movimiento.getProducto() != null && movimiento.getProducto().getId() != null) {
            Producto producto = productoRepository.findById(movimiento.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            movimiento.setProducto(producto);

            // --- Cálculo de stock_resultante antes de guardar ---
            int stockActual = producto.getStock();
            int stockResultante = stockActual;
            if (movimiento.getTipo() == MovimientoInventario.TipoMovimiento.entrada) {
                stockResultante = stockActual + movimiento.getCantidad();
            } else if (movimiento.getTipo() == MovimientoInventario.TipoMovimiento.salida) {
                stockResultante = stockActual - movimiento.getCantidad();
            }
            movimiento.setStockResultante(stockResultante);
        } else {
            throw new RuntimeException("Producto no definido en el movimiento");
        }

        // Guarda el movimiento (el trigger sólo modifica el stock de productos)
        return movimientoInventarioRepository.save(movimiento);
    }

    public void eliminar(Long id) {
        movimientoInventarioRepository.deleteById(id);
    }
}
