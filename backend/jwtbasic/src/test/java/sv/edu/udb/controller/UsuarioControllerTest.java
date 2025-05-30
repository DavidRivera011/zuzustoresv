package sv.edu.udb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.config.SecurityConfigTest;
import sv.edu.udb.dto.RegisterRequest;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.service.JwtService;
import sv.edu.udb.service.UserDetailsServiceImpl;
import sv.edu.udb.service.UsuarioService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@WithMockUser(roles = "ADMIN")
@Import(SecurityConfigTest.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListarEmpleados() throws Exception {
        mockMvc.perform(get("/api/usuarios/empleados"))
                .andExpect(status().isOk());
    }

    @Test
    void testRegistrarEmpleado() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Zheik");
        request.setApellido("Rivera");
        request.setCorreo("zheik@test.com");
        request.setContrasena("12345678");
        request.setRol("empleado");
        request.setTelefono("12345678");
        request.setFechaNacimiento("2000-01-01");
        request.setFechaIngreso("2024-01-01");
        request.setSalario(new BigDecimal("500"));

        when(usuarioRepository.findByCorreo("zheik@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("12345678")).thenReturn("encodedPassword");

        mockMvc.perform(post("/api/usuarios/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarEmpleado() throws Exception {
        Long empleadoId = 1L;

        RegisterRequest request = new RegisterRequest();
        request.setNombre("Zheik");
        request.setApellido("Rivera");
        request.setCorreo("nuevo@test.com");
        request.setContrasena("newpass");
        request.setTelefono("87654321");
        request.setFechaNacimiento("2001-01-01");
        request.setFechaIngreso("2023-01-01");
        request.setFechaSalida("2025-01-01");
        request.setSalario(new BigDecimal("700"));

        Usuario empleado = new Usuario();
        empleado.setId(empleadoId);

        when(usuarioRepository.findById(empleadoId)).thenReturn(Optional.of(empleado));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedPass");

        mockMvc.perform(put("/api/usuarios/empleados/{id}", empleadoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarEmpleado() throws Exception {
        Long empleadoId = 1L;
        when(usuarioRepository.existsById(empleadoId)).thenReturn(true);

        mockMvc.perform(delete("/api/usuarios/empleados/{id}", empleadoId))
                .andExpect(status().isOk());
    }
}
