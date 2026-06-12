package br.com.fabriciofaceroli.sitesettings.adapter.in.web.controller;

import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto.SiteSettingsResponse;
import br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto.UpdateSiteSettingsRequest;
import br.com.fabriciofaceroli.sitesettings.adapter.in.web.dto.UploadSettingsImageResponse;
import br.com.fabriciofaceroli.sitesettings.adapter.in.web.mapper.SiteSettingsWebMapper;
import br.com.fabriciofaceroli.sitesettings.application.port.in.GetSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.in.SettingsImageType;
import br.com.fabriciofaceroli.sitesettings.application.port.in.UpdateSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.in.UploadSettingsImageCommand;
import br.com.fabriciofaceroli.sitesettings.application.port.in.UploadSettingsImagePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/site-settings")
@RequiredArgsConstructor
public class SiteSettingsController implements SiteSettingsApiDocs {

    private final GetSiteSettingsPort getSiteSettingsPort;
    private final UpdateSiteSettingsPort updateSiteSettingsPort;
    private final UploadSettingsImagePort uploadSettingsImagePort;
    private final SiteSettingsWebMapper siteSettingsWebMapper;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<SiteSettingsResponse>> getSettings() {
        var settings = getSiteSettingsPort.get();
        return ResponseEntity.ok(ApiResponse.success(
                "Configurações carregadas com sucesso.",
                siteSettingsWebMapper.toResponse(settings)
        ));
    }

    @Override
    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<SiteSettingsResponse>> updateSettings(@Valid @RequestBody UpdateSiteSettingsRequest request) {
        var updated = updateSiteSettingsPort.update(siteSettingsWebMapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.success(
                "Configurações atualizadas com sucesso.",
                siteSettingsWebMapper.toResponse(updated)
        ));
    }

    @Override
    @PostMapping(value = "/broker-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<UploadSettingsImageResponse>> uploadBrokerPhoto(
            @RequestParam("file") MultipartFile file) {
        var command = new UploadSettingsImageCommand(SettingsImageType.BROKER_PHOTO, siteSettingsWebMapper.toSettingsImageFile(file));
        var url = uploadSettingsImagePort.upload(command);
        return ResponseEntity.ok(ApiResponse.success("Foto do corretor enviada com sucesso.", new UploadSettingsImageResponse(url)));
    }

    @Override
    @PostMapping(value = "/hero-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<UploadSettingsImageResponse>> uploadHeroImage(
            @RequestParam("file") MultipartFile file) {
        var command = new UploadSettingsImageCommand(SettingsImageType.HERO_IMAGE, siteSettingsWebMapper.toSettingsImageFile(file));
        var url = uploadSettingsImagePort.upload(command);
        return ResponseEntity.ok(ApiResponse.success("Imagem hero enviada com sucesso.", new UploadSettingsImageResponse(url)));
    }
}
