package sv.edu.udb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.UsuarioRepository;

@SpringBootApplication
public class JwtbasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtbasicApplication.class, args);
    }
    /*
    @Bean
    public CommandLineRunner initAdmin(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!usuarioRepository.existsByEmail("admin@semita.sv")) {
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setEmail("admin@semita.sv");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ADMIN");
                admin.setRegistradoPorGithub(false);
                usuarioRepository.save(admin);
                System.out.println("🛡️ Usuario admin creado.");
            } else {
                System.out.println("⚠️ Admin ya existe, no se creó otro.");
            }
        };
    }
     */

}
