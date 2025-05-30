package sv.edu.udb.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdenResponse {
    private Long id;
    private String cliente;
    private String telefono;
    private String correo;
    private String direccionEntrega;
    private String fecha;
    private String estado;
    private List<Detalle> detalles;

    @Data
    public static class Detalle {
        private Long productoId;
        private String producto;
        private String imagenUrl;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
    }
}
