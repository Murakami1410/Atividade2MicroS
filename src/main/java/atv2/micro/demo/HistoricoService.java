package atv2.micro.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HistoricoService {

    private static final Logger log = LoggerFactory.getLogger(HistoricoService.class);

    @Autowired
    private HistoricoTransacaoRepository historicoRepository;

    @Autowired
    private StateMachineFactory<String, String> stateMachineFactory;

    @SuppressWarnings("deprecation")
    public void processarTransacao(Long cidadaoId, String descricao) {
        StateMachine<String, String> stateMachine = stateMachineFactory.getStateMachine();
        resetStateMachine(stateMachine);

        try {
            registrarHistorico(cidadaoId, "SOLICITADO", descricao);

            if (enviarEvento(stateMachine, "ANALISAR")) {
                if ("AGUARDANDO_ANALISE".equals(stateMachine.getState().getId())) {
                    registrarHistorico(cidadaoId, "AGUARDANDO_ANALISE", descricao);

                    if (enviarEvento(stateMachine, "CONCLUIR")) {
                        if ("CONCLUIDO".equals(stateMachine.getState().getId())) {
                            registrarHistorico(cidadaoId, "CONCLUIDO", descricao);
                        }
                    }
                }
            }
        } finally {
            try {
                stateMachine.stop();
            } catch (Exception e) {
                log.warn("Erro ao parar a State Machine: {}", e.getMessage(), e);
            }
        }
    }

    private void registrarHistorico(Long cidadaoId, String estado, String descricao) {
        HistoricoTransacaoEntity historico = new HistoricoTransacaoEntity();
        historico.setCidadaoId(cidadaoId);
        historico.setEstado(estado);
        historico.setDescricao(descricao);
        historico.setDataRegistro(LocalDateTime.now());

        log.info("Registrando histórico: Cidadão ID {}, Estado {}, Descrição '{}'", cidadaoId, estado, descricao);
        historicoRepository.save(historico);
    }

    private boolean enviarEvento(StateMachine<String, String> sm, String evento) {
        log.debug("Enviando evento '{}' para a State Machine no estado '{}'", evento, sm.getState().getId());
        try {
            @SuppressWarnings("deprecation")
            boolean accepted = sm.sendEvent(MessageBuilder.withPayload(evento).build());
            if (accepted) {
                log.info("Evento '{}' ACEITO. Novo estado: {}", evento, sm.getState().getId());
            } else {
                log.warn("Evento '{}' NÃO ACEITO. Estado atual: {}", evento, sm.getState().getId());
            }
            return accepted;
        } catch (Exception e) {
            log.error("Erro ao enviar evento '{}': {}", evento, e.getMessage(), e);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private void resetStateMachine(StateMachine<String, String> sm) {
        log.debug("Resetando State Machine...");
        try {
            sm.stop();
            sm.getStateMachineAccessor().doWithAllRegions(access -> {
                access.resetStateMachine(new DefaultStateMachineContext<>("SOLICITADO", null, null, null));
            });
            sm.start();
            log.debug("State Machine resetada para o estado inicial.");
        } catch (Exception e) {
            log.error("Erro ao resetar State Machine: {}", e.getMessage(), e);
            try {
                sm.start();
            } catch (Exception ex) {
                log.error("Erro ao tentar iniciar a State Machine após falha no reset: {}", ex.getMessage(), ex);
            }
        }
    }
}
