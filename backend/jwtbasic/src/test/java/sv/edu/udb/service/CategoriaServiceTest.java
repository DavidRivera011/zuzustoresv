package sv.edu.udb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.udb.model.Categoria;
import sv.edu.udb.repository.CategoriaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarTodas() {
        Categoria cat1 = Categoria.builder().id(1L).nombre("Maquillaje").build();
        Categoria cat2 = Categoria.builder().id(2L).nombre("Cuidado de piel").build();

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2));

        List<Categoria> categorias = categoriaService.listarTodas();

        assertEquals(2, categorias.size());
        assertEquals("Maquillaje", categorias.get(0).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void testGuardar() {
        Categoria cat = Categoria.builder().nombre("Fragancias").build();

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocation -> {
                    Categoria c = invocation.getArgument(0);
                    c.setId(1L); // Simulamos el ID generado
                    return c;
                });

        Categoria creada = categoriaService.guardar(cat);

        assertNotNull(creada.getId());
        assertEquals("Fragancias", creada.getNombre());
        verify(categoriaRepository, times(1)).save(cat);
    }

    @Test
    void testEliminar() {
        Long id = 1L;
        doNothing().when(categoriaRepository).deleteById(id);

        categoriaService.eliminar(id);

        verify(categoriaRepository, times(1)).deleteById(id);
    }

    @Test
    void testBuscarPorIdFound() {
        Categoria cat = Categoria.builder().id(1L).nombre("Accesorios").build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));

        Optional<Categoria> resultado = categoriaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Accesorios", resultado.get().getNombre());
        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorIdNotFound() {
        when(categoriaRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Categoria> resultado = categoriaService.buscarPorId(2L);

        assertFalse(resultado.isPresent());
        verify(categoriaRepository, times(1)).findById(2L);
    }
}
