package sv.edu.udb.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(nullable = false, length = 255)
    private String contrasena;

    private LocalDate fechaNacimiento;

    @Column(length = 20)
    private String telefono;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Estado estado = Estado.activo;

    public enum Estado {
        activo, inactivo
    }
}
