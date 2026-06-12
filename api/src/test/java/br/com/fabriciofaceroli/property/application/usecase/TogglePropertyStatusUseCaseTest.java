package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.TogglePropertyStatusCommand;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.SavePropertyPort;
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
class TogglePropertyStatusUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();

    private static Property activeProperty() {
        return new Property(PROPERTY_ID, "Casa", "casa", null,
                new BigDecimal("300000"), null, null, null, null, null,
                null, null, "São Paulo", "SP", null,
                "SALE", false, "ACTIVE", null, UUID.randomUUID(), null);
    }

    @Test
    void toggle_shouldSetStatusToInactive_whenPropertyIsActive() {
        var find = mock(FindPropertyByIdPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new TogglePropertyStatusUseCase(find, save);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(activeProperty()));
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.toggle(new TogglePropertyStatusCommand(PROPERTY_ID, PropertyStatus.INACTIVE));

        verify(save).save(argThat(p -> p.status().equals("INACTIVE")));
    }

    @Test
    void toggle_shouldSetStatusToActive_whenPropertyIsInactive() {
        var find = mock(FindPropertyByIdPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new TogglePropertyStatusUseCase(find, save);

        var inactive = activeProperty();
        var inactiveProperty = new Property(
                inactive.id(), inactive.title(), inactive.slug(), inactive.description(),
                inactive.price(), inactive.area(), inactive.bedrooms(), inactive.suites(),
                inactive.bathrooms(), inactive.parkingSpots(), inactive.address(),
                inactive.neighborhood(), inactive.city(), inactive.state(), inactive.zipCode(),
                inactive.dealType(), inactive.featured(), "INACTIVE",
                inactive.externalUrl(), inactive.categoryId(), null);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(inactiveProperty));
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.toggle(new TogglePropertyStatusCommand(PROPERTY_ID, PropertyStatus.ACTIVE));

        verify(save).save(argThat(p -> p.status().equals("ACTIVE")));
    }

    @Test
    void toggle_shouldPreserveAllOtherFields_whenChangingStatus() {
        var find = mock(FindPropertyByIdPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new TogglePropertyStatusUseCase(find, save);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(activeProperty()));
        when(save.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.toggle(new TogglePropertyStatusCommand(PROPERTY_ID, PropertyStatus.INACTIVE));

        verify(save).save(argThat(p ->
                p.id().equals(PROPERTY_ID) &&
                p.title().equals("Casa") &&
                p.slug().equals("casa") &&
                p.status().equals("INACTIVE")));
    }

    @Test
    void toggle_shouldThrowResourceNotFoundException_whenPropertyNotFound() {
        var find = mock(FindPropertyByIdPort.class);
        var save = mock(SavePropertyPort.class);
        var sut = new TogglePropertyStatusUseCase(find, save);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.toggle(new TogglePropertyStatusCommand(PROPERTY_ID, PropertyStatus.INACTIVE)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");

        verify(save, never()).save(any());
    }
}
