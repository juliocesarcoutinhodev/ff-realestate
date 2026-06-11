package br.com.fabriciofaceroli.zip.application.port.out;

import br.com.fabriciofaceroli.zip.domain.model.ZipCodeResponse;

public interface ZipCodeProviderPort {

    ZipCodeResponse findByCode(String code);
}
