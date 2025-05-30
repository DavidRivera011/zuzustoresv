package sv.edu.udb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.dto.ClienteRegisterRequest;
import sv.edu.udb.model.Cliente;
import sv.edu.udb.repository.ClienteRepository;
import sv.edu.udb.service.JwtAuthenticationFilter;
import sv.edu.udb.service.JwtService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Import;
import sv.edu.udb.config.SecurityConfigTest;

@WebMvcTest(
        controllers = ClienteController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(SecurityConfigTest.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrarCliente_CuandoCorreoYaExiste_DebeRetornarBadRequest() throws Exception {
        ClienteRegisterRequest request = new ClienteRegisterRequest();
        request.setNombres("Juan");
        request.setApellidos("Pérez");
        request.setCorreo("juan@correo.com");
        request.setContrasena("123456");
        request.setTelefono("12345678");

        when(clienteRepository.findByCorreo("juan@correo.com"))
                .thenReturn(Optional.of(new Cliente()));

        mockMvc.perform(post("/api/clientes/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El correo ya está registrado."));
    }

    @Test
    void registrarCliente_CuandoTodoCorrecto_DebeRetornarOk() throws Exception {
        ClienteRegisterRequest request = new ClienteRegisterRequest();
        request.setNombres("Maria");
        request.setApellidos("Lopez");
        request.setCorreo("maria@correo.com");
        request.setContrasena("abcdef");
        request.setTelefono("87654321");

        when(clienteRepository.findByCorreo("maria@correo.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("abcdef"))
                .thenReturn("encoded-password");
        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/clientes/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cliente registrado exitosamente"));
    }
}
