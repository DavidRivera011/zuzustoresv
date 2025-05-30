package sv.edu.udb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.udb.dto.AuthResponse;
import sv.edu.udb.dto.LoginRequest;
import sv.edu.udb.dto.RegisterRequest;
import sv.edu.udb.model.Cliente;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.ClienteRepository;
import sv.edu.udb.repository.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Usuario usuario;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setNombre("Zheik");
        registerRequest.setApellido("Rivera");
        registerRequest.setCorreo("zheik@mail.com");
        registerRequest.setContrasena("1234");
        registerRequest.setFechaNacimiento("2020-01-01");
        registerRequest.setTelefono("12345678");

        loginRequest = new LoginRequest();
        loginRequest.setCorreo("zheik@mail.com");
        loginRequest.setContrasena("1234");

        usuario = Usuario.builder()
                .id(1L)
                .nombres("Zheik")
                .apellidos("Rivera")
                .correo("zheik@mail.com")
                .contrasena("encodedPassword")
                .rol(Usuario.Rol.cliente)
                .estado(Usuario.Estado.activo)
                .telefono("12345678")
                .build();

        cliente = new Cliente();
        cliente.setCorreo("zheik@mail.com");
        cliente.setContrasena("encodedPassword");
    }

    @Test
    void register_ShouldReturnToken() {
        when(passwordEncoder.encode("1234")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_AsUsuario_ShouldReturnToken() {
        when(usuarioRepository.findByCorreo("zheik@mail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(usuario)).thenReturn("jwt-usuario");

        AuthResponse response = authService.login(loginRequest);

        assertEquals("jwt-usuario", response.getToken());
    }

    @Test
    void login_AsCliente_ShouldReturnToken() {
        when(usuarioRepository.findByCorreo("zheik@mail.com")).thenReturn(Optional.empty());
        when(clienteRepository.findByCorreo("zheik@mail.com")).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("1234", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(cliente)).thenReturn("jwt-cliente");

        AuthResponse response = authService.login(loginRequest);

        assertEquals("jwt-cliente", response.getToken());
    }

    @Test
    void login_ShouldThrowException_WhenCorreoNotFound() {
        when(usuarioRepository.findByCorreo("zheik@mail.com")).thenReturn(Optional.empty());
        when(clienteRepository.findByCorreo("zheik@mail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIncorrect() {
        when(usuarioRepository.findByCorreo("zheik@mail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
    }
}
