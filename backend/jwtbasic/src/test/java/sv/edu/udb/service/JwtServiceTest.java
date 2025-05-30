package sv.edu.udb.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import sv.edu.udb.model.Usuario;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String jwtSecret = "miSuperClaveJWTdeSeguridadSegura123456";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 1000 * 60 * 60);
        jwtService.init();
    }

    @Test
    void testGenerateAndValidateToken() {
        Usuario user = Usuario.builder()
                .id(1L)
                .nombres("Zheik")
                .apellidos("Rivera")
                .correo("zheik@prueba.com")
                .rol(Usuario.Rol.empleado)
                .contrasena("pass")
                .build();

        String token = jwtService.generateToken(user);
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("zheik@prueba.com", username);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void testTokenExpired() {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.extractUsername(expiredToken);
        });
    }

    @Test
    void testExtractInvalidTokenThrowsException() {
        assertThrows(Exception.class, () -> {
            jwtService.extractUsername("invalid.token.value");
        });
    }
}
