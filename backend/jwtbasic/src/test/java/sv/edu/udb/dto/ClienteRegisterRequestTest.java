package sv.edu.udb.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteRegisterRequestTest {

    @Test
    void testSettersAndGetters() {
        ClienteRegisterRequest request = new ClienteRegisterRequest();
        request.setNombres("Zheik");
        request.setApellidos("Rivera");
        request.setCorreo("zheik@mail.com");
        request.setContrasena("1234");
        request.setFechaNacimiento("2020-01-01");
        request.setTelefono("12345678");
        request.setFechaRegistro("2024-05-30");
        request.setEstado("activo");

        assertEquals("Zheik", request.getNombres());
        assertEquals("Rivera", request.getApellidos());
        assertEquals("zheik@mail.com", request.getCorreo());
        assertEquals("1234", request.getContrasena());
        assertEquals("2020-01-01", request.getFechaNacimiento());
        assertEquals("12345678", request.getTelefono());
        assertEquals("2024-05-30", request.getFechaRegistro());
        assertEquals("activo", request.getEstado());
    }

    @Test
    void testNoArgsConstructor() {
        ClienteRegisterRequest request = new ClienteRegisterRequest();
        assertNotNull(request);
    }
}
