package br.com.fabriciofaceroli.category.application.usecase;

import br.com.fabriciofaceroli.category.application.port.in.CreateCategoryCommand;
import br.com.fabriciofaceroli.category.domain.model.Category;
import br.com.fabriciofaceroli.shared.exception.ConflictException;
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
class CreateCategoryUseCaseTest {

    @Test
    void create_shouldReturnCategoryWithGeneratedSlug_whenNameIsUnique() {
        var findPort = mock(br.com.fabriciofaceroli.category.application.port.out.FindCategoryByNamePort.class);
        var savePort = mock(br.com.fabriciofaceroli.category.application.port.out.SaveCategoryPort.class);
        var sut = new CreateCategoryUseCase(findPort, savePort);
        var command = new CreateCategoryCommand("Apartamento", "Imóveis em condomínio vertical.");
        var saved = new Category(UUID.randomUUID(), "Apartamento", "apartamento", "Imóveis em condomínio vertical.");

        when(findPort.findByName("Apartamento")).thenReturn(Optional.empty());
        when(savePort.save(any())).thenReturn(saved);

        var result = sut.create(command);

        assertThat(result.name()).isEqualTo("Apartamento");
        assertThat(result.slug()).isEqualTo("apartamento");
    }

    @Test
    void create_shouldGenerateSlugWithDiacriticsRemoved_whenNameContainsAccents() {
        var findPort = mock(br.com.fabriciofaceroli.category.application.port.out.FindCategoryByNamePort.class);
        var savePort = mock(br.com.fabriciofaceroli.category.application.port.out.SaveCategoryPort.class);
        var sut = new CreateCategoryUseCase(findPort, savePort);
        var command = new CreateCategoryCommand("Área Comercial", null);

        when(findPort.findByName("Área Comercial")).thenReturn(Optional.empty());
        when(savePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = sut.create(command);

        assertThat(result.slug()).isEqualTo("area-comercial");
    }

    @Test
    void create_shouldPersistCategoryWithNullId_whenCreating() {
        var findPort = mock(br.com.fabriciofaceroli.category.application.port.out.FindCategoryByNamePort.class);
        var savePort = mock(br.com.fabriciofaceroli.category.application.port.out.SaveCategoryPort.class);
        var sut = new CreateCategoryUseCase(findPort, savePort);
        var command = new CreateCategoryCommand("Casa", "Imóvel residencial.");

        when(findPort.findByName("Casa")).thenReturn(Optional.empty());
        when(savePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = sut.create(command);

        assertThat(result.id()).isNull();
        assertThat(result.name()).isEqualTo("Casa");
        assertThat(result.description()).isEqualTo("Imóvel residencial.");
    }

    @Test
    void create_shouldThrowConflictException_whenNameAlreadyExists() {
        var findPort = mock(br.com.fabriciofaceroli.category.application.port.out.FindCategoryByNamePort.class);
        var savePort = mock(br.com.fabriciofaceroli.category.application.port.out.SaveCategoryPort.class);
        var sut = new CreateCategoryUseCase(findPort, savePort);
        var command = new CreateCategoryCommand("Apartamento", null);
        var existing = new Category(UUID.randomUUID(), "Apartamento", "apartamento", null);

        when(findPort.findByName("Apartamento")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> sut.create(command))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Já existe uma categoria com esse nome.");
    }

    @Test
    void create_shouldNotCallSavePort_whenNameAlreadyExists() {
        var findPort = mock(br.com.fabriciofaceroli.category.application.port.out.FindCategoryByNamePort.class);
        var savePort = mock(br.com.fabriciofaceroli.category.application.port.out.SaveCategoryPort.class);
        var sut = new CreateCategoryUseCase(findPort, savePort);
        var command = new CreateCategoryCommand("Apartamento", null);
        var existing = new Category(UUID.randomUUID(), "Apartamento", "apartamento", null);

        when(findPort.findByName("Apartamento")).thenReturn(Optional.of(existing));

        try { sut.create(command); } catch (ConflictException ignored) {}

        verify(savePort, never()).save(any());
    }
}
