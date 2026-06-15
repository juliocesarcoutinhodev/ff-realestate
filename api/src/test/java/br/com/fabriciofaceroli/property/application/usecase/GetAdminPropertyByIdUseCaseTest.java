package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.domain.model.CategoryInfo;
import br.com.fabriciofaceroli.property.domain.model.PhotoSummary;
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

    private static final List<PhotoSummary> PHOTOS = List.of(
            new PhotoSummary(UUID.randomUUID(), "https://cdn/foto1.jpg", 0, true),
            new PhotoSummary(UUID.randomUUID(), "https://cdn/foto2.jpg", 1, false)
    );

    @Test
    void getById_shouldReturnPropertyDetailWithPhotos_whenIdExists() {
        var sut = new GetAdminPropertyByIdUseCase(
                id -> Optional.of(PROPERTY_DETAIL),
                id -> PHOTOS
        );

        var result = sut.getById(PROPERTY_ID);

        assertThat(result.id()).isEqualTo(PROPERTY_ID);
        assertThat(result.title()).isEqualTo("Casa Nova no Jardim Mirian");
        assertThat(result.category().name()).isEqualTo("Casa");
        assertThat(result.photos()).hasSize(2);
        assertThat(result.photos().getFirst().cover()).isTrue();
    }

    @Test
    void getById_shouldReturnInactiveProperty_whenPropertyIsInactive() {
        var sut = new GetAdminPropertyByIdUseCase(
                id -> Optional.of(PROPERTY_DETAIL),
                id -> List.of()
        );

        var result = sut.getById(PROPERTY_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
    }

    @Test
    void getById_shouldReturnEmptyPhotos_whenPropertyHasNoPhotos() {
        var sut = new GetAdminPropertyByIdUseCase(
                id -> Optional.of(PROPERTY_DETAIL),
                id -> List.of()
        );

        var result = sut.getById(PROPERTY_ID);

        assertThat(result.photos()).isEmpty();
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenIdNotFound() {
        var sut = new GetAdminPropertyByIdUseCase(
                id -> Optional.empty(),
                id -> List.of()
        );

        assertThatThrownBy(() -> sut.getById(PROPERTY_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");
    }
}
