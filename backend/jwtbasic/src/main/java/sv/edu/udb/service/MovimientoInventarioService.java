package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.edu.udb.model.MovimientoInventario;
import sv.edu.udb.repository.MovimientoInventarioRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public List<MovimientoInventario> listarTodos() {
        return movimientoInventarioRepository.findAll();
    }

    public Optional<MovimientoInventario> buscarPorId(Long id) {
        return movimientoInventarioRepository.findById(id);
    }

    public MovimientoInventario guardar(MovimientoInventario movimiento) {
        return movimientoInventarioRepository.save(movimiento);
    }

    public void eliminar(Long id) {
        movimientoInventarioRepository.deleteById(id);
    }
}
