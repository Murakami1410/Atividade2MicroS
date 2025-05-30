package atv2.micro.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagamentoIptuRepository extends JpaRepository<PagamentoIptuEntity, Long> {
    
       List<PagamentoIptuEntity> findByIdCidadao(int idCidadao);
        long countDistinctByIdCidadao();
        List<PagamentoIptuEntity> findByIdCidadaoAndDataVencimentoBetween(int idCidadao, LocalDate inicioAno, LocalDate fimAno);
        boolean existsByIdCidadaoAndDataVencimentoBetween(int idCidadao, LocalDate inicioAno, LocalDate fimAno);
}
