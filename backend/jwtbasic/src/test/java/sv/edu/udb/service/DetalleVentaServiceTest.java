package sv.edu.udb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.udb.model.DetalleVenta;
import sv.edu.udb.repository.DetalleVentaRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class DetalleVentaServiceTest {

    private DetalleVentaRepository detalleVentaRepository;
    private DetalleVentaService detalleVentaService;

    @BeforeEach
    void setUp() {
        detalleVentaRepository = mock(DetalleVentaRepository.class);
        detalleVentaService = new DetalleVentaService(detalleVentaRepository);
    }

    @Test
    void listarTodos_debeRetornarListaDetalleVentas() {
        DetalleVenta d1 = new DetalleVenta();
        DetalleVenta d2 = new DetalleVenta();
        when(detalleVentaRepository.findAll()).thenReturn(List.of(d1, d2));

        List<DetalleVenta> resultado = detalleVentaService.listarTodos();

        assertThat(resultado).containsExactly(d1, d2);
        verify(detalleVentaRepository).findAll();
    }

    @Test
    void buscarPorId_encontrado() {
        DetalleVenta d = new DetalleVenta();
        when(detalleVentaRepository.findById(7L)).thenReturn(Optional.of(d));

        Optional<DetalleVenta> resultado = detalleVentaService.buscarPorId(7L);

        assertThat(resultado).isPresent().contains(d);
        verify(detalleVentaRepository).findById(7L);
    }

    @Test
    void buscarPorId_noEncontrado() {
        when(detalleVentaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<DetalleVenta> resultado = detalleVentaService.buscarPorId(999L);

        assertThat(resultado).isNotPresent();
        verify(detalleVentaRepository).findById(999L);
    }

    @Test
    void guardar_detalleVenta() {
        DetalleVenta d = new DetalleVenta();
        when(detalleVentaRepository.save(d)).thenReturn(d);

        DetalleVenta resultado = detalleVentaService.guardar(d);

        assertThat(resultado).isSameAs(d);
        verify(detalleVentaRepository).save(d);
    }

    @Test
    void eliminar_detalleVentaPorId() {
        detalleVentaService.eliminar(44L);

        verify(detalleVentaRepository).deleteById(44L);
    }

    @Test
    void buscarPorVentaId_debeRetornarListaPorVenta() {
        DetalleVenta d1 = new DetalleVenta();
        DetalleVenta d2 = new DetalleVenta();
        when(detalleVentaRepository.findByVenta_Id(5L)).thenReturn(List.of(d1, d2));

        List<DetalleVenta> resultado = detalleVentaService.buscarPorVentaId(5L);

        assertThat(resultado).containsExactly(d1, d2);
        verify(detalleVentaRepository).findByVenta_Id(5L);
    }
}
