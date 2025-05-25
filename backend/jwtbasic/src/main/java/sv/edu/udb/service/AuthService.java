package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sv.edu.udb.dto.AuthResponse;
import sv.edu.udb.dto.LoginRequest;
import sv.edu.udb.dto.RegisterRequest;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponse register(RegisterRequest request) {
        Usuario usuario = Usuario.builder()
                .nombres(request.getNombre())
                .apellidos(request.getApellido())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .rol(
                        request.getRol() != null && request.getRol().equalsIgnoreCase("admin")
                                ? Usuario.Rol.admin
                                : Usuario.Rol.empleado
                )
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
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContrasena())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Credenciales inválidas");
        }

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token);
    }
}
