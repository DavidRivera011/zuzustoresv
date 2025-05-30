package sv.edu.udb.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.udb.repository.ProductoRepository;
import sv.edu.udb.model.Producto;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void testBuscarProductoPorId() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Polvo compacto");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = productoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Polvo compacto", resultado.get().getNombre());
    }
}
