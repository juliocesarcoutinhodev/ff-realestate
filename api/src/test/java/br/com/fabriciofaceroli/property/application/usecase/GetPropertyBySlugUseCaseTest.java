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
class GetPropertyBySlugUseCaseTest {

    private static final PropertyDetail PROPERTY_DETAIL = new PropertyDetail(
            UUID.randomUUID(), "Casa Nova no Jardim Mirian", "casa-nova-no-jardim-mirian",
            "Descrição completa.", new BigDecimal("450000.00"), new BigDecimal("125.00"),
            3, 1, 2, 2, "Rua das Flores, 123", "Jardim Mirian", "São Paulo", "SP", "04000-000",
            "SALE", false, "ACTIVE", null,
            new CategoryInfo(UUID.randomUUID(), "Casa", "casa"),
            List.of()
    );

    @Test
    void getBySlug_shouldReturnPropertyDetail_whenSlugIsActiveAndExists() {
        var sut = new GetPropertyBySlugUseCase(slug -> Optional.of(PROPERTY_DETAIL));

        var result = sut.getBySlug("casa-nova-no-jardim-mirian");

        assertThat(result.slug()).isEqualTo("casa-nova-no-jardim-mirian");
        assertThat(result.title()).isEqualTo("Casa Nova no Jardim Mirian");
        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(result.category().name()).isEqualTo("Casa");
        assertThat(result.photos()).isEmpty();
    }

    @Test
    void getBySlug_shouldThrowResourceNotFoundException_whenSlugNotFound() {
        var sut = new GetPropertyBySlugUseCase(slug -> Optional.empty());

        assertThatThrownBy(() -> sut.getBySlug("inexistente"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");
    }

    @Test
    void getBySlug_shouldThrowResourceNotFoundException_whenPropertyIsInactive() {
        // Imóvel inativo: o adapter filtra status=ACTIVE na query, portanto retorna vazio
        var sut = new GetPropertyBySlugUseCase(slug -> Optional.empty());

        assertThatThrownBy(() -> sut.getBySlug("imovel-inativo"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");
    }
}
