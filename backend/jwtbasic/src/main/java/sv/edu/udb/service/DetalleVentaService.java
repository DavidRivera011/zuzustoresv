package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.edu.udb.model.DetalleVenta;
import sv.edu.udb.repository.DetalleVentaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetalleVentaService {
    private final DetalleVentaRepository detalleVentaRepository;

    public List<DetalleVenta> listarTodos() {
        return detalleVentaRepository.findAll();
    }

    public Optional<DetalleVenta> buscarPorId(Long id) {
        return detalleVentaRepository.findById(id);
    }

    public DetalleVenta guardar(DetalleVenta detalleVenta) {
        return detalleVentaRepository.save(detalleVenta);
    }

    public void eliminar(Long id) {
        detalleVentaRepository.deleteById(id);
    }

    public List<DetalleVenta> buscarPorVentaId(Long ventaId) {
        return detalleVentaRepository.findByVenta_Id(ventaId);
    }

}
