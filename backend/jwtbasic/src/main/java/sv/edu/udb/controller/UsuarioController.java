package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.dto.RegisterRequest;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.ProductoRepository;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.repository.VentaRepository;
import sv.edu.udb.service.UsuarioService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // LISTAR empleados
    @GetMapping("/empleados")
    public List<Usuario> listarEmpleados() {
        return usuarioRepository.findAll();
    }

    // REGISTRAR nuevo empleado
    @PostMapping("/empleados")
    public ResponseEntity<?> registrarEmpleado(@RequestBody RegisterRequest request) {
        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está registrado.");
        }
        // Toma el rol desde la request, default a empleado si no viene
        Usuario.Rol rol = Usuario.Rol.empleado;
        if (request.getRol() != null && request.getRol().equalsIgnoreCase("admin")) {
            rol = Usuario.Rol.admin;
        }
        Usuario empleado = Usuario.builder()
                .nombres(request.getNombre())
                .apellidos(request.getApellido())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .rol(rol)
                .estado(Usuario.Estado.activo)
                .telefono(request.getTelefono())
                .fechaNacimiento(
                        request.getFechaNacimiento() != null && !request.getFechaNacimiento().isEmpty()
                                ? LocalDate.parse(request.getFechaNacimiento())
                                : null
                )
                .fechaIngreso(
                        request.getFechaIngreso() != null && !request.getFechaIngreso().isEmpty()
                                ? LocalDate.parse(request.getFechaIngreso())
                                : LocalDate.now()
                )
                .salario(request.getSalario() != null ? request.getSalario() : BigDecimal.ZERO)
                .build();
        usuarioRepository.save(empleado);
        return ResponseEntity.ok("Empleado registrado exitosamente");
    }


    // ACTUALIZAR empleado
    @PutMapping("/empleados/{id}")
    public ResponseEntity<?> actualizarEmpleado(@PathVariable Long id, @RequestBody RegisterRequest request) {
        Optional<Usuario> opEmpleado = usuarioRepository.findById(id);
        if (opEmpleado.isEmpty()) return ResponseEntity.notFound().build();

        Usuario empleado = opEmpleado.get();
        empleado.setNombres(request.getNombre());
        empleado.setApellidos(request.getApellido());
        empleado.setCorreo(request.getCorreo());

        if (request.getContrasena() != null && !request.getContrasena().isEmpty()) {
            empleado.setContrasena(passwordEncoder.encode(request.getContrasena()));
        }
        empleado.setTelefono(request.getTelefono());
        empleado.setFechaNacimiento(
                request.getFechaNacimiento() != null && !request.getFechaNacimiento().isEmpty()
                        ? java.time.LocalDate.parse(request.getFechaNacimiento())
                        : null
        );
        empleado.setFechaIngreso(
                request.getFechaIngreso() != null && !request.getFechaIngreso().isEmpty()
                        ? java.time.LocalDate.parse(request.getFechaIngreso())
                        : null
        );
        empleado.setFechaSalida(
                request.getFechaSalida() != null && !request.getFechaSalida().isEmpty()
                        ? java.time.LocalDate.parse(request.getFechaSalida())
                        : null
        );
        empleado.setSalario(request.getSalario());

        usuarioRepository.save(empleado);
        return ResponseEntity.ok("Empleado actualizado exitosamente");
    }


    // ELIMINAR empleado
    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<?> eliminarEmpleado(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok("Empleado eliminado exitosamente");
    }
}

