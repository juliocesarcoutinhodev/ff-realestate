package br.com.fabriciofaceroli.photo.application.usecase;

import br.com.fabriciofaceroli.photo.application.port.out.FindPhotosByPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPropertyPort;
import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPhotosUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();

    private FindPropertyPort findPropertyPort;
    private FindPhotosByPropertyPort findPhotosByPropertyPort;
    private ListPhotosUseCase sut;

    @BeforeEach
    void setUp() {
        findPropertyPort = mock(FindPropertyPort.class);
        findPhotosByPropertyPort = mock(FindPhotosByPropertyPort.class);
        sut = new ListPhotosUseCase(findPropertyPort, findPhotosByPropertyPort);
    }

    @Test
    void list_shouldReturnPhotos_whenPropertyExists() {
        var photos = List.of(
                new PropertyPhoto(UUID.randomUUID(), PROPERTY_ID, "https://cdn/a.jpg", "a.jpg", true, 0),
                new PropertyPhoto(UUID.randomUUID(), PROPERTY_ID, "https://cdn/b.jpg", "b.jpg", false, 1)
        );
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(photos);

        var result = sut.list(PROPERTY_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).cover()).isTrue();
        verify(findPhotosByPropertyPort).findByPropertyId(PROPERTY_ID);
    }

    @Test
    void list_shouldReturnEmptyList_whenPropertyHasNoPhotos() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(List.of());

        var result = sut.list(PROPERTY_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void list_shouldThrowNotFound_whenPropertyDoesNotExist() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(false);

        assertThatThrownBy(() -> sut.list(PROPERTY_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");

        verify(findPhotosByPropertyPort, never()).findByPropertyId(PROPERTY_ID);
    }
}
