package sv.edu.udb.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VentaRequestTest {

    @Test
    void testVentaRequest_SettersAndGetters() {
        // Crear producto detalle
        VentaRequest.DetalleProductoDTO detalle1 = new VentaRequest.DetalleProductoDTO();
        detalle1.setProductoId(1L);
        detalle1.setCantidad(2);

        VentaRequest.DetalleProductoDTO detalle2 = new VentaRequest.DetalleProductoDTO();
        detalle2.setProductoId(2L);
        detalle2.setCantidad(5);

        List<VentaRequest.DetalleProductoDTO> productos = Arrays.asList(detalle1, detalle2);

        VentaRequest ventaRequest = new VentaRequest();
        ventaRequest.setClienteId(10L);
        ventaRequest.setProductos(productos);
        ventaRequest.setEstado("entregado");

        assertEquals(10L, ventaRequest.getClienteId());
        assertEquals(2, ventaRequest.getProductos().size());
        assertEquals("entregado", ventaRequest.getEstado());

        assertEquals(1L, ventaRequest.getProductos().get(0).getProductoId());
        assertEquals(2, ventaRequest.getProductos().get(0).getCantidad());
        assertEquals(2L, ventaRequest.getProductos().get(1).getProductoId());
        assertEquals(5, ventaRequest.getProductos().get(1).getCantidad());
    }

    @Test
    void testDetalleProductoDTO_SettersAndGetters() {
        VentaRequest.DetalleProductoDTO detalle = new VentaRequest.DetalleProductoDTO();
        detalle.setProductoId(123L);
        detalle.setCantidad(7);

        assertEquals(123L, detalle.getProductoId());
        assertEquals(7, detalle.getCantidad());
    }

    @Test
    void testVentaRequest_EmptyProductos() {
        VentaRequest ventaRequest = new VentaRequest();
        ventaRequest.setClienteId(20L);
        ventaRequest.setProductos(null);
        ventaRequest.setEstado("pendiente");

        assertEquals(20L, ventaRequest.getClienteId());
        assertNull(ventaRequest.getProductos());
        assertEquals("pendiente", ventaRequest.getEstado());
    }
}
