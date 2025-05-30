package sv.edu.udb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.udb.config.SecurityConfigTest;
import sv.edu.udb.dto.VentaRequest;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.model.Venta;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.service.JwtAuthenticationFilter;
import sv.edu.udb.service.VentaService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

@WebMvcTest(
        controllers = VentaController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(SecurityConfigTest.class)
@AutoConfigureMockMvc(addFilters = true)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private void setSecurityContext(UserDetails userDetails) {
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                "password",
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void listarTodas_DebeRetornarListaDeVentas() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaService.listarTodas()).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas")
                        .with(csrf())
                        .with(authenticationAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void crearVenta_DebeCrearVentaYRetornarOk() throws Exception {
        VentaRequest ventaRequest = new VentaRequest();

        Usuario empleado = new Usuario();
        empleado.setCorreo("empleado@correo.com");

        when(usuarioRepository.findByCorreo("empleado@correo.com")).thenReturn(Optional.of(empleado));

        UserDetails userDetails = User.builder()
                .username("empleado@correo.com")
                .password("password")
                .roles("EMPLEADO")
                .build();
        setSecurityContext(userDetails);

        mockMvc.perform(post("/api/ventas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ventaRequest)))
                .andExpect(status().isOk());
    }


    private static RequestPostProcessor authenticationAdmin() {
        return request -> {
            UserDetails userDetails = User.builder()
                    .username("admin@correo.com")
                    .password("password")
                    .roles("ADMIN")
                    .build();

            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    "password",
                    userDetails.getAuthorities()
            );
            request.setUserPrincipal(authentication);
            request.setAttribute("SPRING_SECURITY_CONTEXT", new SecurityContextImpl(authentication));
            return request;
        };
    }

}
