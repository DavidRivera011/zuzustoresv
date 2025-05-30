package sv.edu.udb.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
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

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;


    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrden> items;

    public enum EstadoOrden {
        pendiente, en_proceso, finalizada, cancelada
    }
}
