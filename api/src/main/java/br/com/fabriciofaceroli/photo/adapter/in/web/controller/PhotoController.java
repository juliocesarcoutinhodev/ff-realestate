package br.com.fabriciofaceroli.photo.adapter.in.web.controller;

import br.com.fabriciofaceroli.photo.adapter.in.web.dto.PhotoUploadResponse;
import br.com.fabriciofaceroli.photo.adapter.in.web.mapper.PhotoWebMapper;
import br.com.fabriciofaceroli.photo.application.port.in.ListPhotosPort;
import br.com.fabriciofaceroli.photo.application.port.in.UploadPhotosCommand;
import br.com.fabriciofaceroli.photo.application.port.in.UploadPhotosPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/photos")
@RequiredArgsConstructor
public class PhotoController implements PhotoApiDocs {

    private final UploadPhotosPort uploadPhotosPort;
    private final ListPhotosPort listPhotosPort;
    private final PhotoWebMapper photoWebMapper;

    @Override
    @GetMapping
    public ResponseEntity<List<PhotoUploadResponse>> listPhotos(@PathVariable UUID propertyId) {
        var photos = listPhotosPort.list(propertyId);
        return ResponseEntity.ok(photoWebMapper.toResponseList(photos));
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<PhotoUploadResponse>> uploadPhotos(
            @PathVariable UUID propertyId,
            @RequestParam("files") List<MultipartFile> files) {
        var command = new UploadPhotosCommand(propertyId, photoWebMapper.toPhotoFiles(files));
        var photos = uploadPhotosPort.upload(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(photoWebMapper.toResponseList(photos));
    }
}
