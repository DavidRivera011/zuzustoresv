package sv.edu.udb.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import jakarta.validation.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidLoginRequest() {
        LoginRequest loginRequest = new LoginRequest("zheik@mail.com", "1234");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.isEmpty(), "No debería haber violaciones para datos válidos");
    }

    @Test
    void testCorreoInvalido() {
        LoginRequest loginRequest = new LoginRequest("noesuncorreo", "1234");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("correo")));
    }

    @Test
    void testCorreoEnBlanco() {
        LoginRequest loginRequest = new LoginRequest("", "1234");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("correo")));
    }

    @Test
    void testContrasenaEnBlanco() {
        LoginRequest loginRequest = new LoginRequest("zheik@mail.com", "");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contrasena")));
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setCorreo("zheik@mail.com");
        loginRequest.setContrasena("1234");

        assertEquals("zheik@mail.com", loginRequest.getCorreo());
        assertEquals("1234", loginRequest.getContrasena());
    }
}
