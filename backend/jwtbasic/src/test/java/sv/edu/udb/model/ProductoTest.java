package sv.edu.udb.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void testBuilderAndGettersSetters() {
        Categoria categoria = Categoria.builder().id(1L).nombre("Maquillaje").build();
        LocalDateTime now = LocalDateTime.of(2024, 5, 30, 12, 0, 0);

        Producto producto = Producto.builder()
                .id(1L)
                .nombre("Base líquida")
                .descripcion("Base de cobertura media")
                .marca("L'Oréal")
                .precio(new BigDecimal("9.99"))
                .stock(100)
                .categoria(categoria)
                .imagenUrl("http://url.com/img.jpg")
                .estado(Producto.EstadoProducto.no_disponible)
                .fechaCreacion(now)
                .fechaActualizacion(now)
                .build();

        assertEquals(1L, producto.getId());
        assertEquals("Base líquida", producto.getNombre());
        assertEquals("Base de cobertura media", producto.getDescripcion());
        assertEquals("L'Oréal", producto.getMarca());
        assertEquals(new BigDecimal("9.99"), producto.getPrecio());
        assertEquals(100, producto.getStock());
        assertEquals(categoria, producto.getCategoria());
        assertEquals("http://url.com/img.jpg", producto.getImagenUrl());
        assertEquals(Producto.EstadoProducto.no_disponible, producto.getEstado());
        assertEquals(now, producto.getFechaCreacion());
        assertEquals(now, producto.getFechaActualizacion());
    }

    @Test
    void testDefaultValuesWithBuilder() {
        Producto producto = Producto.builder()
                .nombre("Delineador")
                .precio(new BigDecimal("4.50"))
                .stock(20)
                .build();

        assertEquals(Producto.EstadoProducto.disponible, producto.getEstado());
        assertNotNull(producto.getFechaCreacion());
        assertNotNull(producto.getFechaActualizacion());
    }

    @Test
    void testEnumEstadoProducto() {
        assertEquals("disponible", Producto.EstadoProducto.disponible.name());
        assertEquals("no_disponible", Producto.EstadoProducto.no_disponible.name());
    }

    @Test
    void testEqualsAndHashCode() {
        Producto p1 = Producto.builder().id(1L).nombre("Labial").precio(new BigDecimal("1.00")).stock(1).build();
        Producto p2 = Producto.builder().id(1L).nombre("Labial").precio(new BigDecimal("1.00")).stock(1).build();

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void testToString() {
        Producto producto = Producto.builder().id(123L).nombre("Mascara").precio(new BigDecimal("3.30")).stock(2).build();
        String str = producto.toString();
        assertTrue(str.contains("Mascara"));
        assertTrue(str.contains("123"));
        assertTrue(str.contains("3.30"));
    }
}
