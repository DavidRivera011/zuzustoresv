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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.config.SecurityConfigTest;
import sv.edu.udb.model.MovimientoInventario;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.service.JwtAuthenticationFilter;
import sv.edu.udb.service.MovimientoInventarioService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MovimientoInventarioController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(SecurityConfigTest.class)
class MovimientoInventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimientoInventarioService movimientoInventarioService;
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
    @WithMockUser(username = "admin@correo.com", roles = "ADMIN")
    void listarTodos_Admin_ok() throws Exception {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setId(10L);
        when(movimientoInventarioService.listarTodos()).thenReturn(List.of(mov));

        mockMvc.perform(get("/api/movimientos-inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    @WithMockUser(username = "admin@correo.com", roles = "ADMIN")
    void buscarPorId_Admin_ok() throws Exception {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setId(20L);
        when(movimientoInventarioService.buscarPorId(20L)).thenReturn(Optional.of(mov));

        mockMvc.perform(get("/api/movimientos-inventario/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20L));
    }

    @Test
    @WithMockUser(username = "admin@correo.com", roles = "ADMIN")
    void buscarPorId_Admin_notFound() throws Exception {
        when(movimientoInventarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/movimientos-inventario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "empleado@correo.com", roles = "EMPLEADO")
    void listarMisMovimientos_Empleado_ok() throws Exception {
        Usuario empleado = getEmpleado();
        when(usuarioRepository.findByCorreo("empleado@correo.com")).thenReturn(Optional.of(empleado));

        MovimientoInventario mov = new MovimientoInventario();
        mov.setId(30L);
        when(movimientoInventarioService.listarPorEmpleado(empleado.getId())).thenReturn(List.of(mov));

        mockMvc.perform(get("/api/movimientos-inventario/mios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(30L));
    }

    @Test
    @WithMockUser(username = "admin@correo.com", roles = "ADMIN")
    void crear_Admin_ok() throws Exception {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setId(40L);

        when(movimientoInventarioService.guardar(org.mockito.ArgumentMatchers.any())).thenReturn(mov);

        mockMvc.perform(post("/api/movimientos-inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mov)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(40L));
    }

    @Test
    @WithMockUser(username = "admin@correo.com", roles = "ADMIN")
    void actualizar_Admin_ok() throws Exception {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setId(50L);

        when(movimientoInventarioService.buscarPorId(50L)).thenReturn(Optional.of(mov));
        when(movimientoInventarioService.guardar(org.mockito.ArgumentMatchers.any())).thenReturn(mov);

        mockMvc.perform(put("/api/movimientos-inventario/50")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mov)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50L));
    }

    @Test
    @WithMockUser(username = "admin@correo.com", roles = "ADMIN")
    void actualizar_Admin_notFound() throws Exception {
        MovimientoInventario mov = new MovimientoInventario();
        when(movimientoInventarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/movimientos-inventario/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mov)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@correo.com", roles = "ADMIN")
    void eliminar_Admin_ok() throws Exception {
        MovimientoInventario mov = new MovimientoInventario();
        when(movimientoInventarioService.buscarPorId(60L)).thenReturn(Optional.of(mov));

        mockMvc.perform(delete("/api/movimientos-inventario/60"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@correo.com", roles = "ADMIN")
    void eliminar_Admin_notFound() throws Exception {
        when(movimientoInventarioService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/movimientos-inventario/999"))
                .andExpect(status().isNotFound());
    }
}
