package br.com.fabriciofaceroli.zip.application.usecase;

import br.com.fabriciofaceroli.zip.application.port.in.LookupZipCodePort;
import br.com.fabriciofaceroli.zip.application.port.out.ZipCodeProviderPort;
import br.com.fabriciofaceroli.zip.domain.model.ZipCodeResponse;
import org.springframework.stereotype.Service;

@Service
public class LookupZipCodeUseCase implements LookupZipCodePort {

    private final ZipCodeProviderPort zipCodeProviderPort;

    public LookupZipCodeUseCase(ZipCodeProviderPort zipCodeProviderPort) {
        this.zipCodeProviderPort = zipCodeProviderPort;
    }

    @Override
    public ZipCodeResponse lookup(String code) {
        return zipCodeProviderPort.findByCode(code);
    }
}
