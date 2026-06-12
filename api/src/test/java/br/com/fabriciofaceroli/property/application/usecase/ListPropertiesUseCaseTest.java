package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.ListPropertiesQuery;
import br.com.fabriciofaceroli.property.application.port.out.FindActivePropertiesPort;
import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.property.domain.model.Property;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPropertiesUseCaseTest {

    private final FindActivePropertiesPort findActivePropertiesPort = mock(FindActivePropertiesPort.class);
    private final ListPropertiesUseCase sut = new ListPropertiesUseCase(findActivePropertiesPort);

    @Test
    void list_shouldReturnPageOfProperties_whenActivePropertiesExist() {
        var pageable = PageRequest.of(0, 12);
        var query = new ListPropertiesQuery(null, null, null, null, pageable);
        var properties = List.of(aProperty(), aProperty());
        var page = new PageImpl<>(properties, pageable, 2);
        when(findActivePropertiesPort.findActive(query)).thenReturn(page);

        var result = sut.list(query);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void list_shouldReturnEmptyPage_whenNoActivePropertiesExist() {
        var pageable = PageRequest.of(0, 12);
        var query = new ListPropertiesQuery(null, null, null, null, pageable);
        when(findActivePropertiesPort.findActive(query)).thenReturn(Page.empty(pageable));

        var result = sut.list(query);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void list_shouldDelegateQueryWithFiltersToPort() {
        var categoryId = UUID.randomUUID();
        var pageable = PageRequest.of(1, 6);
        var query = new ListPropertiesQuery(categoryId, DealType.SALE, true, "Campinas", pageable);
        when(findActivePropertiesPort.findActive(query)).thenReturn(Page.empty(pageable));

        sut.list(query);

        verify(findActivePropertiesPort).findActive(query);
    }

    @Test
    void list_shouldReturnCorrectPaginationMetadata() {
        var pageable = PageRequest.of(2, 6);
        var query = new ListPropertiesQuery(null, null, null, null, pageable);
        var page = new PageImpl<>(List.of(aProperty()), pageable, 13);
        when(findActivePropertiesPort.findActive(query)).thenReturn(page);

        var result = sut.list(query);

        assertThat(result.getTotalElements()).isEqualTo(13);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getNumber()).isEqualTo(2);
    }

    private Property aProperty() {
        return new Property(
                UUID.randomUUID(), "Casa Teste", "casa-teste", null,
                BigDecimal.valueOf(450000), BigDecimal.valueOf(125),
                3, 1, 2, 2,
                null, "Jardim Mirian", "São Paulo", "SP", null,
                "SALE", false, "ACTIVE", null, UUID.randomUUID(), null
        );
    }
}
