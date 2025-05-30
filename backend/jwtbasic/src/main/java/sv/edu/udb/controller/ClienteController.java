package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.dto.ClienteRegisterRequest;
import sv.edu.udb.model.Cliente;
import sv.edu.udb.repository.ClienteRepository;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registrarCliente(@RequestBody ClienteRegisterRequest request) {
        if (clienteRepository.findByCorreo(request.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está registrado.");
        }
        Cliente cliente = Cliente.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .telefono(request.getTelefono())
                .fechaNacimiento(
                        request.getFechaNacimiento() != null && !request.getFechaNacimiento().isEmpty()
                                ? java.time.LocalDate.parse(request.getFechaNacimiento())
                                : null
                )
                .estado(Cliente.Estado.activo)
                .build();
        clienteRepository.save(cliente);
        return ResponseEntity.ok("Cliente registrado exitosamente");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<?> obtenerCliente(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> editarCliente(@PathVariable Long id, @RequestBody ClienteRegisterRequest request) {
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setNombres(request.getNombres());
            cliente.setApellidos(request.getApellidos());
            cliente.setCorreo(request.getCorreo());
            if (request.getContrasena() != null && !request.getContrasena().isEmpty()) {
                cliente.setContrasena(passwordEncoder.encode(request.getContrasena()));
            }
            cliente.setTelefono(request.getTelefono());
            if (request.getFechaNacimiento() != null && !request.getFechaNacimiento().isEmpty()) {
                cliente.setFechaNacimiento(java.time.LocalDate.parse(request.getFechaNacimiento()));
            }
            if (request.getEstado() != null) {
                try {
                    cliente.setEstado(Cliente.Estado.valueOf(request.getEstado()));
                } catch (IllegalArgumentException e) {
                }
            }
            clienteRepository.save(cliente);
            return ResponseEntity.ok("Cliente actualizado exitosamente");
        }).orElse(ResponseEntity.notFound().build());
    }
}