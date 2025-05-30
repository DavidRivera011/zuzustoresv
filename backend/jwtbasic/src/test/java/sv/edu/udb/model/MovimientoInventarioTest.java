package sv.edu.udb.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoInventarioTest {

    @Test
    void testBuilderAndGettersSetters() {
        LocalDateTime fecha = LocalDateTime.of(2024, 5, 30, 10, 0);
        Producto producto = Producto.builder().id(2L).nombre("Polvo Compacto").build();
        Usuario empleado = Usuario.builder().id(5L).nombres("Karla").build();

        MovimientoInventario mov = MovimientoInventario.builder()
                .id(100L)
                .tipo(MovimientoInventario.TipoMovimiento.entrada)
                .cantidad(50)
                .fecha(fecha)
                .producto(producto)
                .empleado(empleado)
                .motivo("Reposición de stock")
                .stockResultante(150)
                .build();

        assertEquals(100L, mov.getId());
        assertEquals(MovimientoInventario.TipoMovimiento.entrada, mov.getTipo());
        assertEquals(50, mov.getCantidad());
        assertEquals(fecha, mov.getFecha());
        assertEquals(producto, mov.getProducto());
        assertEquals(empleado, mov.getEmpleado());
        assertEquals("Reposición de stock", mov.getMotivo());
        assertEquals(150, mov.getStockResultante());
    }

    @Test
    void testDefaultFecha() {
        MovimientoInventario mov = MovimientoInventario.builder().build();
        assertNotNull(mov.getFecha());
    }

    @Test
    void testEnumTipoMovimiento() {
        assertEquals("entrada", MovimientoInventario.TipoMovimiento.entrada.name());
        assertEquals("salida", MovimientoInventario.TipoMovimiento.salida.name());
        assertEquals("devolucion", MovimientoInventario.TipoMovimiento.devolucion.name());
    }

    @Test
    void testEqualsAndHashCode() {
        MovimientoInventario m1 = MovimientoInventario.builder().id(1L).build();
        MovimientoInventario m2 = MovimientoInventario.builder().id(1L).build();
        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void testToString() {
        MovimientoInventario mov = MovimientoInventario.builder().id(12L).motivo("Ajuste").build();
        String str = mov.toString();
        assertTrue(str.contains("Ajuste"));
        assertTrue(str.contains("12"));
    }
}
