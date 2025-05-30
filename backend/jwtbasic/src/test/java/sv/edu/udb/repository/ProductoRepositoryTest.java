package sv.edu.udb.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sv.edu.udb.model.Producto;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void testGuardarYBuscarProducto() {
        Producto producto = new Producto();
        producto.setNombre("Labial");
        producto.setPrecio(new java.math.BigDecimal("5.99"));
        producto.setStock(50);
        // agrega los campos necesarios

        producto = productoRepository.save(producto);

        assertNotNull(producto.getId());
        assertEquals("Labial", producto.getNombre());
    }
}
