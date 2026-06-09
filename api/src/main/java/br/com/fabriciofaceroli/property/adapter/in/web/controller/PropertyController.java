package br.com.fabriciofaceroli.property.adapter.in.web.controller;

import br.com.fabriciofaceroli.property.adapter.in.web.dto.CreatePropertyRequest;
import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertyDetailResponse;
import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertySummaryResponse;
import br.com.fabriciofaceroli.property.adapter.in.web.dto.TogglePropertyStatusRequest;
import br.com.fabriciofaceroli.property.adapter.in.web.dto.UpdatePropertyRequest;
import br.com.fabriciofaceroli.property.adapter.in.web.mapper.PropertyWebMapper;
import br.com.fabriciofaceroli.property.application.port.in.CreatePropertyPort;
import br.com.fabriciofaceroli.property.application.port.in.DeletePropertyPort;
import br.com.fabriciofaceroli.property.application.port.in.GetPropertyBySlugPort;
import br.com.fabriciofaceroli.property.application.port.in.ListPropertiesPort;
import br.com.fabriciofaceroli.property.application.port.in.ListPropertiesQuery;
import br.com.fabriciofaceroli.property.application.port.in.TogglePropertyStatusCommand;
import br.com.fabriciofaceroli.property.application.port.in.TogglePropertyStatusPort;
import br.com.fabriciofaceroli.property.application.port.in.UpdatePropertyPort;
import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyController implements PropertyApiDocs {

    private final ListPropertiesPort listPropertiesPort;
    private final GetPropertyBySlugPort getPropertyBySlugPort;
    private final CreatePropertyPort createPropertyPort;
    private final UpdatePropertyPort updatePropertyPort;
    private final DeletePropertyPort deletePropertyPort;
    private final TogglePropertyStatusPort togglePropertyStatusPort;
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

    @Override
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<PropertyDetailResponse>> getBySlug(@PathVariable String slug) {
        var property = getPropertyBySlugPort.getBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Imóvel encontrado.", propertyWebMapper.toDetailResponse(property)));
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<PropertySummaryResponse>> create(@Valid @RequestBody CreatePropertyRequest request) {
        var property = createPropertyPort.create(propertyWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Imóvel cadastrado com sucesso.", propertyWebMapper.toSummaryResponse(property)));
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<PropertySummaryResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePropertyRequest request) {
        var property = updatePropertyPort.update(propertyWebMapper.toUpdateCommand(id, request));
        return ResponseEntity.ok(ApiResponse.success("Imóvel atualizado com sucesso.", propertyWebMapper.toSummaryResponse(property)));
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deletePropertyPort.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<PropertySummaryResponse>> toggleStatus(
            @PathVariable UUID id,
            @Valid @RequestBody TogglePropertyStatusRequest request) {
        var property = togglePropertyStatusPort.toggle(new TogglePropertyStatusCommand(id, request.status()));
        return ResponseEntity.ok(ApiResponse.success("Status atualizado com sucesso.", propertyWebMapper.toSummaryResponse(property)));
    }
}
