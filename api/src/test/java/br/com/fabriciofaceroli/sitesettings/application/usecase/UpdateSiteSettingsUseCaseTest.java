package br.com.fabriciofaceroli.sitesettings.application.usecase;

import br.com.fabriciofaceroli.shared.exception.BusinessException;
import br.com.fabriciofaceroli.sitesettings.application.port.in.UpdateSiteSettingsCommand;
import br.com.fabriciofaceroli.sitesettings.application.port.out.FindSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.SaveSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateSiteSettingsUseCaseTest {

    private static final UUID SETTINGS_ID = UUID.randomUUID();

    private FindSiteSettingsPort findSiteSettingsPort;
    private SaveSiteSettingsPort saveSiteSettingsPort;
    private UpdateSiteSettingsUseCase sut;

    @BeforeEach
    void setUp() {
        findSiteSettingsPort = mock(FindSiteSettingsPort.class);
        saveSiteSettingsPort = mock(SaveSiteSettingsPort.class);
        sut = new UpdateSiteSettingsUseCase(findSiteSettingsPort, saveSiteSettingsPort);
    }

    @Test
    void update_shouldFetchExisting_andSave_withCommandValues() {
        when(findSiteSettingsPort.find()).thenReturn(existing());
        when(saveSiteSettingsPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = validCommand("Novo Nome", "111111-F", "5511988888888");
        var result = sut.update(command);

        assertThat(result.brokerName()).isEqualTo("Novo Nome");
        assertThat(result.brokerCreci()).isEqualTo("111111-F");
        assertThat(result.whatsapp()).isEqualTo("5511988888888");
        verify(saveSiteSettingsPort).save(any());
    }

    @Test
    void update_shouldPreserveExistingId_whenSaving() {
        when(findSiteSettingsPort.find()).thenReturn(existing());
        when(saveSiteSettingsPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sut.update(validCommand("Nome", "000000-F", "5511999999999"));

        var captor = ArgumentCaptor.forClass(SiteSettings.class);
        verify(saveSiteSettingsPort).save(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(SETTINGS_ID);
    }

    @Test
    void update_shouldThrowBusinessException_whenBrokerNameIsBlank() {
        assertThatThrownBy(() -> sut.update(validCommand("   ", "000000-F", "5511999999999")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome do corretor é obrigatório.");

        verify(findSiteSettingsPort, never()).find();
        verify(saveSiteSettingsPort, never()).save(any());
    }

    @Test
    void update_shouldThrowBusinessException_whenBrokerNameIsNull() {
        assertThatThrownBy(() -> sut.update(validCommand(null, "000000-F", "5511999999999")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome do corretor é obrigatório.");

        verify(findSiteSettingsPort, never()).find();
    }

    @Test
    void update_shouldThrowBusinessException_whenWhatsAppIsBlank() {
        assertThatThrownBy(() -> sut.update(validCommand("Nome", "000000-F", "  ")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("WhatsApp é obrigatório.");

        verify(findSiteSettingsPort, never()).find();
        verify(saveSiteSettingsPort, never()).save(any());
    }

    @Test
    void update_shouldThrowBusinessException_whenWhatsAppContainsNonDigits() {
        assertThatThrownBy(() -> sut.update(validCommand("Nome", "000000-F", "+55 (11) 99999-9999")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("whatsapp deve conter apenas dígitos, com mínimo de 10 caracteres.");

        verify(findSiteSettingsPort, never()).find();
        verify(saveSiteSettingsPort, never()).save(any());
    }

    @Test
    void update_shouldThrowBusinessException_whenWhatsAppTooShort() {
        assertThatThrownBy(() -> sut.update(validCommand("Nome", "000000-F", "999999")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("whatsapp deve conter apenas dígitos, com mínimo de 10 caracteres.");

        verify(findSiteSettingsPort, never()).find();
        verify(saveSiteSettingsPort, never()).save(any());
    }

    @Test
    void update_shouldAcceptWhatsApp_withExactlyTenDigits() {
        when(findSiteSettingsPort.find()).thenReturn(existing());
        when(saveSiteSettingsPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = sut.update(validCommand("Nome", "000000-F", "1199999999"));

        assertThat(result.whatsapp()).isEqualTo("1199999999");
    }

    private UpdateSiteSettingsCommand validCommand(String brokerName, String brokerCreci, String whatsapp) {
        return new UpdateSiteSettingsCommand(
                brokerName, brokerCreci, "Bio do corretor.",
                null, null, "Título Hero", "Subtítulo Hero",
                "+55 (11) 99999-9999", whatsapp,
                "Olá, vim pelo site!", "contato@example.com",
                null, null, null, "Meta descrição."
        );
    }

    private SiteSettings existing() {
        return new SiteSettings(
                SETTINGS_ID, "Fabrício Faceroli", "000000-F",
                null, null, null,
                null, null, null, "5511999999999",
                null, null, null, null, null, null,
                LocalDateTime.now()
        );
    }
}
