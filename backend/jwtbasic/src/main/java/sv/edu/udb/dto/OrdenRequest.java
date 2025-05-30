package sv.edu.udb.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrdenRequest {
    private Long clienteId;
    private List<ItemRequest> items;

    @Data
    public static class ItemRequest {
        private Long productoId;
        private Integer cantidad;
    }
}
