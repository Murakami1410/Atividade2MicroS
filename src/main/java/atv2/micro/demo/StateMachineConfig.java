package atv2.micro.demo;



import org.springframework.context.annotation.Configuration;

import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;


@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<String, String> {

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
        states
            .withStates()
            .initial("SOLICITADO")
            .state("AGUARDANDO_ANALISE")
            .state("CONCLUIDO");
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
        transitions
            .withExternal()
            .source("SOLICITADO").target("AGUARDANDO_ANALISE").event("ANALISAR")
            .and()
            .withExternal()
            .source("AGUARDANDO_ANALISE").target("CONCLUIDO").event("CONCLUIR");
    }
}

