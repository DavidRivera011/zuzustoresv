package sv.edu.udb.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrdenRequest {
    private Long clienteId;
    private String telefono;
    private String correo;
    private String direccionEntrega;
    private List<ItemCarrito> items;

    @Data
    public static class ItemCarrito {
        private Long productoId;
        private Integer cantidad;
    }
}
