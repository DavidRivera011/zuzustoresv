package sv.edu.udb.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.config.SecurityConfigTest;
import sv.edu.udb.model.Producto;
import sv.edu.udb.model.Venta.EstadoVenta;
import sv.edu.udb.repository.ProductoRepository;
import sv.edu.udb.repository.VentaRepository;
import sv.edu.udb.service.JwtService;
import sv.edu.udb.service.UserDetailsServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(SecurityConfigTest.class)
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private ProductoRepository productoRepository;

    @MockBean
    private VentaRepository ventaRepository;

    @MockBean
    private JwtService jwtService;

    @Test
    void testGetEstadisticas() throws Exception {
        when(ventaRepository.sumaTotalByEstado(EstadoVenta.entregado)).thenReturn(new BigDecimal("100"));
        when(ventaRepository.sumaTotalByEstado(EstadoVenta.devolucion)).thenReturn(new BigDecimal("20"));
        when(ventaRepository.sumaTotalByEstado(EstadoVenta.cancelado)).thenReturn(new BigDecimal("10"));

        Producto producto = Producto.builder()
                .nombre("Producto 1")
                .precio(new BigDecimal("10"))
                .stock(11)
                .build();

        when(productoRepository.findAll()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/admin/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ganancias").value(70))
                .andExpect(jsonPath("$.devoluciones").value(20))
                .andExpect(jsonPath("$.compras").value(0))
                .andExpect(jsonPath("$.ingresos").value(100))
                .andExpect(jsonPath("$.valorStock").value(110));
    }

    @Test
    void testStockBajo() throws Exception {
        Producto p = Producto.builder()
                .id(1L)
                .nombre("Producto Bajo")
                .stock(1)
                .estado(Producto.EstadoProducto.disponible)
                .build();

        when(productoRepository.findByStockLessThan(2)).thenReturn(List.of(p));

        mockMvc.perform(get("/api/admin/stock-bajo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ordenID").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Producto Bajo"))
                .andExpect(jsonPath("$[0].stock").value(1))
                .andExpect(jsonPath("$[0].estado").value("disponible"));
    }
}
