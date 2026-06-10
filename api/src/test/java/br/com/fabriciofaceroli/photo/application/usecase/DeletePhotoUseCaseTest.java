package br.com.fabriciofaceroli.photo.application.usecase;

import br.com.fabriciofaceroli.photo.application.port.out.DeletePhotoFilePort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPhotosByPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.RemovePhotoPort;
import br.com.fabriciofaceroli.photo.application.port.out.SavePhotosPort;
import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletePhotoUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();
    private static final UUID COVER_ID = UUID.randomUUID();
    private static final UUID SECOND_ID = UUID.randomUUID();
    private static final UUID THIRD_ID = UUID.randomUUID();

    private FindPhotosByPropertyPort findPhotosByPropertyPort;
    private DeletePhotoFilePort deletePhotoFilePort;
    private RemovePhotoPort removePhotoPort;
    private SavePhotosPort savePhotosPort;
    private DeletePhotoUseCase sut;

    @BeforeEach
    void setUp() {
        findPhotosByPropertyPort = mock(FindPhotosByPropertyPort.class);
        deletePhotoFilePort = mock(DeletePhotoFilePort.class);
        removePhotoPort = mock(RemovePhotoPort.class);
        savePhotosPort = mock(SavePhotosPort.class);
        sut = new DeletePhotoUseCase(findPhotosByPropertyPort, deletePhotoFilePort, removePhotoPort, savePhotosPort);
    }

    @Test
    void delete_shouldRemoveFromMinioAndDatabase() {
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(threePhotos());
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        sut.delete(PROPERTY_ID, SECOND_ID);

        verify(deletePhotoFilePort).delete("https://cdn.example.com/b.jpg");
        verify(removePhotoPort).deleteById(SECOND_ID);
    }

    @Test
    @SuppressWarnings("unchecked")
    void delete_shouldReorderRemainingPhotosSequentially() {
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(threePhotos());
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        sut.delete(PROPERTY_ID, SECOND_ID);

        ArgumentCaptor<List<PropertyPhoto>> captor = ArgumentCaptor.forClass(List.class);
        verify(savePhotosPort).saveAll(captor.capture());

        var saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).orderIndex()).isEqualTo(0);
        assertThat(saved.get(1).orderIndex()).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void delete_shouldPromoteNextPhotoAsCover_whenCoverIsDeleted() {
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(threePhotos());
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        sut.delete(PROPERTY_ID, COVER_ID);

        ArgumentCaptor<List<PropertyPhoto>> captor = ArgumentCaptor.forClass(List.class);
        verify(savePhotosPort).saveAll(captor.capture());

        var saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).cover()).isTrue();
        assertThat(saved.get(1).cover()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    void delete_shouldPreserveExistingCover_whenNonCoverIsDeleted() {
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(threePhotos());
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        sut.delete(PROPERTY_ID, SECOND_ID);

        ArgumentCaptor<List<PropertyPhoto>> captor = ArgumentCaptor.forClass(List.class);
        verify(savePhotosPort).saveAll(captor.capture());

        var saved = captor.getValue();
        assertThat(saved.stream().filter(p -> COVER_ID.equals(p.id())).findFirst())
                .isPresent().get().extracting(PropertyPhoto::cover).isEqualTo(true);
    }

    @Test
    void delete_shouldNotCallSaveAll_whenLastPhotoIsDeleted() {
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(
                List.of(new PropertyPhoto(COVER_ID, PROPERTY_ID, "https://cdn.example.com/a.jpg", "a.jpg", true, 0))
        );

        sut.delete(PROPERTY_ID, COVER_ID);

        verify(deletePhotoFilePort).delete(anyString());
        verify(removePhotoPort).deleteById(COVER_ID);
        verify(savePhotosPort, never()).saveAll(anyList());
    }

    @Test
    void delete_shouldThrowNotFound_whenPhotoDoesNotBelongToProperty() {
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(threePhotos());

        var unknownId = UUID.randomUUID();

        assertThatThrownBy(() -> sut.delete(PROPERTY_ID, unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Foto não encontrada.");

        verify(deletePhotoFilePort, never()).delete(anyString());
        verify(removePhotoPort, never()).deleteById(unknownId);
    }

    @Test
    void delete_shouldThrowNotFound_whenPropertyHasNoPhotos() {
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> sut.delete(PROPERTY_ID, COVER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Foto não encontrada.");
    }

    private List<PropertyPhoto> threePhotos() {
        return List.of(
                new PropertyPhoto(COVER_ID, PROPERTY_ID, "https://cdn.example.com/a.jpg", "a.jpg", true, 0),
                new PropertyPhoto(SECOND_ID, PROPERTY_ID, "https://cdn.example.com/b.jpg", "b.jpg", false, 1),
                new PropertyPhoto(THIRD_ID, PROPERTY_ID, "https://cdn.example.com/c.jpg", "c.jpg", false, 2)
        );
    }
}
