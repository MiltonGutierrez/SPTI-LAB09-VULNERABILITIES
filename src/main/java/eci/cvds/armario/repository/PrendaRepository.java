package eci.cvds.armario.repository;

import eci.cvds.armario.model.Prenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PrendaRepository extends JpaRepository<Prenda, UUID> {
    Prenda findByPrendaId(UUID uuid);
}
