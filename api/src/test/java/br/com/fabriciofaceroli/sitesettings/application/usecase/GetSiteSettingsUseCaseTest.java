package br.com.fabriciofaceroli.sitesettings.application.usecase;

import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.sitesettings.application.port.out.FindSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSiteSettingsUseCaseTest {

    private FindSiteSettingsPort findSiteSettingsPort;
    private GetSiteSettingsUseCase sut;

    @BeforeEach
    void setUp() {
        findSiteSettingsPort = mock(FindSiteSettingsPort.class);
        sut = new GetSiteSettingsUseCase(findSiteSettingsPort);
    }

    @Test
    void get_shouldReturnSettings_fromPort() {
        var expected = settings();
        when(findSiteSettingsPort.find()).thenReturn(expected);

        var result = sut.get();

        assertThat(result).isEqualTo(expected);
        assertThat(result.brokerName()).isEqualTo("Fabrício Faceroli");
        assertThat(result.whatsapp()).isEqualTo("5511999999999");
    }

    @Test
    void get_shouldDelegateToFindPort_onCacheMiss() {
        when(findSiteSettingsPort.find()).thenReturn(settings());

        sut.get();

        verify(findSiteSettingsPort).find();
    }

    @Test
    void get_shouldPropagateNotFound_whenPortThrows() {
        when(findSiteSettingsPort.find()).thenThrow(new ResourceNotFoundException("Configurações do site não encontradas."));

        assertThatThrownBy(() -> sut.get())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Configurações do site não encontradas.");
    }

    private SiteSettings settings() {
        return new SiteSettings(
                UUID.randomUUID(), "Fabrício Faceroli", "000000-F",
                "Corretor com 15 anos de experiência.",
                "https://storage.example.com/broker.jpg",
                "https://storage.example.com/hero.jpg",
                "Fabrício Faceroli", "Corretor de Imóveis",
                "+55 (11) 99999-9999", "5511999999999",
                "Olá, vim pelo site!", "contato@example.com",
                "https://instagram.com/fabricio", "https://facebook.com/fabricio",
                "https://linkedin.com/in/fabricio", "Imóveis de alto padrão.",
                LocalDateTime.now()
        );
    }
}
