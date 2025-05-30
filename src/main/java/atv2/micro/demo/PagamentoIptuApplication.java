package atv2.micro.demo;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PagamentoIptuApplication {

    @Autowired
    private PagamentoIptuRepository pagamentoIptuRepository;

    @Autowired
    private MeterRegistry meterRegistry; 

    @Transactional
    public List<PagamentoIptuEntity> gerarParcelas(int idCidadao, boolean pagamentoUnico) {
        LocalDate hoje = LocalDate.now();
        
        
        if (pagamentoIptuRepository.existsByIdCidadaoAndDataVencimentoBetween(idCidadao, hoje.withDayOfYear(1), hoje.withDayOfYear(366))) {
            throw new IllegalStateException("Já existem parcelas geradas para este cidadão neste ano.");
        }

        for (int i = 1; i <= 12; i++) {
            PagamentoIptuEntity pagamento = new PagamentoIptuEntity();
            pagamento.setIdCidadao(idCidadao);
            pagamento.setPagamentoUnico(pagamentoUnico);
            
            double valorParcela = calcularValorParcela(idCidadao);
            if (pagamentoUnico) {
                valorParcela = (i == 1) ? 1000.00 : 0.00;
            }
            pagamento.setValor(valorParcela);
            pagamento.setPago(false);
            pagamento.setDataVencimento(hoje.plusMonths(i).withDayOfMonth(hoje.plusMonths(i).lengthOfMonth()));

            pagamentoIptuRepository.save(pagamento);
        }

        
        atualizarContadorUsuarios();

        return pagamentoIptuRepository.findByIdCidadao(idCidadao);
    }

    @Transactional
    public void pagarParcela(Long id) {
        PagamentoIptuEntity pagamento = pagamentoIptuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Parcela com ID " + id + " não encontrada"));

        if (pagamento.isPago()) {
            throw new IllegalStateException("Parcela com ID " + id + " já está paga.");
        }

        pagamento.setPago(true);
        pagamento.setDataPagamento(LocalDate.now());
        pagamentoIptuRepository.save(pagamento);
    }

    public double consultarTotalDevido(int idCidadao) {
        return pagamentoIptuRepository.findByIdCidadao(idCidadao).stream()
                .filter(p -> !p.isPago())
                .mapToDouble(PagamentoIptuEntity::getValor)
                .sum();
    }

    public long consultarTotalPagas(int idCidadao) {
        return pagamentoIptuRepository.findByIdCidadao(idCidadao).stream()
                .filter(PagamentoIptuEntity::isPago)
                .count();
    }

    public List<PagamentoIptuEntity> historicoPagamentos(int idCidadao) {
        return pagamentoIptuRepository.findByIdCidadao(idCidadao);
    }

    private double calcularValorParcela(int idCidadao) {
       
        return 1000.00;
    }

    
    private void atualizarContadorUsuarios() {
        
        long totalCidadaos = pagamentoIptuRepository.countDistinctByIdCidadao();
        meterRegistry.gauge("usuarios_cadastrados", totalCidadaos); 
    }
}
