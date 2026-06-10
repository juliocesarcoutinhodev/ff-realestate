package br.com.fabriciofaceroli.category.application.usecase;

import br.com.fabriciofaceroli.category.application.port.in.UpdateCategoryCommand;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryByIdPort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryByNamePort;
import br.com.fabriciofaceroli.category.application.port.out.SaveCategoryPort;
import br.com.fabriciofaceroli.category.domain.model.Category;
import br.com.fabriciofaceroli.shared.exception.ConflictException;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

    @Test
    void update_shouldReturnUpdatedCategoryWithNewSlug_whenNameChanges() {
        var id = UUID.randomUUID();
        var existing = new Category(id, "Apartamento", "apartamento", "Descrição antiga.");
        var findById = mock(FindCategoryByIdPort.class);
        var findByName = mock(FindCategoryByNamePort.class);
        var save = mock(SaveCategoryPort.class);
        var sut = new UpdateCategoryUseCase(findById, findByName, save);
        var command = new UpdateCategoryCommand(id, "Apartamento Compacto", "Studios e kitinetes.");

        when(findById.findById(id)).thenReturn(Optional.of(existing));
        when(findByName.findByName("Apartamento Compacto")).thenReturn(Optional.empty());
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = sut.update(command);

        assertThat(result.name()).isEqualTo("Apartamento Compacto");
        assertThat(result.slug()).isEqualTo("apartamento-compacto");
        assertThat(result.description()).isEqualTo("Studios e kitinetes.");
    }

    @Test
    void update_shouldNotThrowConflict_whenNameRemainsTheSame() {
        var id = UUID.randomUUID();
        var existing = new Category(id, "Apartamento", "apartamento", null);
        var findById = mock(FindCategoryByIdPort.class);
        var findByName = mock(FindCategoryByNamePort.class);
        var save = mock(SaveCategoryPort.class);
        var sut = new UpdateCategoryUseCase(findById, findByName, save);
        var command = new UpdateCategoryCommand(id, "Apartamento", "Nova descrição.");

        when(findById.findById(id)).thenReturn(Optional.of(existing));
        // same category returned — same id, so no conflict
        when(findByName.findByName("Apartamento")).thenReturn(Optional.of(existing));
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = sut.update(command);

        assertThat(result.name()).isEqualTo("Apartamento");
        assertThat(result.description()).isEqualTo("Nova descrição.");
    }

    @Test
    void update_shouldRegenerateSlugWithDiacriticsRemoved_whenNameContainsAccents() {
        var id = UUID.randomUUID();
        var existing = new Category(id, "Area", "area", null);
        var findById = mock(FindCategoryByIdPort.class);
        var findByName = mock(FindCategoryByNamePort.class);
        var save = mock(SaveCategoryPort.class);
        var sut = new UpdateCategoryUseCase(findById, findByName, save);
        var command = new UpdateCategoryCommand(id, "Área Comercial", null);

        when(findById.findById(id)).thenReturn(Optional.of(existing));
        when(findByName.findByName("Área Comercial")).thenReturn(Optional.empty());
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = sut.update(command);

        assertThat(result.slug()).isEqualTo("area-comercial");
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenCategoryNotFound() {
        var id = UUID.randomUUID();
        var findById = mock(FindCategoryByIdPort.class);
        var sut = new UpdateCategoryUseCase(findById, mock(FindCategoryByNamePort.class), mock(SaveCategoryPort.class));

        when(findById.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.update(new UpdateCategoryCommand(id, "Novo Nome", null)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoria não encontrada.");
    }

    @Test
    void update_shouldThrowConflictException_whenNameBelongsToAnotherCategory() {
        var id = UUID.randomUUID();
        var otherId = UUID.randomUUID();
        var existing = new Category(id, "Casa", "casa", null);
        var otherCategory = new Category(otherId, "Apartamento", "apartamento", null);
        var findById = mock(FindCategoryByIdPort.class);
        var findByName = mock(FindCategoryByNamePort.class);
        var save = mock(SaveCategoryPort.class);
        var sut = new UpdateCategoryUseCase(findById, findByName, save);

        when(findById.findById(id)).thenReturn(Optional.of(existing));
        when(findByName.findByName("Apartamento")).thenReturn(Optional.of(otherCategory));

        assertThatThrownBy(() -> sut.update(new UpdateCategoryCommand(id, "Apartamento", null)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Já existe uma categoria com esse nome.");
    }

    @Test
    void update_shouldNotCallSavePort_whenCategoryNotFound() {
        var id = UUID.randomUUID();
        var findById = mock(FindCategoryByIdPort.class);
        var save = mock(SaveCategoryPort.class);
        var sut = new UpdateCategoryUseCase(findById, mock(FindCategoryByNamePort.class), save);

        when(findById.findById(id)).thenReturn(Optional.empty());

        try { sut.update(new UpdateCategoryCommand(id, "Nome", null)); } catch (ResourceNotFoundException ignored) {}

        verify(save, never()).save(any());
    }
}
