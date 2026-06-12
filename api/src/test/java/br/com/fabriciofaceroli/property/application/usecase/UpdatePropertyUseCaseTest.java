package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.category.application.port.out.ValidateCategoryExistsPort;
import br.com.fabriciofaceroli.property.application.port.in.UpdatePropertyCommand;
import br.com.fabriciofaceroli.property.application.port.out.CheckPropertySlugPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.SavePropertyPort;
import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePropertyUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();
    private static final UUID CATEGORY_ID = UUID.randomUUID();

    private static Property existingProperty(String slug) {
        return new Property(PROPERTY_ID, "Casa Antiga", slug, "Desc",
                new BigDecimal("300000"), new BigDecimal("100"), 2, 0, 1, 1,
                "Rua A, 1", "Centro", "São Paulo", "SP", "04000-000",
                "SALE", false, "ACTIVE", null, CATEGORY_ID, null);
    }

    private static UpdatePropertyCommand command(String title, PropertyStatus status) {
        return new UpdatePropertyCommand(PROPERTY_ID, title, "Nova desc",
                new BigDecimal("500000"), new BigDecimal("130"), 3, 1, 2, 2,
                "Rua B, 10", "Jardim", "Campinas", "SP", "13000-000",
                DealType.SALE, true, null, CATEGORY_ID, status);
    }

    @Test
    void update_shouldSaveUpdatedProperty_whenPropertyAndCategoryExist() {
        var find = mock(FindPropertyByIdPort.class);
        var validate = mock(ValidateCategoryExistsPort.class);
        var slugCheck = mock(CheckPropertySlugPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new UpdatePropertyUseCase(find, validate, slugCheck, save);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty("casa-antiga")));
        when(validate.exists(CATEGORY_ID)).thenReturn(true);
        when(slugCheck.existsBySlug(any())).thenReturn(false);
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.update(command("Casa Nova", PropertyStatus.ACTIVE));

        verify(save).save(argThat(p -> p.id().equals(PROPERTY_ID) && p.title().equals("Casa Nova")));
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenPropertyNotFound() {
        var find = mock(FindPropertyByIdPort.class);
        var sut = new UpdatePropertyUseCase(find, mock(ValidateCategoryExistsPort.class),
                mock(CheckPropertySlugPort.class), mock(SavePropertyPort.class));

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.update(command("Título", null)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenCategoryNotFound() {
        var find = mock(FindPropertyByIdPort.class);
        var validate = mock(ValidateCategoryExistsPort.class);
        var sut = new UpdatePropertyUseCase(find, validate,
                mock(CheckPropertySlugPort.class), mock(SavePropertyPort.class));

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty("slug")));
        when(validate.exists(CATEGORY_ID)).thenReturn(false);

        assertThatThrownBy(() -> sut.update(command("Título", null)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoria não encontrada.");
    }

    @Test
    void update_shouldKeepSlug_whenTitleDidNotChange() {
        var find = mock(FindPropertyByIdPort.class);
        var validate = mock(ValidateCategoryExistsPort.class);
        var slugCheck = mock(CheckPropertySlugPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new UpdatePropertyUseCase(find, validate, slugCheck, save);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty("casa-antiga")));
        when(validate.exists(CATEGORY_ID)).thenReturn(true);
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.update(command("Casa Antiga", PropertyStatus.ACTIVE));

        verify(save).save(argThat(p -> p.slug().equals("casa-antiga")));
        verify(slugCheck, never()).existsBySlug(any());
    }

    @Test
    void update_shouldRegenerateSlug_whenTitleChanged() {
        var find = mock(FindPropertyByIdPort.class);
        var validate = mock(ValidateCategoryExistsPort.class);
        var slugCheck = mock(CheckPropertySlugPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new UpdatePropertyUseCase(find, validate, slugCheck, save);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty("casa-antiga")));
        when(validate.exists(CATEGORY_ID)).thenReturn(true);
        when(slugCheck.existsBySlug("casa-nova")).thenReturn(false);
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.update(command("Casa Nova", PropertyStatus.ACTIVE));

        verify(save).save(argThat(p -> p.slug().equals("casa-nova")));
    }

    @Test
    void update_shouldAppendSuffix_whenNewSlugAlreadyExists() {
        var find = mock(FindPropertyByIdPort.class);
        var validate = mock(ValidateCategoryExistsPort.class);
        var slugCheck = mock(CheckPropertySlugPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new UpdatePropertyUseCase(find, validate, slugCheck, save);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty("casa-antiga")));
        when(validate.exists(CATEGORY_ID)).thenReturn(true);
        when(slugCheck.existsBySlug("casa-nova")).thenReturn(true);
        when(slugCheck.existsBySlug("casa-nova-2")).thenReturn(false);
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.update(command("Casa Nova", PropertyStatus.ACTIVE));

        verify(save).save(argThat(p -> p.slug().equals("casa-nova-2")));
    }

    @Test
    void update_shouldKeepExistingStatus_whenStatusIsNull() {
        var find = mock(FindPropertyByIdPort.class);
        var validate = mock(ValidateCategoryExistsPort.class);
        var slugCheck = mock(CheckPropertySlugPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new UpdatePropertyUseCase(find, validate, slugCheck, save);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty("casa-antiga")));
        when(validate.exists(CATEGORY_ID)).thenReturn(true);
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.update(command("Casa Antiga", null));

        verify(save).save(argThat(p -> p.status().equals("ACTIVE")));
    }

    @Test
    void update_shouldChangeStatus_whenStatusProvided() {
        var find = mock(FindPropertyByIdPort.class);
        var validate = mock(ValidateCategoryExistsPort.class);
        var slugCheck = mock(CheckPropertySlugPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new UpdatePropertyUseCase(find, validate, slugCheck, save);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(existingProperty("casa-antiga")));
        when(validate.exists(CATEGORY_ID)).thenReturn(true);
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.update(command("Casa Antiga", PropertyStatus.INACTIVE));

        verify(save).save(argThat(p -> p.status().equals("INACTIVE")));
    }
}
