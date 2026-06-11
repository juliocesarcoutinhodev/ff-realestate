package br.com.fabriciofaceroli.zip.application.port.in;

import br.com.fabriciofaceroli.zip.domain.model.ZipCodeResponse;

public interface LookupZipCodePort {

    ZipCodeResponse lookup(String code);
}
