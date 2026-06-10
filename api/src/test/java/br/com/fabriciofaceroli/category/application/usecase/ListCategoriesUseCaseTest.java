package br.com.fabriciofaceroli.category.application.usecase;

import br.com.fabriciofaceroli.category.domain.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ListCategoriesUseCaseTest {

    @Test
    void listAll_shouldReturnAllCategories_whenCategoriesExist() {
        var categories = List.of(
                new Category(UUID.randomUUID(), "Apartamento", "apartamento", "Imóveis em condomínio vertical."),
                new Category(UUID.randomUUID(), "Casa", "casa", "Imóveis residenciais horizontais.")
        );
        var sut = new ListCategoriesUseCase(() -> categories);

        var result = sut.listAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Apartamento");
        assertThat(result.get(1).name()).isEqualTo("Casa");
    }

    @Test
    void listAll_shouldReturnEmptyList_whenNoCategoriesExist() {
        var sut = new ListCategoriesUseCase(List::of);

        var result = sut.listAll();

        assertThat(result).isEmpty();
    }
}
