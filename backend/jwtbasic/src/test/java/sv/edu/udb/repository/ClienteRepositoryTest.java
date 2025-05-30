package sv.edu.udb.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sv.edu.udb.model.Cliente;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    @DisplayName("Guardar y recuperar cliente por correo")
    void testGuardarYBuscarPorCorreo() {
        Cliente cliente = Cliente.builder()
                .nombres("Ana")
                .apellidos("López")
                .correo("ana@correo.com")
                .contrasena("segura123")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .telefono("55512345")
                .fechaRegistro(LocalDate.now())
                .estado(Cliente.Estado.activo)
                .build();

        clienteRepository.save(cliente);

        Optional<Cliente> encontrado = clienteRepository.findByCorreo("ana@correo.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombres()).isEqualTo("Ana");
        assertThat(encontrado.get().getEstado()).isEqualTo(Cliente.Estado.activo);
    }

    @Test
    @DisplayName("No debe encontrar cliente con correo inexistente")
    void testBuscarPorCorreoInexistente() {
        Optional<Cliente> encontrado = clienteRepository.findByCorreo("noexiste@correo.com");
        assertThat(encontrado).isNotPresent();
    }

    @Test
    @DisplayName("No debe permitir correos duplicados")
    void testCorreoUnico() {
        Cliente c1 = Cliente.builder()
                .nombres("Luis")
                .apellidos("Martínez")
                .correo("luis@correo.com")
                .contrasena("clave1")
                .build();
        clienteRepository.save(c1);

        Cliente c2 = Cliente.builder()
                .nombres("Lucía")
                .apellidos("Martínez")
                .correo("luis@correo.com")
                .contrasena("clave2")
                .build();

        assertThrows(Exception.class, () -> {
            clienteRepository.saveAndFlush(c2);
        });
    }

    @Test
    @DisplayName("Actualizar datos de un cliente")
    void testActualizarCliente() {
        Cliente cliente = Cliente.builder()
                .nombres("Carlos")
                .apellidos("García")
                .correo("carlos@correo.com")
                .contrasena("passcarlos")
                .estado(Cliente.Estado.inactivo)
                .build();

        Cliente guardado = clienteRepository.save(cliente);

        guardado.setNombres("Carlos Alberto");
        guardado.setEstado(Cliente.Estado.activo);
        clienteRepository.save(guardado);

        Optional<Cliente> actualizado = clienteRepository.findById(guardado.getId());

        assertThat(actualizado).isPresent();
        assertThat(actualizado.get().getNombres()).isEqualTo("Carlos Alberto");
        assertThat(actualizado.get().getEstado()).isEqualTo(Cliente.Estado.activo);
    }

    @Test
    @DisplayName("Permitir campos nulos opcionales")
    void testCamposNulos() {
        Cliente cliente = Cliente.builder()
                .nombres("Julia")
                .apellidos("Ramos")
                .correo("julia@correo.com")
                .contrasena("julia123")
                .build();

        Cliente guardado = clienteRepository.save(cliente);

        Optional<Cliente> encontrado = clienteRepository.findByCorreo("julia@correo.com");
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getTelefono()).isNull();
        assertThat(encontrado.get().getFechaNacimiento()).isNull();
        assertThat(encontrado.get().getFechaRegistro()).isNull();
    }
}
