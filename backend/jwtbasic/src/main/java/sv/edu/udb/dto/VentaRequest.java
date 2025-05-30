package sv.edu.udb.dto;

import lombok.Data;
import java.util.List;

@Data
public class VentaRequest {
    private Long clienteId;
    private List<DetalleProductoDTO> productos;
    private String estado;

    @Data
    public static class DetalleProductoDTO {
        private Long productoId;
        private Integer cantidad;
    }
}
