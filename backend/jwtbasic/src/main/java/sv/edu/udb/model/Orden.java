package sv.edu.udb.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoOrden estado = EstadoOrden.pendiente;

    private String telefono;
    private String correo;
    private String direccionEntrega;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<DetalleOrden> detalles;

    @PrePersist
    public void prePersist() {
        if (fecha == null) fecha = LocalDateTime.now();
        if (telefono == null && cliente != null) telefono = cliente.getTelefono();
        if (correo == null && cliente != null) correo = cliente.getCorreo();
    }

    public enum EstadoOrden {
        pendiente,
        procesando,
        entregada,
        cancelada
    }
}
