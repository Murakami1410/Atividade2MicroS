package atv2.micro.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@FeignClient(name = "pagamento-iptu-client", url = "${feign.client.iptu.url:http://localhost:8081/pagamentos-iptu}")
public interface PagamentoFeignClient {

    @Operation(summary = "Gera parcelas de IPTU para o cidadão", description = "Gera as parcelas de IPTU para o cidadão com base no tipo de pagamento escolhido")
    @PostMapping("/gerar/{idCidadao}/{pagamentoUnico}")
    List<PagamentoIptuEntity> gerarParcelas(@PathVariable int idCidadao, @PathVariable boolean pagamentoUnico);

    @Operation(summary = "Efetua o pagamento de uma parcela de IPTU", description = "Marca uma parcela como paga com base no ID")
    @PutMapping("/pagar/{id}")
    String pagarParcela(@PathVariable Long id); 

    @Operation(summary = "Consulta o total devido de IPTU de um cidadão", description = "Retorna o valor total de IPTU que um cidadão deve pagar")
    @GetMapping("/total-devido/{idCidadao}")
    Double totalDevido(@PathVariable int idCidadao); 

    @Operation(summary = "Consulta o total de parcelas pagas de um cidadão", description = "Retorna o número total de parcelas pagas por um cidadão")
    @GetMapping("/total-pagas/{idCidadao}")
    Long totalPagas(@PathVariable int idCidadao); 

    @Operation(summary = "Consulta o histórico de pagamentos de IPTU de um cidadão", description = "Retorna o histórico de pagamentos realizados por um cidadão")
    @GetMapping("/historico/{idCidadao}")
    List<PagamentoIptuEntity> historicoPagamentos(@PathVariable int idCidadao);
}
