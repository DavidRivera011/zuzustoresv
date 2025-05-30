package sv.edu.udb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import sv.edu.udb.dto.VentaRequest;
import sv.edu.udb.model.*;
import sv.edu.udb.repository.ClienteRepository;
import sv.edu.udb.repository.DetalleVentaRepository;
import sv.edu.udb.repository.ProductoRepository;
import sv.edu.udb.repository.VentaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class VentaServiceTest {

    private VentaRepository ventaRepository;
    private ClienteRepository clienteRepository;
    private ProductoRepository productoRepository;
    private DetalleVentaRepository detalleVentaRepository;
    private VentaService ventaService;

    @BeforeEach
    void setUp() {
        ventaRepository = mock(VentaRepository.class);
        clienteRepository = mock(ClienteRepository.class);
        productoRepository = mock(ProductoRepository.class);
        detalleVentaRepository = mock(DetalleVentaRepository.class);
        ventaService = new VentaService(ventaRepository, clienteRepository, productoRepository, detalleVentaRepository);
    }

    @Test
    void listarTodas_devuelveListaVentas() {
        Venta venta = new Venta();
        when(ventaRepository.findAll()).thenReturn(List.of(venta));
        List<Venta> resultado = ventaService.listarTodas();
        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorId_encontrado() {
        Venta venta = new Venta();
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        Optional<Venta> result = ventaService.buscarPorId(1L);
        assertThat(result).isPresent();
    }

    @Test
    void guardar_persisteVenta() {
        Venta venta = new Venta();
        when(ventaRepository.save(venta)).thenReturn(venta);
        Venta saved = ventaService.guardar(venta);
        assertThat(saved).isNotNull();
    }

    @Test
    void eliminar_llamaAlRepositorio() {
        ventaService.eliminar(2L);
        verify(ventaRepository).deleteById(2L);
    }

    @Test
    void contarVentasHoy_funciona() {
        when(ventaRepository.countByFechaBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(5L);
        long count = ventaService.contarVentasHoy();
        assertThat(count).isEqualTo(5);
    }

    @Test
    void listarPorEmpleado_funciona() {
        Usuario empleado = new Usuario();
        when(ventaRepository.findByEmpleado(empleado)).thenReturn(List.of(new Venta()));
        List<Venta> ventas = ventaService.listarPorEmpleado(empleado);
        assertThat(ventas).hasSize(1);
    }

    @Test
    void crearVenta_flujoCompleto() {
        Usuario empleado = new Usuario();
        empleado.setId(10L);
        empleado.setCorreo("empleado@correo.com");

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Producto producto = new Producto();
        producto.setId(100L);
        producto.setPrecio(new BigDecimal("12.00"));

        VentaRequest.DetalleProductoDTO detalleDTO = new VentaRequest.DetalleProductoDTO();
        detalleDTO.setProductoId(100L);
        detalleDTO.setCantidad(2);

        VentaRequest request = new VentaRequest();
        request.setClienteId(1L);
        request.setEstado("entregado");
        request.setProductos(List.of(detalleDTO));

        // Stubs
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
        // La venta debe ser "persistida" dos veces (al inicio y al final para actualizar el total)
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> {
            Venta v = invocation.getArgument(0);
            v.setId(99L);
            return v;
        });

        // Test
        Venta venta = ventaService.crearVenta(request, empleado);

        // Verifica total calculado
        assertThat(venta.getTotal()).isEqualByComparingTo("24.00");

        // Verifica que se guardó detalle
        ArgumentCaptor<DetalleVenta> detalleCaptor = ArgumentCaptor.forClass(DetalleVenta.class);
        verify(detalleVentaRepository).save(detalleCaptor.capture());
        DetalleVenta detalleGuardado = detalleCaptor.getValue();
        assertThat(detalleGuardado.getCantidad()).isEqualTo(2);
        assertThat(detalleGuardado.getPrecioUnitario()).isEqualByComparingTo("12.00");
        assertThat(detalleGuardado.getSubtotal()).isEqualByComparingTo("24.00");

        // Verifica que se guardó la venta dos veces
        verify(ventaRepository, atLeast(2)).save(any(Venta.class));
    }
}
