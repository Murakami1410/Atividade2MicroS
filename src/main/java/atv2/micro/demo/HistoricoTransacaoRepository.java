package atv2.micro.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; 
import java.util.List;

@Repository
public interface HistoricoTransacaoRepository extends JpaRepository<HistoricoTransacaoEntity, Long> {
        
    List<HistoricoTransacaoEntity> findByCidadaoId(Long cidadaoId);
}