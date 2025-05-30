package sv.edu.udb.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserDetailsServiceImpl userDetailsService;
    private JwtAuthenticationFilter filter;
    private FilterChain chain;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userDetailsService = mock(UserDetailsServiceImpl.class);
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        chain = mock(FilterChain.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        // Limpia el contexto de seguridad antes de cada test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_endpointPublico_noHaceNada() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/auth/login");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_sinHeaderAuthorization_noHaceNada() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/privado");
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_tokenValido_autenticaUsuario() throws Exception {
        String jwt = "testtoken";
        String correo = "test@correo.com";
        String rol = "admin";

        when(request.getServletPath()).thenReturn("/api/privado");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);

        when(jwtService.extractUsername(jwt)).thenReturn(correo);
        when(jwtService.extractClaim(eq(jwt), any())).thenReturn(rol);

        UserDetails userDetails = User.builder()
                .username(correo)
                .password("pass")
                .roles(rol.toUpperCase())
                .build();

        when(userDetailsService.loadUserByUsername(correo)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        // La autenticación debería estar en el contexto de seguridad
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(userDetails);
        assertThat(auth.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Test
    void doFilterInternal_tokenInvalido_noAutentica() throws Exception {
        String jwt = "tokeninvalido";
        String correo = "test@correo.com";
        String rol = "empleado";

        when(request.getServletPath()).thenReturn("/api/privado");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);

        when(jwtService.extractUsername(jwt)).thenReturn(correo);
        when(jwtService.extractClaim(eq(jwt), any())).thenReturn(rol);

        UserDetails userDetails = User.builder()
                .username(correo)
                .password("pass")
                .roles(rol.toUpperCase())
                .build();

        when(userDetailsService.loadUserByUsername(correo)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_yaAutenticado_noVuelveAAutenticar() throws Exception {
        // Contexto ya autenticado
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("ya", "ya", List.of())
        );
        String jwt = "otro";
        String correo = "otro@correo.com";

        when(request.getServletPath()).thenReturn("/api/privado");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(correo);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        // Se mantiene el authentication ya presente
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo("ya");
    }
}
