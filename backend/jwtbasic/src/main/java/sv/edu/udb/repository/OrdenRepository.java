package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.model.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

}