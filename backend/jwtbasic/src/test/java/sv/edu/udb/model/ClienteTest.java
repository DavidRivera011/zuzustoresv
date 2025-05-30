package sv.edu.udb.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void testBuilderAndGettersSetters() {
        LocalDate nacimiento = LocalDate.of(2000, 1, 1);
        LocalDate registro = LocalDate.of(2024, 5, 30);

        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombres("Ana")
                .apellidos("Gómez")
                .correo("ana@mail.com")
                .contrasena("password123")
                .fechaNacimiento(nacimiento)
                .telefono("12345678")
                .fechaRegistro(registro)
                .estado(Cliente.Estado.inactivo)
                .build();

        assertEquals(1L, cliente.getId());
        assertEquals("Ana", cliente.getNombres());
        assertEquals("Gómez", cliente.getApellidos());
        assertEquals("ana@mail.com", cliente.getCorreo());
        assertEquals("password123", cliente.getContrasena());
        assertEquals(nacimiento, cliente.getFechaNacimiento());
        assertEquals("12345678", cliente.getTelefono());
        assertEquals(registro, cliente.getFechaRegistro());
        assertEquals(Cliente.Estado.inactivo, cliente.getEstado());
    }

    @Test
    void testDefaultValuesWithBuilder() {
        Cliente cliente = Cliente.builder()
                .nombres("Luis")
                .apellidos("Pérez")
                .correo("luis@mail.com")
                .contrasena("password456")
                .build();

        assertEquals(Cliente.Estado.activo, cliente.getEstado());
    }

    @Test
    void testEnumEstado() {
        assertEquals("activo", Cliente.Estado.activo.name());
        assertEquals("inactivo", Cliente.Estado.inactivo.name());
    }

    @Test
    void testEqualsAndHashCode() {
        Cliente c1 = Cliente.builder().id(1L).correo("c@a.com").build();
        Cliente c2 = Cliente.builder().id(1L).correo("c@a.com").build();
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToString() {
        Cliente cliente = Cliente.builder().id(10L).nombres("Carlos").apellidos("Lopez").build();
        String str = cliente.toString();
        assertTrue(str.contains("Carlos"));
        assertTrue(str.contains("Lopez"));
        assertTrue(str.contains("10"));
    }
}
