package sv.edu.udb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.UsuarioRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UsuarioRepository usuarioRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
    }

    @Test
    void loadUserByUsername_usuarioExiste_devuelveUserDetails() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@correo.com");
        usuario.setContrasena("1234");
        usuario.setRol(Usuario.Rol.admin);

        when(usuarioRepository.findByCorreo("test@correo.com"))
                .thenReturn(Optional.of(usuario));

        UserDetails result = userDetailsService.loadUserByUsername("test@correo.com");

        assertThat(result.getUsername()).isEqualTo("test@correo.com");
        assertThat(result.getPassword()).isEqualTo("1234");
        assertThat(result.getAuthorities()).extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_usuarioNoExiste_lanzaExcepcion() {
        when(usuarioRepository.findByCorreo("noexiste@correo.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userDetailsService.loadUserByUsername("noexiste@correo.com")
        )
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }
}
