package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sv.edu.udb.dto.AuthResponse;
import sv.edu.udb.dto.LoginRequest;
import sv.edu.udb.dto.RegisterRequest;
import sv.edu.udb.model.Cliente;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.repository.ClienteRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponse register(RegisterRequest request) {
        Usuario usuario = Usuario.builder()
                .nombres(request.getNombre())
                .apellidos(request.getApellido())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .rol(Usuario.Rol.cliente)
                .estado(Usuario.Estado.activo)
                .telefono(request.getTelefono())
                .fechaNacimiento(
                        request.getFechaNacimiento() != null && !request.getFechaNacimiento().isEmpty()
                                ? java.time.LocalDate.parse(request.getFechaNacimiento())
                                : null
                )
                .build();

        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(request.getCorreo());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
                String token = jwtService.generateToken(usuario);
                return new AuthResponse(token);
            } else {
                throw new RuntimeException("Credenciales inválidas");
            }
        }

        Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(request.getCorreo());
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            if (passwordEncoder.matches(request.getContrasena(), cliente.getContrasena())) {
                String token = jwtService.generateToken(cliente);
                return new AuthResponse(token);
            } else {
                throw new RuntimeException("Credenciales inválidas");
            }
        }

        throw new RuntimeException("Credenciales inválidas");
    }
}
