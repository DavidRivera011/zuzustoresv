package sv.edu.udb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.model.Categoria;
import sv.edu.udb.service.CategoriaService;
import sv.edu.udb.service.JwtService;
import sv.edu.udb.service.UserDetailsServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testListarTodas() throws Exception {
        List<Categoria> categorias = Arrays.asList(
                Categoria.builder().id(1L).nombre("Maquillaje").build(),
                Categoria.builder().id(2L).nombre("Cuidado de piel").build()
        );

        when(categoriaService.listarTodas()).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Maquillaje"));
    }

    @Test
    void testBuscarPorIdFound() throws Exception {
        Categoria cat = Categoria.builder().id(1L).nombre("Maquillaje").build();

        when(categoriaService.buscarPorId(1L)).thenReturn(Optional.of(cat));

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Maquillaje"));
    }

    @Test
    void testBuscarPorIdNotFound() throws Exception {
        when(categoriaService.buscarPorId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categorias/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearCategoria() throws Exception {
        Categoria cat = Categoria.builder().nombre("Fragancias").build();
        Categoria saved = Categoria.builder().id(1L).nombre("Fragancias").build();

        when(categoriaService.guardar(any(Categoria.class))).thenReturn(saved);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Fragancias"));
    }

    @Test
    void testActualizarCategoriaFound() throws Exception {
        Categoria cat = Categoria.builder().id(1L).nombre("Accesorios").build();

        when(categoriaService.buscarPorId(1L)).thenReturn(Optional.of(cat));
        when(categoriaService.guardar(any(Categoria.class)))
                .thenReturn(Categoria.builder().id(1L).nombre("Accesorios Actualizado").build());

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cat.toBuilder().nombre("Accesorios Actualizado").build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Accesorios Actualizado"));
    }

    @Test
    void testActualizarCategoriaNotFound() throws Exception {
        Categoria cat = Categoria.builder().nombre("Accesorios").build();

        when(categoriaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/categorias/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarCategoriaFound() throws Exception {
        when(categoriaService.buscarPorId(1L)).thenReturn(Optional.of(Categoria.builder().id(1L).nombre("x").build()));
        doNothing().when(categoriaService).eliminar(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarCategoriaNotFound() throws Exception {
        when(categoriaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/categorias/99"))
                .andExpect(status().isNotFound());
    }
}
