package sv.edu.udb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.udb.model.MovimientoInventario;
import sv.edu.udb.model.Producto;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.MovimientoInventarioRepository;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovimientoInventarioServiceTest {

    private MovimientoInventarioRepository movimientoInventarioRepository;
    private UsuarioRepository usuarioRepository;
    private ProductoRepository productoRepository;
    private MovimientoInventarioService movimientoInventarioService;

    @BeforeEach
    void setUp() {
        movimientoInventarioRepository = mock(MovimientoInventarioRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        productoRepository = mock(ProductoRepository.class);
        movimientoInventarioService = new MovimientoInventarioService(
                movimientoInventarioRepository,
                usuarioRepository,
                productoRepository
        );
    }

    @Test
    void listarTodos_debeRetornarTodosLosMovimientos() {
        MovimientoInventario m1 = new MovimientoInventario();
        MovimientoInventario m2 = new MovimientoInventario();
        when(movimientoInventarioRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MovimientoInventario> result = movimientoInventarioService.listarTodos();

        assertThat(result).containsExactly(m1, m2);
        verify(movimientoInventarioRepository).findAll();
    }

    @Test
    void buscarPorId_encontrado() {
        MovimientoInventario m = new MovimientoInventario();
        when(movimientoInventarioRepository.findById(10L)).thenReturn(Optional.of(m));

        Optional<MovimientoInventario> result = movimientoInventarioService.buscarPorId(10L);

        assertThat(result).isPresent().contains(m);
        verify(movimientoInventarioRepository).findById(10L);
    }

    @Test
    void buscarPorId_noEncontrado() {
        when(movimientoInventarioRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<MovimientoInventario> result = movimientoInventarioService.buscarPorId(999L);

        assertThat(result).isNotPresent();
        verify(movimientoInventarioRepository).findById(999L);
    }

    @Test
    void listarPorEmpleado_debeRetornarListaPorEmpleado() {
        MovimientoInventario m1 = new MovimientoInventario();
        MovimientoInventario m2 = new MovimientoInventario();
        when(movimientoInventarioRepository.findByEmpleadoId(4L)).thenReturn(List.of(m1, m2));

        List<MovimientoInventario> result = movimientoInventarioService.listarPorEmpleado(4L);

        assertThat(result).containsExactly(m1, m2);
        verify(movimientoInventarioRepository).findByEmpleadoId(4L);
    }

    @Test
    void guardar_movimientoEntrada_calculaStockResultanteYGuarda() {
        Usuario empleado = new Usuario();
        empleado.setId(2L);
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setStock(15);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setEmpleado(empleado);
        movimiento.setProducto(producto);
        movimiento.setTipo(MovimientoInventario.TipoMovimiento.entrada);
        movimiento.setCantidad(3);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(empleado));
        when(productoRepository.findById(5L)).thenReturn(Optional.of(producto));
        when(movimientoInventarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MovimientoInventario result = movimientoInventarioService.guardar(movimiento);

        assertThat(result.getStockResultante()).isEqualTo(18); // 15 + 3
        verify(movimientoInventarioRepository).save(movimiento);
    }

    @Test
    void guardar_movimientoSalida_calculaStockResultanteYGuarda() {
        Usuario empleado = new Usuario();
        empleado.setId(3L);
        Producto producto = new Producto();
        producto.setId(6L);
        producto.setStock(20);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setEmpleado(empleado);
        movimiento.setProducto(producto);
        movimiento.setTipo(MovimientoInventario.TipoMovimiento.salida);
        movimiento.setCantidad(5);

        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(empleado));
        when(productoRepository.findById(6L)).thenReturn(Optional.of(producto));
        when(movimientoInventarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MovimientoInventario result = movimientoInventarioService.guardar(movimiento);

        assertThat(result.getStockResultante()).isEqualTo(15); // 20 - 5
        verify(movimientoInventarioRepository).save(movimiento);
    }

    @Test
    void guardar_lanzaExcepcion_siNoEncuentraEmpleado() {
        Usuario empleado = new Usuario();
        empleado.setId(8L);
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setStock(10);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setEmpleado(empleado);
        movimiento.setProducto(producto);
        movimiento.setTipo(MovimientoInventario.TipoMovimiento.entrada);
        movimiento.setCantidad(1);

        when(usuarioRepository.findById(8L)).thenReturn(Optional.empty());
        when(productoRepository.findById(5L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> movimientoInventarioService.guardar(movimiento))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Empleado no encontrado");
    }

    @Test
    void guardar_lanzaExcepcion_siNoEncuentraProducto() {
        Usuario empleado = new Usuario();
        empleado.setId(1L);
        Producto producto = new Producto();
        producto.setId(999L);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setEmpleado(empleado);
        movimiento.setProducto(producto);
        movimiento.setTipo(MovimientoInventario.TipoMovimiento.entrada);
        movimiento.setCantidad(10);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimientoInventarioService.guardar(movimiento))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado");
    }

    @Test
    void guardar_lanzaExcepcion_siNoHayProductoEnMovimiento() {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setEmpleado(new Usuario());
        movimiento.setCantidad(2);
        movimiento.setTipo(MovimientoInventario.TipoMovimiento.entrada);

        assertThatThrownBy(() -> movimientoInventarioService.guardar(movimiento))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no definido");
    }

    @Test
    void eliminar_eliminaPorId() {
        movimientoInventarioService.eliminar(45L);

        verify(movimientoInventarioRepository).deleteById(45L);
    }
}
