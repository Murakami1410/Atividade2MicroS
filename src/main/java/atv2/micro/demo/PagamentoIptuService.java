package atv2.micro.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagamentoIptuService {

    @Autowired
    private PagamentoIptuRepository pagamentoIptuRepository;

    
    public void gerarParcelas(int idCidadao, boolean pagamentoUnico) {
        
        List<PagamentoIptuEntity> parcelasExistentes = pagamentoIptuRepository.findByIdCidadao(idCidadao);
        if (!parcelasExistentes.isEmpty()) {
            throw new IllegalArgumentException("Parcelas já foram geradas para este cidadão.");
        }

        
        for (int i = 0; i < 12; i++) {
            PagamentoIptuEntity parcela = new PagamentoIptuEntity();
            parcela.setIdCidadao(idCidadao);
            parcela.setPagamentoUnico(pagamentoUnico);

            
            if (pagamentoUnico) {
                parcela.setValor(i == 0 ? 1000.00 : 0.00);  
            } else {
                parcela.setValor(1000.00);  
            }

            parcela.setPago(false); 
            parcela.setDataVencimento(parcela.getDataVencimento().plusMonths(i)); 
            pagamentoIptuRepository.save(parcela); 
        }
    }

    
    public void pagarParcela(Long id) {
        Optional<PagamentoIptuEntity> parcelaOptional = pagamentoIptuRepository.findById(id);
        if (!parcelaOptional.isPresent()) {
            throw new IllegalArgumentException("Parcela não encontrada.");
        }

        PagamentoIptuEntity parcela = parcelaOptional.get();
        parcela.setPago(true);  
        pagamentoIptuRepository.save(parcela); 
    }

    
    public double calcularTotalDevido(int idCidadao) {
        List<PagamentoIptuEntity> parcelas = pagamentoIptuRepository.findByIdCidadao(idCidadao);
        double totalDevido = 0.0;
        for (PagamentoIptuEntity parcela : parcelas) {
            if (!parcela.isPago()) {
                totalDevido += parcela.getValor();  
            }
        }
        return totalDevido;
    }

    
    public int calcularTotalParcelasPagas(int idCidadao) {
        List<PagamentoIptuEntity> parcelas = pagamentoIptuRepository.findByIdCidadao(idCidadao);
        int totalPagas = 0;
        for (PagamentoIptuEntity parcela : parcelas) {
            if (parcela.isPago()) {
                totalPagas++; 
            }
        }
        return totalPagas;
    }
}

