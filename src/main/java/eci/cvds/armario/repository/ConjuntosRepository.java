package eci.cvds.armario.repository;
import eci.cvds.armario.model.Conjuntos;
import eci.cvds.armario.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ConjuntosRepository extends JpaRepository<Conjuntos, UUID>{
    List<Conjuntos> findByUser(User user);
}
