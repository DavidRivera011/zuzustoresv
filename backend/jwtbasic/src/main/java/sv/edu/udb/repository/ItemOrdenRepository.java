package sv.edu.udb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.model.ItemOrden;

public interface ItemOrdenRepository extends JpaRepository<ItemOrden, Long> { }