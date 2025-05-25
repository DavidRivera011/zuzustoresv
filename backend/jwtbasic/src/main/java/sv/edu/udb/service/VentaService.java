package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.edu.udb.model.Venta;
import sv.edu.udb.repository.VentaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VentaService {
    private final VentaRepository ventaRepository;

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> buscarPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public Venta guardar(Venta venta) {
        return ventaRepository.save(venta);
    }

    public void eliminar(Long id) {
        ventaRepository.deleteById(id);
    }
}
