package br.com.fabriciofaceroli.zip.infrastructure.cnpja;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(CnpjaProperties.class)
public class CnpjaConfig {

    @Bean
    public RestClient cnpjaRestClient(CnpjaProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.apiUrl())
                .defaultHeader("Authorization", properties.apiToken())
                .build();
    }
}
