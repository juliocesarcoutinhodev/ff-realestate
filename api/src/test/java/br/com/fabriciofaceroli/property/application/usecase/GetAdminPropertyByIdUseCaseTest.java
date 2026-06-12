package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.domain.model.CategoryInfo;
import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class GetAdminPropertyByIdUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();

    private static final PropertyDetail PROPERTY_DETAIL = new PropertyDetail(
            PROPERTY_ID, "Casa Nova no Jardim Mirian", "casa-nova-no-jardim-mirian",
            "Descrição completa.", new BigDecimal("450000.00"), new BigDecimal("125.00"),
            3, 1, 2, 2, "Rua das Flores, 123", "Jardim Mirian", "São Paulo", "SP", "04000-000",
            "SALE", false, "INACTIVE", null,
            new CategoryInfo(UUID.randomUUID(), "Casa", "casa"),
            List.of()
    );

    @Test
    void getById_shouldReturnPropertyDetail_whenIdExists() {
        var sut = new GetAdminPropertyByIdUseCase(id -> Optional.of(PROPERTY_DETAIL));

        var result = sut.getById(PROPERTY_ID);

        assertThat(result.id()).isEqualTo(PROPERTY_ID);
        assertThat(result.title()).isEqualTo("Casa Nova no Jardim Mirian");
        assertThat(result.category().name()).isEqualTo("Casa");
        assertThat(result.photos()).isEmpty();
    }

    @Test
    void getById_shouldReturnInactiveProperty_whenPropertyIsInactive() {
        var sut = new GetAdminPropertyByIdUseCase(id -> Optional.of(PROPERTY_DETAIL));

        var result = sut.getById(PROPERTY_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenIdNotFound() {
        var sut = new GetAdminPropertyByIdUseCase(id -> Optional.empty());

        assertThatThrownBy(() -> sut.getById(PROPERTY_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");
    }
}
