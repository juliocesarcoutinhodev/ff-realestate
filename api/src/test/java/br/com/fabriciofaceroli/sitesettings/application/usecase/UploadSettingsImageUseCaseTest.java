package br.com.fabriciofaceroli.sitesettings.application.usecase;

import br.com.fabriciofaceroli.shared.exception.BusinessException;
import br.com.fabriciofaceroli.shared.exception.UnsupportedMediaTypeException;
import br.com.fabriciofaceroli.sitesettings.application.port.in.SettingsImageFile;
import br.com.fabriciofaceroli.sitesettings.application.port.in.SettingsImageType;
import br.com.fabriciofaceroli.sitesettings.application.port.in.UploadSettingsImageCommand;
import br.com.fabriciofaceroli.sitesettings.application.port.out.DeleteSettingsFilePort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.FindSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.SaveSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.UploadSettingsFilePort;
import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadSettingsImageUseCaseTest {

    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final String PUBLIC_URL = "https://storage.example.com/ff-realestate/settings/broker-photo.jpg";

    private FindSiteSettingsPort findSiteSettingsPort;
    private SaveSiteSettingsPort saveSiteSettingsPort;
    private UploadSettingsFilePort uploadSettingsFilePort;
    private DeleteSettingsFilePort deleteSettingsFilePort;
    private UploadSettingsImageUseCase sut;

    @BeforeEach
    void setUp() {
        findSiteSettingsPort = mock(FindSiteSettingsPort.class);
        saveSiteSettingsPort = mock(SaveSiteSettingsPort.class);
        uploadSettingsFilePort = mock(UploadSettingsFilePort.class);
        deleteSettingsFilePort = mock(DeleteSettingsFilePort.class);
        sut = new UploadSettingsImageUseCase(findSiteSettingsPort, saveSiteSettingsPort, uploadSettingsFilePort, deleteSettingsFilePort);
    }

    @Test
    void upload_shouldUploadBrokerPhoto_andUpdateBrokerPhotoUrl() {
        var existing = settingsWithUrls("https://old-photo.jpg", "https://old-hero.jpg");
        when(findSiteSettingsPort.find()).thenReturn(existing);
        when(uploadSettingsFilePort.upload(anyString(), anyString(), any())).thenReturn(PUBLIC_URL);
        when(saveSiteSettingsPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = sut.upload(command(SettingsImageType.BROKER_PHOTO, "foto.jpg", "image/jpeg", 100));

        assertThat(result).isEqualTo(PUBLIC_URL);

        var captor = ArgumentCaptor.forClass(SiteSettings.class);
        verify(saveSiteSettingsPort).save(captor.capture());
        assertThat(captor.getValue().brokerPhotoUrl()).isEqualTo(PUBLIC_URL);
        assertThat(captor.getValue().heroImageUrl()).isEqualTo("https://old-hero.jpg");
    }

    @Test
    void upload_shouldUploadHeroImage_andUpdateHeroImageUrl() {
        var existing = settingsWithUrls("https://old-photo.jpg", "https://old-hero.jpg");
        when(findSiteSettingsPort.find()).thenReturn(existing);
        when(uploadSettingsFilePort.upload(anyString(), anyString(), any())).thenReturn("https://storage.example.com/settings/hero-image.jpg");
        when(saveSiteSettingsPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.upload(command(SettingsImageType.HERO_IMAGE, "banner.jpg", "image/jpeg", 100));

        var captor = ArgumentCaptor.forClass(SiteSettings.class);
        verify(saveSiteSettingsPort).save(captor.capture());
        assertThat(captor.getValue().heroImageUrl()).isEqualTo("https://storage.example.com/settings/hero-image.jpg");
        assertThat(captor.getValue().brokerPhotoUrl()).isEqualTo("https://old-photo.jpg");
    }

    @Test
    void upload_shouldDeleteOldFile_whenPreviousUrlExists() {
        var existing = settingsWithUrls("https://old-photo.jpg", null);
        when(findSiteSettingsPort.find()).thenReturn(existing);
        when(uploadSettingsFilePort.upload(anyString(), anyString(), any())).thenReturn(PUBLIC_URL);
        when(saveSiteSettingsPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.upload(command(SettingsImageType.BROKER_PHOTO, "foto.jpg", "image/jpeg", 100));

        verify(deleteSettingsFilePort).delete("https://old-photo.jpg");
    }

    @Test
    void upload_shouldNotDelete_whenNoPreviousUrl() {
        var existing = settingsWithUrls(null, null);
        when(findSiteSettingsPort.find()).thenReturn(existing);
        when(uploadSettingsFilePort.upload(anyString(), anyString(), any())).thenReturn(PUBLIC_URL);
        when(saveSiteSettingsPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.upload(command(SettingsImageType.BROKER_PHOTO, "foto.jpg", "image/jpeg", 100));

        verify(deleteSettingsFilePort, never()).delete(anyString());
    }

    @Test
    void upload_shouldUseCorrectObjectName_forBrokerPhoto() {
        when(findSiteSettingsPort.find()).thenReturn(settingsWithUrls(null, null));
        when(uploadSettingsFilePort.upload(anyString(), anyString(), any())).thenReturn(PUBLIC_URL);
        when(saveSiteSettingsPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.upload(command(SettingsImageType.BROKER_PHOTO, "minha-foto.png", "image/png", 100));

        verify(uploadSettingsFilePort).upload(eq("settings/broker-photo.png"), eq("image/png"), any());
    }

    @Test
    void upload_shouldUseCorrectObjectName_forHeroImage() {
        when(findSiteSettingsPort.find()).thenReturn(settingsWithUrls(null, null));
        when(uploadSettingsFilePort.upload(anyString(), anyString(), any())).thenReturn(PUBLIC_URL);
        when(saveSiteSettingsPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.upload(command(SettingsImageType.HERO_IMAGE, "banner.webp", "image/webp", 100));

        verify(uploadSettingsFilePort).upload(eq("settings/hero-image.webp"), eq("image/webp"), any());
    }

    @Test
    void upload_shouldThrowBusinessException_whenFileTooLarge() {
        var oversized = MAX_SIZE + 1;

        assertThatThrownBy(() -> sut.upload(command(SettingsImageType.BROKER_PHOTO, "foto.jpg", "image/jpeg", oversized)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Arquivo muito grande. Tamanho máximo permitido: 5MB.");

        verify(findSiteSettingsPort, never()).find();
        verify(uploadSettingsFilePort, never()).upload(anyString(), anyString(), any());
    }

    @Test
    void upload_shouldThrow415_whenFormatNotAllowed() {
        assertThatThrownBy(() -> sut.upload(command(SettingsImageType.BROKER_PHOTO, "doc.pdf", "application/pdf", 100)))
                .isInstanceOf(UnsupportedMediaTypeException.class)
                .hasMessage("Formato de arquivo não suportado. Formatos aceitos: jpg, jpeg, png, webp.");

        verify(findSiteSettingsPort, never()).find();
        verify(uploadSettingsFilePort, never()).upload(anyString(), anyString(), any());
    }

    @Test
    void upload_shouldAccept_allAllowedFormats() {
        when(findSiteSettingsPort.find()).thenReturn(settingsWithUrls(null, null));
        when(uploadSettingsFilePort.upload(anyString(), anyString(), any())).thenReturn(PUBLIC_URL);
        when(saveSiteSettingsPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        for (var ext : new String[]{"jpg", "jpeg", "png", "webp"}) {
            sut.upload(command(SettingsImageType.BROKER_PHOTO, "foto." + ext, "image/" + ext, 100));
        }

        verify(uploadSettingsFilePort, org.mockito.Mockito.times(4)).upload(anyString(), anyString(), any());
    }

    private UploadSettingsImageCommand command(SettingsImageType type, String fileName, String contentType, long size) {
        var file = new SettingsImageFile(fileName, contentType, size, new ByteArrayInputStream(new byte[0]));
        return new UploadSettingsImageCommand(type, file);
    }

    private SiteSettings settingsWithUrls(String brokerPhotoUrl, String heroImageUrl) {
        return new SiteSettings(
                UUID.randomUUID(), "Nome", "000000-F", null,
                brokerPhotoUrl, heroImageUrl,
                null, null, null, "5511999999999",
                null, null, null, null, null, null,
                LocalDateTime.now()
        );
    }
}
