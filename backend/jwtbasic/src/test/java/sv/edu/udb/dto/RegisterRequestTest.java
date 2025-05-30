package sv.edu.udb.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import jakarta.validation.*;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRegisterRequest() {
        RegisterRequest req = new RegisterRequest(
                "Zheik",
                "Rivera",
                "zheik@mail.com",
                "12345678",
                "admin",
                "2000-01-01",
                "12345678",
                "2024-05-30",
                "2024-12-31",
                new BigDecimal("1000.00")
        );
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(), "No debería haber violaciones para datos válidos");
    }

    @Test
    void testNombreEsObligatorio() {
        RegisterRequest req = new RegisterRequest(
                "",
                "Rivera",
                "zheik@mail.com",
                "12345678",
                "admin",
                "2000-01-01",
                "12345678",
                "2024-05-30",
                "2024-12-31",
                new BigDecimal("1000.00")
        );
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }

    @Test
    void testApellidoEsObligatorio() {
        RegisterRequest req = new RegisterRequest(
                "Zheik",
                "",
                "zheik@mail.com",
                "12345678",
                "admin",
                "2000-01-01",
                "12345678",
                "2024-05-30",
                "2024-12-31",
                new BigDecimal("1000.00")
        );
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("apellido")));
    }

    @Test
    void testCorreoEsObligatorioYValido() {
        RegisterRequest req1 = new RegisterRequest(
                "Zheik",
                "Rivera",
                "",
                "12345678",
                "admin",
                "2000-01-01",
                "12345678",
                "2024-05-30",
                "2024-12-31",
                new BigDecimal("1000.00")
        );
        Set<ConstraintViolation<RegisterRequest>> violations1 = validator.validate(req1);
        assertFalse(violations1.isEmpty());
        assertTrue(violations1.stream().anyMatch(v -> v.getPropertyPath().toString().equals("correo")));

        RegisterRequest req2 = new RegisterRequest(
                "Zheik",
                "Rivera",
                "noesunemail",
                "12345678",
                "admin",
                "2000-01-01",
                "12345678",
                "2024-05-30",
                "2024-12-31",
                new BigDecimal("1000.00")
        );
        Set<ConstraintViolation<RegisterRequest>> violations2 = validator.validate(req2);
        assertFalse(violations2.isEmpty());
        assertTrue(violations2.stream().anyMatch(v -> v.getPropertyPath().toString().equals("correo")));
    }

    @Test
    void testContrasenaEsObligatoriaYMinimo8Caracteres() {
        RegisterRequest req1 = new RegisterRequest(
                "Zheik",
                "Rivera",
                "zheik@mail.com",
                "",
                "admin",
                "2000-01-01",
                "12345678",
                "2024-05-30",
                "2024-12-31",
                new BigDecimal("1000.00")
        );
        Set<ConstraintViolation<RegisterRequest>> violations1 = validator.validate(req1);
        assertFalse(violations1.isEmpty());
        assertTrue(violations1.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contrasena")));

        RegisterRequest req2 = new RegisterRequest(
                "Zheik",
                "Rivera",
                "zheik@mail.com",
                "1234567", // solo 7 caracteres
                "admin",
                "2000-01-01",
                "12345678",
                "2024-05-30",
                "2024-12-31",
                new BigDecimal("1000.00")
        );
        Set<ConstraintViolation<RegisterRequest>> violations2 = validator.validate(req2);
        assertFalse(violations2.isEmpty());
        assertTrue(violations2.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contrasena")));
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        RegisterRequest req = new RegisterRequest();
        req.setNombre("Zheik");
        req.setApellido("Rivera");
        req.setCorreo("zheik@mail.com");
        req.setContrasena("12345678");
        req.setRol("admin");
        req.setFechaNacimiento("2000-01-01");
        req.setTelefono("12345678");
        req.setFechaIngreso("2024-05-30");
        req.setFechaSalida("2024-12-31");
        req.setSalario(new BigDecimal("1000.00"));

        assertEquals("Zheik", req.getNombre());
        assertEquals("Rivera", req.getApellido());
        assertEquals("zheik@mail.com", req.getCorreo());
        assertEquals("12345678", req.getContrasena());
        assertEquals("admin", req.getRol());
        assertEquals("2000-01-01", req.getFechaNacimiento());
        assertEquals("12345678", req.getTelefono());
        assertEquals("2024-05-30", req.getFechaIngreso());
        assertEquals("2024-12-31", req.getFechaSalida());
        assertEquals(new BigDecimal("1000.00"), req.getSalario());
    }
}
