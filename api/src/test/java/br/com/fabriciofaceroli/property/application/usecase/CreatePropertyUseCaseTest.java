package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.category.application.port.out.ValidateCategoryExistsPort;
import br.com.fabriciofaceroli.property.application.port.in.CreatePropertyCommand;
import br.com.fabriciofaceroli.property.application.port.out.CheckPropertySlugPort;
import br.com.fabriciofaceroli.property.application.port.out.SavePropertyPort;
import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePropertyUseCaseTest {

    private static final UUID CATEGORY_ID = UUID.randomUUID();

    private static CreatePropertyCommand command(String title) {
        return new CreatePropertyCommand(title, "Desc", new BigDecimal("450000"),
                new BigDecimal("125"), 3, 1, 2, 2, "Rua A, 1", "Centro",
                "São Paulo", "SP", "04000-000", DealType.SALE, false,
                "https://example.com", CATEGORY_ID);
    }

    private static Property savedProperty(String slug) {
        return new Property(UUID.randomUUID(), "Casa", slug, "Desc",
                new BigDecimal("450000"), new BigDecimal("125"), 3, 1, 2, 2,
                "Rua A, 1", "Centro", "São Paulo", "SP", "04000-000",
                "SALE", false, "ACTIVE", null, CATEGORY_ID, null);
    }

    @Test
    void create_shouldSaveProperty_whenCategoryExistsAndSlugIsUnique() {
        var validate = mock(ValidateCategoryExistsPort.class);
        var slugCheck = mock(CheckPropertySlugPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new CreatePropertyUseCase(validate, slugCheck, save);

        when(validate.exists(CATEGORY_ID)).thenReturn(true);
        when(slugCheck.existsBySlug(any())).thenReturn(false);
        when(save.save(any())).thenReturn(savedProperty("casa-nova-no-jardim-mirian"));

        var result = sut.create(command("Casa Nova no Jardim Mirian"));

        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(save).save(argThat(p -> p.slug().equals("casa-nova-no-jardim-mirian")));
    }

    @Test
    void create_shouldThrowResourceNotFoundException_whenCategoryNotFound() {
        var validate = mock(ValidateCategoryExistsPort.class);
        var sut = new CreatePropertyUseCase(validate, mock(CheckPropertySlugPort.class), mock(SavePropertyPort.class));

        when(validate.exists(CATEGORY_ID)).thenReturn(false);

        assertThatThrownBy(() -> sut.create(command("Título")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoria não encontrada.");
    }

    @Test
    void create_shouldNotSave_whenCategoryNotFound() {
        var validate = mock(ValidateCategoryExistsPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new CreatePropertyUseCase(validate, mock(CheckPropertySlugPort.class), save);

        when(validate.exists(CATEGORY_ID)).thenReturn(false);

        try { sut.create(command("Título")); } catch (ResourceNotFoundException ignored) {}

        verify(save, never()).save(any());
    }

    @Test
    void create_shouldGenerateUniqueSlug_whenBaseSlugAlreadyExists() {
        var validate = mock(ValidateCategoryExistsPort.class);
        var slugCheck = mock(CheckPropertySlugPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new CreatePropertyUseCase(validate, slugCheck, save);

        when(validate.exists(CATEGORY_ID)).thenReturn(true);
        when(slugCheck.existsBySlug("casa")).thenReturn(true);
        when(slugCheck.existsBySlug("casa-2")).thenReturn(false);
        when(save.save(any())).thenReturn(savedProperty("casa-2"));

        sut.create(command("Casa"));

        verify(save).save(argThat(p -> p.slug().equals("casa-2")));
    }

    @Test
    void create_shouldSetStatusAsActive_always() {
        var validate = mock(ValidateCategoryExistsPort.class);
        var slugCheck = mock(CheckPropertySlugPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new CreatePropertyUseCase(validate, slugCheck, save);

        when(validate.exists(CATEGORY_ID)).thenReturn(true);
        when(slugCheck.existsBySlug(any())).thenReturn(false);
        when(save.save(any())).thenReturn(savedProperty("titulo"));

        sut.create(command("Título"));

        verify(save).save(argThat(p -> "ACTIVE".equals(p.status())));
    }

    @Test
    void create_shouldNormalizeTitleToSlug_removingAccentsAndSpecialChars() {
        var validate = mock(ValidateCategoryExistsPort.class);
        var slugCheck = mock(CheckPropertySlugPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new CreatePropertyUseCase(validate, slugCheck, save);

        when(validate.exists(CATEGORY_ID)).thenReturn(true);
        when(slugCheck.existsBySlug(any())).thenReturn(false);
        when(save.save(any())).thenReturn(savedProperty("apartamento-cobertura-em-sao-paulo"));

        sut.create(command("Apartamento Cobertura em São Paulo!"));

        verify(save).save(argThat(p -> p.slug().equals("apartamento-cobertura-em-sao-paulo")));
    }
}
