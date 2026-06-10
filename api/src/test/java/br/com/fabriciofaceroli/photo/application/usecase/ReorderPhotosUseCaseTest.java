package br.com.fabriciofaceroli.photo.application.usecase;

import br.com.fabriciofaceroli.photo.application.port.in.PhotoOrderItem;
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
class ReorderPhotosUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();
    private static final UUID PHOTO_A = UUID.randomUUID();
    private static final UUID PHOTO_B = UUID.randomUUID();
    private static final UUID PHOTO_C = UUID.randomUUID();

    private FindPropertyPort findPropertyPort;
    private FindPhotosByPropertyPort findPhotosByPropertyPort;
    private SavePhotosPort savePhotosPort;
    private ReorderPhotosUseCase sut;

    @BeforeEach
    void setUp() {
        findPropertyPort = mock(FindPropertyPort.class);
        findPhotosByPropertyPort = mock(FindPhotosByPropertyPort.class);
        savePhotosPort = mock(SavePhotosPort.class);
        sut = new ReorderPhotosUseCase(findPropertyPort, findPhotosByPropertyPort, savePhotosPort);
    }

    @Test
    @SuppressWarnings("unchecked")
    void reorder_shouldApplyNewOrderIndexToAllProvidedPhotos() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(threePhotos(0, 1, 2));
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        sut.reorder(PROPERTY_ID, List.of(
                new PhotoOrderItem(PHOTO_A, 2),
                new PhotoOrderItem(PHOTO_B, 0),
                new PhotoOrderItem(PHOTO_C, 1)
        ));

        ArgumentCaptor<List<PropertyPhoto>> captor = ArgumentCaptor.forClass(List.class);
        verify(savePhotosPort).saveAll(captor.capture());

        var saved = captor.getValue();
        assertThat(orderOf(saved, PHOTO_A)).isEqualTo(2);
        assertThat(orderOf(saved, PHOTO_B)).isEqualTo(0);
        assertThat(orderOf(saved, PHOTO_C)).isEqualTo(1);
    }

    @Test
    void reorder_shouldReturnUpdatedListFromSaveAll() {
        var expected = threePhotos(2, 0, 1);
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(threePhotos(0, 1, 2));
        when(savePhotosPort.saveAll(anyList())).thenReturn(expected);

        var result = sut.reorder(PROPERTY_ID, List.of(
                new PhotoOrderItem(PHOTO_A, 2),
                new PhotoOrderItem(PHOTO_B, 0),
                new PhotoOrderItem(PHOTO_C, 1)
        ));

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @SuppressWarnings("unchecked")
    void reorder_shouldPreserveOrderIndex_forPhotosNotInRequest() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(threePhotos(0, 1, 2));
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        sut.reorder(PROPERTY_ID, List.of(new PhotoOrderItem(PHOTO_A, 5)));

        ArgumentCaptor<List<PropertyPhoto>> captor = ArgumentCaptor.forClass(List.class);
        verify(savePhotosPort).saveAll(captor.capture());

        var saved = captor.getValue();
        assertThat(orderOf(saved, PHOTO_A)).isEqualTo(5);
        assertThat(orderOf(saved, PHOTO_B)).isEqualTo(1);
        assertThat(orderOf(saved, PHOTO_C)).isEqualTo(2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void reorder_shouldPreserveCoverFlag_afterReorder() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(List.of(
                new PropertyPhoto(PHOTO_A, PROPERTY_ID, "url-a", "a.jpg", true, 0),
                new PropertyPhoto(PHOTO_B, PROPERTY_ID, "url-b", "b.jpg", false, 1)
        ));
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        sut.reorder(PROPERTY_ID, List.of(
                new PhotoOrderItem(PHOTO_A, 1),
                new PhotoOrderItem(PHOTO_B, 0)
        ));

        ArgumentCaptor<List<PropertyPhoto>> captor = ArgumentCaptor.forClass(List.class);
        verify(savePhotosPort).saveAll(captor.capture());

        var saved = captor.getValue();
        assertThat(saved.stream().filter(p -> PHOTO_A.equals(p.id())).findFirst()).isPresent()
                .get().extracting(PropertyPhoto::cover).isEqualTo(true);
        assertThat(saved.stream().filter(p -> PHOTO_B.equals(p.id())).findFirst()).isPresent()
                .get().extracting(PropertyPhoto::cover).isEqualTo(false);
    }

    @Test
    void reorder_shouldThrowNotFound_whenPropertyDoesNotExist() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(false);

        assertThatThrownBy(() -> sut.reorder(PROPERTY_ID, List.of(new PhotoOrderItem(PHOTO_A, 0))))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");

        verify(savePhotosPort, never()).saveAll(anyList());
    }

    @Test
    void reorder_shouldThrowNotFound_whenPhotoIdDoesNotBelongToProperty() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(threePhotos(0, 1, 2));

        var unknownId = UUID.randomUUID();

        assertThatThrownBy(() -> sut.reorder(PROPERTY_ID, List.of(new PhotoOrderItem(unknownId, 0))))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Foto não encontrada.");

        verify(savePhotosPort, never()).saveAll(anyList());
    }

    private List<PropertyPhoto> threePhotos(int orderA, int orderB, int orderC) {
        return List.of(
                new PropertyPhoto(PHOTO_A, PROPERTY_ID, "url-a", "a.jpg", true, orderA),
                new PropertyPhoto(PHOTO_B, PROPERTY_ID, "url-b", "b.jpg", false, orderB),
                new PropertyPhoto(PHOTO_C, PROPERTY_ID, "url-c", "c.jpg", false, orderC)
        );
    }

    private int orderOf(List<PropertyPhoto> photos, UUID id) {
        return photos.stream()
                .filter(p -> id.equals(p.id()))
                .findFirst()
                .map(PropertyPhoto::orderIndex)
                .orElseThrow();
    }
}
