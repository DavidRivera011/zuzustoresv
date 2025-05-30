package sv.edu.udb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.config.SecurityConfigTest;
import sv.edu.udb.model.DetalleVenta;
import sv.edu.udb.service.DetalleVentaService;
import sv.edu.udb.service.JwtAuthenticationFilter;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = DetalleVentaController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(SecurityConfigTest.class)
class DetalleVentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DetalleVentaService detalleVentaService;

    @Autowired
    private ObjectMapper objectMapper;

    // ==== Helper para setear el contexto de seguridad ====
    private void setSecurityContextEmpleado() {
        UserDetails userDetails = User.builder()
                .username("empleado@correo.com")
                .password("password")
                .roles("EMPLEADO")
                .build();
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, "password", userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void listarTodos_debeRetornarLista() throws Exception {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(1L);

        when(detalleVentaService.listarTodos()).thenReturn(List.of(detalle));

        mockMvc.perform(get("/api/detalle-ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void buscarPorId_existente() throws Exception {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(2L);

        when(detalleVentaService.buscarPorId(2L)).thenReturn(Optional.of(detalle));

        mockMvc.perform(get("/api/detalle-ventas/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void buscarPorId_noExistente() throws Exception {
        when(detalleVentaService.buscarPorId(9L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/detalle-ventas/9"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorVenta_debeRequerirRolEmpleadoOAdmin() throws Exception {
        setSecurityContextEmpleado();

        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(3L);

        when(detalleVentaService.buscarPorVentaId(10L)).thenReturn(List.of(detalle));

        mockMvc.perform(get("/api/detalle-ventas/por-venta/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L));
    }

    @Test
    void crearDetalleVenta_debeGuardarYRetornar() throws Exception {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(4L);

        when(detalleVentaService.guardar(Mockito.any(DetalleVenta.class))).thenReturn(detalle);

        mockMvc.perform(post("/api/detalle-ventas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detalle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L));
    }

    @Test
    void actualizar_existente() throws Exception {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(5L);

        when(detalleVentaService.buscarPorId(5L)).thenReturn(Optional.of(detalle));
        when(detalleVentaService.guardar(Mockito.any(DetalleVenta.class))).thenReturn(detalle);

        mockMvc.perform(put("/api/detalle-ventas/5")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detalle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    void actualizar_noExistente() throws Exception {
        DetalleVenta detalle = new DetalleVenta();

        when(detalleVentaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/detalle-ventas/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detalle)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_existente() throws Exception {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(7L);

        when(detalleVentaService.buscarPorId(7L)).thenReturn(Optional.of(detalle));

        mockMvc.perform(delete("/api/detalle-ventas/7")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_noExistente() throws Exception {
        when(detalleVentaService.buscarPorId(88L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/detalle-ventas/88")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
