package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.model.Cliente;
import sv.edu.udb.repository.UsuarioRepository;
import sv.edu.udb.repository.ClienteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        var usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String rol = usuario.getRol().name();
            return new org.springframework.security.core.userdetails.User(
                    usuario.getCorreo(),
                    usuario.getContrasena(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()))
            );
        }

        var clienteOpt = clienteRepository.findByCorreo(correo);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    cliente.getCorreo(),
                    cliente.getContrasena(),
                    List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))
            );
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
    }
}
