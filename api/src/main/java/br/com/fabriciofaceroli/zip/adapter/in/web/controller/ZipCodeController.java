package br.com.fabriciofaceroli.zip.adapter.in.web.controller;

import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.zip.application.port.in.LookupZipCodePort;
import br.com.fabriciofaceroli.zip.domain.model.ZipCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/zip")
@RequiredArgsConstructor
public class ZipCodeController implements ZipCodeApiDocs {

    private final LookupZipCodePort lookupZipCodePort;

    @Override
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<ZipCodeResponse>> lookup(@PathVariable String code) {
        var result = lookupZipCodePort.lookup(code);
        return ResponseEntity.ok(ApiResponse.success("CEP encontrado.", result));
    }
}
