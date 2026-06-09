package br.com.fabriciofaceroli.property.adapter.in.web.controller;

import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertySummaryResponse;
import br.com.fabriciofaceroli.property.adapter.in.web.mapper.PropertyWebMapper;
import br.com.fabriciofaceroli.property.application.port.in.ListPropertiesPort;
import br.com.fabriciofaceroli.property.application.port.in.ListPropertiesQuery;
import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.shared.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyController implements PropertyApiDocs {

    private final ListPropertiesPort listPropertiesPort;
    private final PropertyWebMapper propertyWebMapper;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PropertySummaryResponse>>> listActive(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) DealType dealType,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        var query = new ListPropertiesQuery(categoryId, dealType, featured, city, PageRequest.of(page, size));
        var result = listPropertiesPort.list(query);
        return ResponseEntity.ok(ApiResponse.success("Imóveis listados com sucesso.", propertyWebMapper.toPageResponse(result)));
    }
}
