package br.com.fabriciofaceroli.zip.infrastructure.cnpja;

import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.shared.exception.ServiceUnavailableException;
import br.com.fabriciofaceroli.zip.application.port.out.ZipCodeProviderPort;
import br.com.fabriciofaceroli.zip.domain.model.ZipCodeResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class CnpjaZipAdapter implements ZipCodeProviderPort {

    private final RestClient restClient;

    public CnpjaZipAdapter(@Qualifier("cnpjaRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @Cacheable(value = "zip-codes", key = "#code")
    public ZipCodeResponse findByCode(String code) {
        try {
            var raw = restClient.get()
                    .uri("/zip/{code}", code)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError(),
                            (req, res) -> { throw new ResourceNotFoundException("CEP não encontrado."); }
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            (req, res) -> { throw new ServiceUnavailableException("Serviço de CEP indisponível. Preencha o endereço manualmente."); }
                    )
                    .body(CnpjaZipRaw.class);

            if (raw == null) {
                throw new ServiceUnavailableException("Serviço de CEP indisponível. Preencha o endereço manualmente.");
            }

            return new ZipCodeResponse(raw.code(), raw.street(), raw.district(), raw.city(), raw.state());

        } catch (ResourceNotFoundException | ServiceUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Serviço de CEP indisponível. Preencha o endereço manualmente.");
        }
    }

    private record CnpjaZipRaw(
            String code,
            String street,
            String district,
            String city,
            String state
    ) {}
}
