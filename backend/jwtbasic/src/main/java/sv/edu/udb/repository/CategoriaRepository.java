package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
