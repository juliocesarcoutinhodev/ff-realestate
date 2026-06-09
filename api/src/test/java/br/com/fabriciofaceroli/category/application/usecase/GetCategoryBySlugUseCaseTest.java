package br.com.fabriciofaceroli.category.application.usecase;

import br.com.fabriciofaceroli.category.domain.model.Category;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class GetCategoryBySlugUseCaseTest {

    @Test
    void getBySlug_shouldReturnCategory_whenSlugExists() {
        var expected = new Category(UUID.randomUUID(), "Apartamento", "apartamento", "Imóveis em condomínio vertical.");
        var sut = new GetCategoryBySlugUseCase(slug -> Optional.of(expected));

        var result = sut.getBySlug("apartamento");

        assertThat(result.slug()).isEqualTo("apartamento");
        assertThat(result.name()).isEqualTo("Apartamento");
    }

    @Test
    void getBySlug_shouldThrowResourceNotFoundException_whenSlugNotFound() {
        var sut = new GetCategoryBySlugUseCase(slug -> Optional.empty());

        assertThatThrownBy(() -> sut.getBySlug("inexistente"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoria não encontrada.");
    }
}
