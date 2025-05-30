package sv.edu.udb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.config.SecurityConfigTest;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.model.Venta;
import sv.edu.udb.repository.MovimientoInventarioRepository;
import sv.edu.udb.repository.ProductoRepository;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.repository.VentaRepository;
import sv.edu.udb.service.JwtAuthenticationFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = EmpleadoController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(SecurityConfigTest.class)
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimientoInventarioRepository movimientoInventarioRepository;
    @MockBean
    private ProductoRepository productoRepository;
    @MockBean
    private VentaRepository ventaRepository;
    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario getEmpleado() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setCorreo("empleado@correo.com");
        return u;
    }

    @Test
    @WithMockUser(username = "empleado@correo.com", roles = "EMPLEADO")
    void getEstadisticasEmpleado_retornaEstadisticas() throws Exception {
        Usuario empleado = getEmpleado();
        when(usuarioRepository.findByCorreo("empleado@correo.com")).thenReturn(Optional.of(empleado));

        when(movimientoInventarioRepository.countByEmpleadoIdAndFechaBetween(
                empleado.getId(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(23, 59, 59)
        )).thenReturn(2L);

        when(ventaRepository.countByEmpleadoIdAndFechaBetween(
                empleado.getId(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(23, 59, 59)
        )).thenReturn(3L);

        when(movimientoInventarioRepository.countProductosUnicosGestionadosPorEmpleado(empleado.getId())).thenReturn(4L);
        when(ventaRepository.countByEmpleadoIdAndEstado(empleado.getId(), Venta.EstadoVenta.entregado)).thenReturn(5L);

        mockMvc.perform(get("/api/empleado/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movimientosHoy").value(2L))
                .andExpect(jsonPath("$.ventasHoy").value(3L))
                .andExpect(jsonPath("$.productosGestionados").value(4L))
                .andExpect(jsonPath("$.totalVentasEntregadas").value(5L));
    }

    @Test
    @WithMockUser(username = "empleado@correo.com", roles = "EMPLEADO")
    void getProductosGestionados_retornaCorrecto() throws Exception {
        Usuario empleado = getEmpleado();
        when(usuarioRepository.findByCorreo("empleado@correo.com")).thenReturn(Optional.of(empleado));

        when(movimientoInventarioRepository.countProductosGestionadosPorCategoria(empleado.getId()))
                .thenReturn(List.of(
                        new Object[]{"Maquillaje", 10},
                        new Object[]{"Skincare", 20}
                ));

        mockMvc.perform(get("/api/empleado/productos-gestionados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels[0]").value("Maquillaje"))
                .andExpect(jsonPath("$.labels[1]").value("Skincare"))
                .andExpect(jsonPath("$.values[0]").value(10))
                .andExpect(jsonPath("$.values[1]").value(20));
    }
}
