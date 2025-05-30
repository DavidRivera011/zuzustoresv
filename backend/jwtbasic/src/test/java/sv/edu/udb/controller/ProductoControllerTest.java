package sv.edu.udb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.config.SecurityConfigTest;
import sv.edu.udb.model.Producto;
import sv.edu.udb.service.JwtService;
import sv.edu.udb.service.ProductoService;
import sv.edu.udb.service.UserDetailsServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@Import(SecurityConfigTest.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarTodos() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(new Producto()));
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void testListarPublicos() throws Exception {
        when(productoService.listarDisponibles()).thenReturn(List.of(new Producto()));
        mockMvc.perform(get("/api/productos/public"))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarPorId_ProductoExiste() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarPorId_ProductoNoExiste() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearProducto() throws Exception {
        Producto producto = new Producto();
        producto.setNombre("Labial");
        producto.setStock(10);

        when(productoService.guardar(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarProducto_Existe() throws Exception {
        Producto producto = new Producto();
        producto.setNombre("Crema");
        producto.setStock(15);

        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(new Producto()));
        when(productoService.guardar(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarProducto_NoExiste() throws Exception {
        Producto producto = new Producto();
        producto.setNombre("Crema");

        when(productoService.buscarPorId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarProducto_Existe() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(new Producto()));

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarProducto_NoExiste() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNotFound());
    }
}
