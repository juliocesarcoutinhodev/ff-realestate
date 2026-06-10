package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.out.DeletePropertyByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.DeletePropertyPhotosPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyByIdPort;
import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletePropertyUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();

    private static Property anyProperty() {
        return new Property(PROPERTY_ID, "Casa", "casa", null,
                new BigDecimal("300000"), null, null, null, null, null,
                null, null, "São Paulo", "SP", null,
                "SALE", false, "ACTIVE", null, UUID.randomUUID());
    }

    @Test
    void delete_shouldDeletePhotosAndProperty_whenPropertyExists() {
        var find = mock(FindPropertyByIdPort.class);
        var deletePhotos = mock(DeletePropertyPhotosPort.class);
        var deleteById = mock(DeletePropertyByIdPort.class);
        var sut = new DeletePropertyUseCase(find, deletePhotos, deleteById);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(anyProperty()));

        sut.delete(PROPERTY_ID);

        verify(deletePhotos).deleteByPropertyId(PROPERTY_ID);
        verify(deleteById).deleteById(PROPERTY_ID);
    }

    @Test
    void delete_shouldDeletePhotosBeforeProperty() {
        var find = mock(FindPropertyByIdPort.class);
        var deletePhotos = mock(DeletePropertyPhotosPort.class);
        var deleteById = mock(DeletePropertyByIdPort.class);
        var sut = new DeletePropertyUseCase(find, deletePhotos, deleteById);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.of(anyProperty()));

        sut.delete(PROPERTY_ID);

        InOrder order = inOrder(deletePhotos, deleteById);
        order.verify(deletePhotos).deleteByPropertyId(PROPERTY_ID);
        order.verify(deleteById).deleteById(PROPERTY_ID);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenPropertyNotFound() {
        var find = mock(FindPropertyByIdPort.class);
        var sut = new DeletePropertyUseCase(find, mock(DeletePropertyPhotosPort.class), mock(DeletePropertyByIdPort.class));

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.delete(PROPERTY_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");
    }

    @Test
    void delete_shouldNotDeleteAnything_whenPropertyNotFound() {
        var find = mock(FindPropertyByIdPort.class);
        var deletePhotos = mock(DeletePropertyPhotosPort.class);
        var deleteById = mock(DeletePropertyByIdPort.class);
        var sut = new DeletePropertyUseCase(find, deletePhotos, deleteById);

        when(find.findById(PROPERTY_ID)).thenReturn(Optional.empty());

        try { sut.delete(PROPERTY_ID); } catch (ResourceNotFoundException ignored) {}

        verify(deletePhotos, never()).deleteByPropertyId(PROPERTY_ID);
        verify(deleteById, never()).deleteById(PROPERTY_ID);
    }
}
