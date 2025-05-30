package sv.edu.udb.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sv.edu.udb.model.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testFindByCorreo() {
        Usuario usuario = Usuario.builder()
                .nombres("Juan Carlos")
                .apellidos("Rivera")
                .correo("test@correo.com")
                .contrasena("12345")
                .rol(Usuario.Rol.empleado)
                .estado(Usuario.Estado.activo)
                .telefono("77778888")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .fechaIngreso(LocalDate.now())
                .salario(new BigDecimal("500.00"))
                .build();

        usuarioRepository.save(usuario);

        Optional<Usuario> result = usuarioRepository.findByCorreo("test@correo.com");
        assertTrue(result.isPresent());
        assertEquals("Juan Carlos", result.get().getNombres());
        assertEquals("Rivera", result.get().getApellidos());
        assertEquals(Usuario.Rol.empleado, result.get().getRol());
    }
}
