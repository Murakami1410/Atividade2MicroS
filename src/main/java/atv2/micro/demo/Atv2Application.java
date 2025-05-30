package atv2.micro.demo; // Certifique-se de que o pacote esteja correto

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // Importar para habilitar o Feign

@SpringBootApplication
@EnableFeignClients // Habilita o uso de Feign Clients na aplicação
public class Atv2Application {

    public static void main(String[] args) {
        SpringApplication.run(Atv2Application.class, args); // Altere para o nome correto da classe
    }

}
