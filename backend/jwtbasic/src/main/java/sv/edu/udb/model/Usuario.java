package sv.edu.udb.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {
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

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Estado estado = Estado.activo;

    public enum Rol {
        admin, empleado
    }

    public enum Estado {
        activo, inactivo
    }

    // Implementación de UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna el rol como autoridad de Spring Security
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.rol.name().toUpperCase()));
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.estado == Estado.activo;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.estado == Estado.activo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.estado == Estado.activo;
    }

    @Override
    public boolean isEnabled() {
        return this.estado == Estado.activo;
    }
}
