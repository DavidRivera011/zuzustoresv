package sv.edu.udb.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void testBuilderAndGettersSetters() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan@mail.com")
                .contrasena("1234")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .telefono("12345678")
                .rol(Usuario.Rol.admin)
                .estado(Usuario.Estado.activo)
                .fechaIngreso(LocalDate.of(2024, 1, 1))
                .fechaSalida(LocalDate.of(2024, 12, 31))
                .salario(new BigDecimal("1500.00"))
                .build();

        assertEquals(1L, usuario.getId());
        assertEquals("Juan", usuario.getNombres());
        assertEquals("Pérez", usuario.getApellidos());
        assertEquals("juan@mail.com", usuario.getCorreo());
        assertEquals("1234", usuario.getContrasena());
        assertEquals(LocalDate.of(2000, 1, 1), usuario.getFechaNacimiento());
        assertEquals("12345678", usuario.getTelefono());
        assertEquals(Usuario.Rol.admin, usuario.getRol());
        assertEquals(Usuario.Estado.activo, usuario.getEstado());
        assertEquals(LocalDate.of(2024, 1, 1), usuario.getFechaIngreso());
        assertEquals(LocalDate.of(2024, 12, 31), usuario.getFechaSalida());
        assertEquals(new BigDecimal("1500.00"), usuario.getSalario());
    }

    @Test
    void testEqualsAndHashCode() {
        Usuario u1 = Usuario.builder()
                .id(1L)
                .correo("mail@mail.com")
                .build();
        Usuario u2 = Usuario.builder()
                .id(1L)
                .correo("mail@mail.com")
                .build();

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void testToString() {
        Usuario usuario = Usuario.builder().id(5L).correo("str@mail.com").build();
        String toString = usuario.toString();
        assertTrue(toString.contains("str@mail.com"));
        assertTrue(toString.contains("id=5"));
    }

    @Test
    void testUserDetailsMethodsActivo() {
        Usuario usuario = Usuario.builder()
                .estado(Usuario.Estado.activo)
                .correo("activo@mail.com")
                .contrasena("contraseña")
                .rol(Usuario.Rol.empleado)
                .build();

        Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.iterator().next() instanceof SimpleGrantedAuthority);
        assertEquals("ROLE_EMPLEADO", authorities.iterator().next().getAuthority());

        assertEquals("contraseña", usuario.getPassword());
        assertEquals("activo@mail.com", usuario.getUsername());
        assertTrue(usuario.isAccountNonExpired());
        assertTrue(usuario.isAccountNonLocked());
        assertTrue(usuario.isCredentialsNonExpired());
        assertTrue(usuario.isEnabled());
    }

    @Test
    void testUserDetailsMethodsInactivo() {
        Usuario usuario = Usuario.builder()
                .estado(Usuario.Estado.inactivo)
                .rol(Usuario.Rol.cliente)
                .build();

        assertFalse(usuario.isAccountNonExpired());
        assertFalse(usuario.isAccountNonLocked());
        assertFalse(usuario.isCredentialsNonExpired());
        assertFalse(usuario.isEnabled());
    }
}
