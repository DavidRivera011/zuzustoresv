package sv.edu.udb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.config.SecurityConfigTest;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.model.Usuario.Estado;
import sv.edu.udb.model.Usuario.Rol;
import sv.edu.udb.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
    }

    @BeforeEach
    public void limpiarUsuarios() {
        usuarioRepository.deleteAll();
    }

    @Test
    void testGuardarUsuario() {
        Usuario usuario = Usuario.builder()
                .nombres("Carlos")
                .apellidos("Gómez")
                .correo("carlos@ejemplo.com")
                .contrasena("password123")
                .rol(Rol.empleado)
                .fechaNacimiento(LocalDate.of(1990, 2, 20))
                .telefono("22223333")
                .fechaIngreso(LocalDate.now())
                .salario(new BigDecimal("500.00"))
                .build();

        Usuario guardado = usuarioService.guardar(usuario);

        assertNotNull(guardado.getId());
        assertEquals("Carlos", guardado.getNombres());
        assertEquals(Rol.empleado, guardado.getRol());
        assertEquals(Estado.activo, guardado.getEstado());
    }

    @Test
    void testBuscarPorCorreo() {
        Usuario usuario = Usuario.builder()
                .nombres("Laura")
                .apellidos("Martínez")
                .correo("laura@ejemplo.com")
                .contrasena("clave")
                .rol(Rol.admin)
                .fechaNacimiento(LocalDate.of(1985, 7, 15))
                .telefono("77778888")
                .fechaIngreso(LocalDate.now())
                .salario(new BigDecimal("1200.00"))
                .build();

        usuarioService.guardar(usuario);

        Optional<Usuario> encontradoOpt = usuarioService.buscarPorCorreo("laura@ejemplo.com");
        assertTrue(encontradoOpt.isPresent());
        Usuario encontrado = encontradoOpt.get();
        assertEquals("Laura", encontrado.getNombres());
        assertEquals(Rol.admin, encontrado.getRol());
    }

    @Test
    void testListarUsuarios() {
        usuarioService.guardar(Usuario.builder()
                .nombres("A")
                .apellidos("Uno")
                .correo("a@uno.com")
                .contrasena("x")
                .rol(Rol.cliente)
                .fechaIngreso(LocalDate.now())
                .build());

        usuarioService.guardar(Usuario.builder()
                .nombres("B")
                .apellidos("Dos")
                .correo("b@dos.com")
                .contrasena("y")
                .rol(Rol.empleado)
                .fechaIngreso(LocalDate.now())
                .build());

        List<Usuario> usuarios = usuarioService.listarTodos();
        assertEquals(2, usuarios.size());

        usuarioService.listarTodos().forEach(u -> System.out.println(u.getCorreo() + " - " + u.getNombres()));
    }


}
