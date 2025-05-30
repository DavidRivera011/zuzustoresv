package sv.edu.udb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import sv.edu.udb.model.Cliente;
import sv.edu.udb.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class ClienteServiceTest {

    private ClienteRepository clienteRepository;
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteRepository = mock(ClienteRepository.class);
        clienteService = new ClienteService(clienteRepository);
    }

    @Test
    void listarTodos_debeRetornarListaClientes() {
        Cliente c1 = new Cliente();
        Cliente c2 = new Cliente();
        when(clienteRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Cliente> resultado = clienteService.listarTodos();

        assertThat(resultado).containsExactly(c1, c2);
        verify(clienteRepository).findAll();
    }

    @Test
    void buscarPorId_encontrado() {
        Cliente c = new Cliente();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(c));

        Optional<Cliente> resultado = clienteService.buscarPorId(1L);

        assertThat(resultado).isPresent().contains(c);
        verify(clienteRepository).findById(1L);
    }

    @Test
    void buscarPorId_noEncontrado() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.buscarPorId(99L);

        assertThat(resultado).isNotPresent();
        verify(clienteRepository).findById(99L);
    }

    @Test
    void buscarPorCorreo_encontrado() {
        Cliente c = new Cliente();
        when(clienteRepository.findByCorreo("correo@test.com")).thenReturn(Optional.of(c));

        Optional<Cliente> resultado = clienteService.buscarPorCorreo("correo@test.com");

        assertThat(resultado).isPresent().contains(c);
        verify(clienteRepository).findByCorreo("correo@test.com");
    }

    @Test
    void buscarPorCorreo_noEncontrado() {
        when(clienteRepository.findByCorreo("x@test.com")).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.buscarPorCorreo("x@test.com");

        assertThat(resultado).isNotPresent();
        verify(clienteRepository).findByCorreo("x@test.com");
    }

    @Test
    void guardar_cliente() {
        Cliente c = new Cliente();
        when(clienteRepository.save(c)).thenReturn(c);

        Cliente resultado = clienteService.guardar(c);

        assertThat(resultado).isSameAs(c);
        verify(clienteRepository).save(c);
    }

    @Test
    void eliminar_clientePorId() {
        clienteService.eliminar(33L);

        verify(clienteRepository).deleteById(33L);
    }
}
