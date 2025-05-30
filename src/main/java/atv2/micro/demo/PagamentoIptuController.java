package atv2.micro.demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/pagamentos-iptu")
@Tag(name = "IPTU", description = "API para Gerenciamento de Pagamentos de IPTU")
public class PagamentoIptuController {

    private static final Logger log = LoggerFactory.getLogger(PagamentoIptuController.class);

    @Autowired
    private PagamentoIptuApplication pagamentoIptuApplication;

    
    @Operation(summary = "Gerar parcelas IPTU", description = "Gera as parcelas para o pagamento do IPTU do cidadão.")
    @PostMapping("/gerar/{idCidadao}/{pagamentoUnico}")
    public ResponseEntity<List<PagamentoIptuEntity>> gerarParcelas(
            @Parameter(description = "ID do cidadão") @PathVariable int idCidadao,
            @Parameter(description = "Indica se o pagamento será único ou parcelado") @PathVariable boolean pagamentoUnico) {
        log.info("Requisição para gerar parcelas IPTU: Cidadão ID {}, Pagamento Único: {}", idCidadao, pagamentoUnico);
        try {
            List<PagamentoIptuEntity> parcelas = pagamentoIptuApplication.gerarParcelas(idCidadao, pagamentoUnico);
            return ResponseEntity.status(HttpStatus.CREATED).body(parcelas);
        } catch (Exception e) {
            log.error("Erro ao gerar parcelas para cidadão ID {}: {}", idCidadao, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @Operation(summary = "Pagar parcela IPTU", description = "Registra o pagamento de uma parcela do IPTU.")
    @PutMapping("/pagar/{id}")
    public ResponseEntity<String> pagarParcela(@Parameter(description = "ID da parcela") @PathVariable Long id) {
        log.info("Requisição para pagar parcela IPTU: ID {}", id);
        try {
            pagamentoIptuApplication.pagarParcela(id);
            return ResponseEntity.ok("Parcela com ID " + id + " paga com sucesso!");
        } catch (IllegalArgumentException e) {
            log.warn("Tentativa de pagar parcela não encontrada: ID {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("Tentativa de pagar parcela já paga: ID {}", id);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("Erro interno ao pagar parcela ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao processar pagamento da parcela " + id);
        }
    }

    
    @Operation(summary = "Consultar total devido", description = "Consulta o total devido de IPTU para um cidadão.")
    @GetMapping("/total-devido/{idCidadao}")
    public ResponseEntity<Double> totalDevido(@Parameter(description = "ID do cidadão") @PathVariable int idCidadao) {
        log.debug("Consultando total devido IPTU para cidadão ID {}", idCidadao);
        try {
            Double totalDevido = pagamentoIptuApplication.consultarTotalDevido(idCidadao);
            return ResponseEntity.ok(totalDevido);
        } catch (Exception e) {
            log.error("Erro ao consultar total devido para cidadão ID {}: {}", idCidadao, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

 
    @Operation(summary = "Consultar total de parcelas pagas", description = "Consulta o total de parcelas já pagas pelo cidadão.")
    @GetMapping("/total-pagas/{idCidadao}")
    public ResponseEntity<Long> totalPagas(@Parameter(description = "ID do cidadão") @PathVariable int idCidadao) {
        log.debug("Consultando total de parcelas pagas IPTU para cidadão ID {}", idCidadao);
        try {
            Long totalPagas = pagamentoIptuApplication.consultarTotalPagas(idCidadao);
            return ResponseEntity.ok(totalPagas);
        } catch (Exception e) {
            log.error("Erro ao consultar total de parcelas pagas para cidadão ID {}: {}", idCidadao, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

   
    @Operation(summary = "Consultar histórico de pagamentos", description = "Consulta o histórico de pagamentos de IPTU para um cidadão.")
    @GetMapping("/historico/{idCidadao}")
    public ResponseEntity<List<PagamentoIptuEntity>> historicoPagamentos(@Parameter(description = "ID do cidadão") @PathVariable int idCidadao) {
        log.debug("Consultando histórico de parcelas IPTU para cidadão ID {}", idCidadao);
        try {
            List<PagamentoIptuEntity> historico = pagamentoIptuApplication.historicoPagamentos(idCidadao);
            return ResponseEntity.ok(historico);
        } catch (Exception e) {
            log.error("Erro ao consultar histórico de parcelas para cidadão ID {}: {}", idCidadao, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
