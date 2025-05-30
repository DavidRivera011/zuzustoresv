package sv.edu.udb.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.dto.AuthResponse;
import sv.edu.udb.dto.LoginRequest;
import sv.edu.udb.dto.RegisterRequest;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.ClienteRepository;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.service.AuthService;
import sv.edu.udb.service.UsuarioService;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO','CLIENTE')")
    @GetMapping("/me")
    public Object getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String correo = authentication.getName();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isPresent()) {
            return usuarioOpt.get();
        }

        return clienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
