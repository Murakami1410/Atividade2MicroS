package atv2.micro.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;

@RestController
public class HistoricoController {

    @Autowired
    private HistoricoTransacaoRepository historicoRepository;

    
    @Operation(
        summary = "Consultar histórico de transações",
        description = "Retorna o histórico completo de transações de um cidadão, informando as etapas das transações realizadas.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Histórico encontrado",
                content = @Content(mediaType = "application/json", 
                                   schema = @Schema(implementation = HistoricoTransacaoEntity.class))
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Cidadão não encontrado"
            )
        }
    )
    @GetMapping("/historico")
    public List<HistoricoTransacaoEntity> consultarHistorico(
        @Parameter(description = "ID do cidadão", required = true)
        @RequestParam Long cidadaoId) {

        // Retorna a lista de transações do cidadão, consultando o repositório
        return historicoRepository.findByCidadaoId(cidadaoId);
    }
}
