package br.com.fabriciofaceroli.photo.application.usecase;

import br.com.fabriciofaceroli.photo.application.port.in.PhotoFile;
import br.com.fabriciofaceroli.photo.application.port.in.UploadPhotosCommand;
import br.com.fabriciofaceroli.photo.application.port.out.FindPhotosByPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.SavePhotosPort;
import br.com.fabriciofaceroli.photo.application.port.out.UploadPhotoFilePort;
import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.shared.exception.UnsupportedMediaTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadPhotosUseCaseTest {

    private static final UUID PROPERTY_ID = UUID.randomUUID();

    private FindPropertyPort findPropertyPort;
    private FindPhotosByPropertyPort findPhotosByPropertyPort;
    private UploadPhotoFilePort uploadPhotoFilePort;
    private SavePhotosPort savePhotosPort;
    private UploadPhotosUseCase sut;

    @BeforeEach
    void setUp() {
        findPropertyPort = mock(FindPropertyPort.class);
        findPhotosByPropertyPort = mock(FindPhotosByPropertyPort.class);
        uploadPhotoFilePort = mock(UploadPhotoFilePort.class);
        savePhotosPort = mock(SavePhotosPort.class);
        sut = new UploadPhotosUseCase(findPropertyPort, findPhotosByPropertyPort, uploadPhotoFilePort, savePhotosPort);
    }

    @Test
    void upload_shouldSavePhotos_whenPropertyExistsAndFilesAreValid() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.countByPropertyId(PROPERTY_ID)).thenReturn(0);
        when(findPhotosByPropertyPort.existsCoverByPropertyId(PROPERTY_ID)).thenReturn(false);
        when(uploadPhotoFilePort.upload(anyString(), anyString(), any())).thenReturn("https://cdn.example.com/photo.jpg");
        var saved = List.of(new PropertyPhoto(UUID.randomUUID(), PROPERTY_ID, "https://cdn.example.com/photo.jpg", "foto.jpg", true, 0));
        when(savePhotosPort.saveAll(anyList())).thenReturn(saved);
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(saved);

        var command = new UploadPhotosCommand(PROPERTY_ID, List.of(photoFile("foto.jpg", "image/jpeg")));
        var result = sut.upload(command);

        assertThat(result).hasSize(1);
        verify(savePhotosPort).saveAll(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    void upload_firstPhotoShouldBeCover_whenPropertyHasNoPhotos() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.countByPropertyId(PROPERTY_ID)).thenReturn(0);
        when(findPhotosByPropertyPort.existsCoverByPropertyId(PROPERTY_ID)).thenReturn(false);
        when(uploadPhotoFilePort.upload(anyString(), anyString(), any())).thenReturn("https://cdn.example.com/photo.jpg");
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        var command = new UploadPhotosCommand(PROPERTY_ID, List.of(photoFile("a.jpg", "image/jpeg"), photoFile("b.jpg", "image/jpeg")));
        sut.upload(command);

        ArgumentCaptor<List<PropertyPhoto>> captor = ArgumentCaptor.forClass(List.class);
        verify(savePhotosPort).saveAll(captor.capture());
        assertThat(captor.getValue().get(0).cover()).isTrue();
        assertThat(captor.getValue().get(1).cover()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    void upload_shouldNotSetCover_whenPropertyAlreadyHasCover() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.countByPropertyId(PROPERTY_ID)).thenReturn(2);
        when(findPhotosByPropertyPort.existsCoverByPropertyId(PROPERTY_ID)).thenReturn(true);
        when(uploadPhotoFilePort.upload(anyString(), anyString(), any())).thenReturn("https://cdn.example.com/photo.jpg");
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        var command = new UploadPhotosCommand(PROPERTY_ID, List.of(photoFile("c.jpg", "image/jpeg")));
        sut.upload(command);

        ArgumentCaptor<List<PropertyPhoto>> captor = ArgumentCaptor.forClass(List.class);
        verify(savePhotosPort).saveAll(captor.capture());
        assertThat(captor.getValue().get(0).cover()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    void upload_shouldAssignOrderIndexSequentially_fromExistingCount() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.countByPropertyId(PROPERTY_ID)).thenReturn(3);
        when(findPhotosByPropertyPort.existsCoverByPropertyId(PROPERTY_ID)).thenReturn(true);
        when(uploadPhotoFilePort.upload(anyString(), anyString(), any())).thenReturn("https://cdn.example.com/photo.jpg");
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        var command = new UploadPhotosCommand(PROPERTY_ID, List.of(photoFile("d.jpg", "image/jpeg"), photoFile("e.jpg", "image/jpeg")));
        sut.upload(command);

        ArgumentCaptor<List<PropertyPhoto>> captor = ArgumentCaptor.forClass(List.class);
        verify(savePhotosPort).saveAll(captor.capture());
        assertThat(captor.getValue().get(0).orderIndex()).isEqualTo(3);
        assertThat(captor.getValue().get(1).orderIndex()).isEqualTo(4);
    }

    @Test
    void upload_shouldThrowNotFound_whenPropertyDoesNotExist() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(false);

        var command = new UploadPhotosCommand(PROPERTY_ID, List.of(photoFile("foto.jpg", "image/jpeg")));

        assertThatThrownBy(() -> sut.upload(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Imóvel não encontrado.");

        verify(savePhotosPort, never()).saveAll(anyList());
    }

    @Test
    void upload_shouldThrow415_whenFileExtensionIsInvalid() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);

        var command = new UploadPhotosCommand(PROPERTY_ID, List.of(photoFile("doc.pdf", "application/pdf")));

        assertThatThrownBy(() -> sut.upload(command))
                .isInstanceOf(UnsupportedMediaTypeException.class)
                .hasMessage("Formato de arquivo não suportado. Formatos aceitos: jpg, jpeg, png, webp.");

        verify(savePhotosPort, never()).saveAll(anyList());
    }

    @Test
    void upload_shouldAccept_allAllowedExtensions() {
        when(findPropertyPort.existsById(PROPERTY_ID)).thenReturn(true);
        when(findPhotosByPropertyPort.countByPropertyId(PROPERTY_ID)).thenReturn(0);
        when(findPhotosByPropertyPort.existsCoverByPropertyId(PROPERTY_ID)).thenReturn(false);
        when(uploadPhotoFilePort.upload(anyString(), anyString(), any())).thenReturn("https://cdn.example.com/photo.jpg");
        when(savePhotosPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
        var allPhotos = List.of(
                new PropertyPhoto(UUID.randomUUID(), PROPERTY_ID, "https://cdn.example.com/photo.jpg", "a.jpg", true, 0),
                new PropertyPhoto(UUID.randomUUID(), PROPERTY_ID, "https://cdn.example.com/photo.jpg", "b.jpeg", false, 1),
                new PropertyPhoto(UUID.randomUUID(), PROPERTY_ID, "https://cdn.example.com/photo.jpg", "c.png", false, 2),
                new PropertyPhoto(UUID.randomUUID(), PROPERTY_ID, "https://cdn.example.com/photo.jpg", "d.webp", false, 3));
        when(findPhotosByPropertyPort.findByPropertyId(PROPERTY_ID)).thenReturn(allPhotos);

        var command = new UploadPhotosCommand(PROPERTY_ID, List.of(
                photoFile("a.jpg", "image/jpeg"),
                photoFile("b.jpeg", "image/jpeg"),
                photoFile("c.png", "image/png"),
                photoFile("d.webp", "image/webp")));

        assertThat(sut.upload(command)).hasSize(4);
    }

    private static PhotoFile photoFile(String name, String contentType) {
        return new PhotoFile(name, contentType, new ByteArrayInputStream(new byte[0]));
    }
}
