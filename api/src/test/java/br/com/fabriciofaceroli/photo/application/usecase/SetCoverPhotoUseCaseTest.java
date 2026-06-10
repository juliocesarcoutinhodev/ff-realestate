package br.com.fabriciofaceroli.photo.application.usecase;

import br.com.fabriciofaceroli.photo.application.port.out.FindPhotosByPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPropertyPort;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetCoverPhotoUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();
    private static final UUID COVER_PHOTO_ID = UUID.randomUUID();
    private static final UUID OTHER_PHOTO_ID = UUID.randomUUID();

    private FindPropertyPort findPropertyPort;
    private FindPhotosByPropertyPort findPhotosByPropertyPort;
    private SavePhotosPort savePhotosPort;
    private SetCoverPhotoUseCase sut;

    @BeforeEach
    void setUp() {
        findPropertyPort = mock(FindPropertyPort.class);
        findPhotosByPropertyPort = mock(FindPhotosByPropertyPort.class);
        savePhotosPort = mock(SavePhotosPort.class);
        sut = new SetCoverPhotoUseCase(findPropertyPort, findPhotosByPropertyPort, savePhotosPort);
    }

    @Test
    @SuppressWarnings("unchecked")
    void setCover_shouldMarkTargetAsCoverAndClearOthers_whenPropertyAndPhotoExist() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(twoPhotos(OTHER_PHOTO_ID, true, COVER_PHOTO_ID, false));
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        sut.setCover(PROPERTY_ID, COVER_PHOTO_ID);

        ArgumentCaptor<List<PropertyPhoto>> captor = ArgumentCaptor.forClass(List.class);
        verify(savePhotosPort).saveAll(captor.capture());

        var saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved.stream().filter(p -> COVER_PHOTO_ID.equals(p.id())).findFirst()).isPresent()
                .get().extracting(PropertyPhoto::cover).isEqualTo(true);
        assertThat(saved.stream().filter(p -> OTHER_PHOTO_ID.equals(p.id())).findFirst()).isPresent()
                .get().extracting(PropertyPhoto::cover).isEqualTo(false);
    }

    @Test
    void setCover_shouldReturnUpdatedList_orderedFromSaveAll() {
        var expectedResult = twoPhotos(COVER_PHOTO_ID, true, OTHER_PHOTO_ID, false);
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(twoPhotos(OTHER_PHOTO_ID, false, COVER_PHOTO_ID, false));
        when(savePhotosPort.saveAll(anyList())).thenReturn(expectedResult);

        var result = sut.setCover(PROPERTY_ID, COVER_PHOTO_ID);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void setCover_shouldWork_whenTargetPhotoWasAlreadyCover() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(twoPhotos(COVER_PHOTO_ID, true, OTHER_PHOTO_ID, false));
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        sut.setCover(PROPERTY_ID, COVER_PHOTO_ID);

        verify(savePhotosPort).saveAll(anyList());
    }

    @Test
    void setCover_shouldThrowNotFound_whenPropertyDoesNotExist() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(false);

        assertThatThrownBy(() -> sut.setCover(PROPERTY_ID, COVER_PHOTO_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");

        verify(savePhotosPort, never()).saveAll(anyList());
    }

    @Test
    void setCover_shouldThrowNotFound_whenPhotoDoesNotBelongToProperty() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(twoPhotos(OTHER_PHOTO_ID, true, UUID.randomUUID(), false));

        assertThatThrownBy(() -> sut.setCover(PROPERTY_ID, COVER_PHOTO_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Foto não encontrada.");

        verify(savePhotosPort, never()).saveAll(anyList());
    }

    @Test
    void setCover_shouldThrowNotFound_whenPropertyHasNoPhotos() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> sut.setCover(PROPERTY_ID, COVER_PHOTO_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Foto não encontrada.");

        verify(savePhotosPort, never()).saveAll(anyList());
    }

    private static List<PropertyPhoto> twoPhotos(UUID firstId, boolean firstCover, UUID secondId, boolean secondCover) {
        return List.of(
                new PropertyPhoto(firstId, PROPERTY_ID, "https://cdn.example.com/a.jpg", "a.jpg", firstCover, 0),
                new PropertyPhoto(secondId, PROPERTY_ID, "https://cdn.example.com/b.jpg", "b.jpg", secondCover, 1)
        );
    }
}
