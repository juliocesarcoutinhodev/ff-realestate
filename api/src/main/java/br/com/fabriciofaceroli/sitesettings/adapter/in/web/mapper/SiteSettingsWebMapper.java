package br.com.fabriciofaceroli.sitesettings.adapter.in.web.mapper;

import br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto.SiteSettingsResponse;
import br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto.UpdateSiteSettingsRequest;
import br.com.fabriciofaceroli.sitesettings.application.port.in.SettingsImageFile;
import br.com.fabriciofaceroli.sitesettings.application.port.in.UpdateSiteSettingsCommand;
import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SiteSettingsWebMapper {

    SiteSettingsResponse toResponse(SiteSettings domain);

    UpdateSiteSettingsCommand toCommand(UpdateSiteSettingsRequest request);

    default SettingsImageFile toSettingsImageFile(MultipartFile file) {
        try {
            return new SettingsImageFile(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getInputStream()
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
