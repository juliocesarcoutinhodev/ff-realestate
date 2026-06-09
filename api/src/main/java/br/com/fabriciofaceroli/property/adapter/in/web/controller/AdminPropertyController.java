package br.com.fabriciofaceroli.property.adapter.in.web.controller;

import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertySummaryResponse;
import br.com.fabriciofaceroli.property.adapter.in.web.mapper.PropertyWebMapper;
import br.com.fabriciofaceroli.property.application.port.in.ListAllPropertiesPort;
import br.com.fabriciofaceroli.property.application.port.in.ListAllPropertiesQuery;
import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.shared.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/properties")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminPropertyController implements AdminPropertyApiDocs {

    private final ListAllPropertiesPort listAllPropertiesPort;
    private final PropertyWebMapper propertyWebMapper;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PropertySummaryResponse>>> listAll(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) DealType dealType,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        var query = new ListAllPropertiesQuery(categoryId, dealType, featured, city, status, PageRequest.of(page, size));
        var result = listAllPropertiesPort.listAll(query);
        return ResponseEntity.ok(ApiResponse.success("Imóveis listados com sucesso.", propertyWebMapper.toPageResponse(result)));
    }
}
