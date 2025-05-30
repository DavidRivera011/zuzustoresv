package sv.edu.udb.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthResponseTest {

    @Test
    @DisplayName("Debe construir correctamente y exponer el token")
    void testConstructorYGetters() {
        String expectedToken = "test-jwt-token";
        AuthResponse response = new AuthResponse(expectedToken);

        assertThat(response.getToken()).isEqualTo(expectedToken);
    }

    @Test
    @DisplayName("Debe permitir modificar el token con setter")
    void testSetToken() {
        AuthResponse response = new AuthResponse("initial-token");
        response.setToken("nuevo-token");

        assertThat(response.getToken()).isEqualTo("nuevo-token");
    }

    @Test
    @DisplayName("Equals y hashCode funcionan correctamente")
    void testEqualsAndHashCode() {
        AuthResponse r1 = new AuthResponse("token-123");
        AuthResponse r2 = new AuthResponse("token-123");
        AuthResponse r3 = new AuthResponse("otro-token");

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).hasSameHashCodeAs(r2);
        assertThat(r1).isNotEqualTo(r3);
    }

    @Test
    @DisplayName("toString debe incluir el token")
    void testToString() {
        AuthResponse response = new AuthResponse("tokentest");
        assertThat(response.toString()).contains("tokentest");
    }
}
