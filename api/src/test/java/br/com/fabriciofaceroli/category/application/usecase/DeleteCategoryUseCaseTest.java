package br.com.fabriciofaceroli.category.application.usecase;

import br.com.fabriciofaceroli.category.application.port.out.CountPropertiesByCategoryPort;
import br.com.fabriciofaceroli.category.application.port.out.DeleteCategoryByIdPort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryByIdPort;
import br.com.fabriciofaceroli.category.domain.model.Category;
import br.com.fabriciofaceroli.shared.exception.ConflictException;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryUseCaseTest {

    @Test
    void delete_shouldDeleteCategory_whenNoPropertiesAreLinked() {
        var id = UUID.randomUUID();
        var findById = mock(FindCategoryByIdPort.class);
        var countProperties = mock(CountPropertiesByCategoryPort.class);
        var deleteById = mock(DeleteCategoryByIdPort.class);
        var sut = new DeleteCategoryUseCase(findById, countProperties, deleteById);

        when(findById.findById(id)).thenReturn(Optional.of(new Category(id, "Casa", "casa", null)));
        when(countProperties.countByCategory(id)).thenReturn(0L);

        assertThatNoException().isThrownBy(() -> sut.delete(id));
        verify(deleteById).deleteById(id);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenCategoryNotFound() {
        var id = UUID.randomUUID();
        var findById = mock(FindCategoryByIdPort.class);
        var sut = new DeleteCategoryUseCase(findById, mock(CountPropertiesByCategoryPort.class), mock(DeleteCategoryByIdPort.class));

        when(findById.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.delete(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoria não encontrada.");
    }

    @Test
    void delete_shouldThrowConflictException_whenCategoryHasLinkedProperties() {
        var id = UUID.randomUUID();
        var findById = mock(FindCategoryByIdPort.class);
        var countProperties = mock(CountPropertiesByCategoryPort.class);
        var sut = new DeleteCategoryUseCase(findById, countProperties, mock(DeleteCategoryByIdPort.class));

        when(findById.findById(id)).thenReturn(Optional.of(new Category(id, "Apartamento", "apartamento", null)));
        when(countProperties.countByCategory(id)).thenReturn(3L);

        assertThatThrownBy(() -> sut.delete(id))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Não é possível excluir uma categoria com imóveis vinculados.");
    }

    @Test
    void delete_shouldNotCallDeletePort_whenCategoryNotFound() {
        var id = UUID.randomUUID();
        var findById = mock(FindCategoryByIdPort.class);
        var deleteById = mock(DeleteCategoryByIdPort.class);
        var sut = new DeleteCategoryUseCase(findById, mock(CountPropertiesByCategoryPort.class), deleteById);

        when(findById.findById(id)).thenReturn(Optional.empty());

        try { sut.delete(id); } catch (ResourceNotFoundException ignored) {}

        verify(deleteById, never()).deleteById(id);
    }

    @Test
    void delete_shouldNotCallDeletePort_whenCategoryHasLinkedProperties() {
        var id = UUID.randomUUID();
        var findById = mock(FindCategoryByIdPort.class);
        var countProperties = mock(CountPropertiesByCategoryPort.class);
        var deleteById = mock(DeleteCategoryByIdPort.class);
        var sut = new DeleteCategoryUseCase(findById, countProperties, deleteById);

        when(findById.findById(id)).thenReturn(Optional.of(new Category(id, "Apartamento", "apartamento", null)));
        when(countProperties.countByCategory(id)).thenReturn(1L);

        try { sut.delete(id); } catch (ConflictException ignored) {}

        verify(deleteById, never()).deleteById(id);
    }
}
