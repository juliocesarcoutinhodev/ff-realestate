package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.ListAllPropertiesQuery;
import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAllPropertiesUseCaseTest {

    private static Property property(PropertyStatus status) {
        return new Property(UUID.randomUUID(), "Título", "titulo", "Desc",
                new BigDecimal("300000"), new BigDecimal("80"), 2, 0, 1, 1,
                "Rua A, 1", "Centro", "Campinas", "SP", "13000-000",
                status.name(), false, status.name(), null, UUID.randomUUID());
    }

    @Test
    void listAll_shouldReturnAllPropertiesRegardlessOfStatus_whenNoStatusFilter() {
        var port = mock(br.com.fabriciofaceroli.property.application.port.out.FindAllPropertiesPort.class);
        var sut = new ListAllPropertiesUseCase(port);
        var query = new ListAllPropertiesQuery(null, null, null, null, null, PageRequest.of(0, 12));
        var expected = new PageImpl<>(List.of(property(PropertyStatus.ACTIVE), property(PropertyStatus.INACTIVE)));

        when(port.findAll(query)).thenReturn(expected);

        var result = sut.listAll(query);

        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(port).findAll(query);
    }

    @Test
    void listAll_shouldDelegateStatusFilter_whenStatusProvided() {
        var port = mock(br.com.fabriciofaceroli.property.application.port.out.FindAllPropertiesPort.class);
        var sut = new ListAllPropertiesUseCase(port);
        var query = new ListAllPropertiesQuery(null, null, null, null, PropertyStatus.INACTIVE, PageRequest.of(0, 12));
        var expected = new PageImpl<>(List.of(property(PropertyStatus.INACTIVE)));

        when(port.findAll(query)).thenReturn(expected);

        var result = sut.listAll(query);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).status()).isEqualTo("INACTIVE");
        verify(port).findAll(query);
    }

    @Test
    void listAll_shouldReturnEmptyPage_whenNoPropertiesExist() {
        var port = mock(br.com.fabriciofaceroli.property.application.port.out.FindAllPropertiesPort.class);
        var sut = new ListAllPropertiesUseCase(port);
        var query = new ListAllPropertiesQuery(null, null, null, null, null, PageRequest.of(0, 12));

        when(port.findAll(any())).thenReturn(Page.empty());

        var result = sut.listAll(query);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void listAll_shouldDelegateAllFilters_whenProvided() {
        var port = mock(br.com.fabriciofaceroli.property.application.port.out.FindAllPropertiesPort.class);
        var sut = new ListAllPropertiesUseCase(port);
        var categoryId = UUID.randomUUID();
        var query = new ListAllPropertiesQuery(categoryId, DealType.SALE, true, "São Paulo", PropertyStatus.ACTIVE, PageRequest.of(0, 12));

        when(port.findAll(query)).thenReturn(Page.empty());

        sut.listAll(query);

        verify(port).findAll(query);
    }
}
