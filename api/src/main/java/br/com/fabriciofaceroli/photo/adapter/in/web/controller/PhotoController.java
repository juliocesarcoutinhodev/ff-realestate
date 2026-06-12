package br.com.fabriciofaceroli.photo.adapter.in.web.controller;

import br.com.fabriciofaceroli.photo.adapter.in.web.dto.PhotoOrderItemRequest;
import br.com.fabriciofaceroli.photo.adapter.in.web.dto.PhotoUploadResponse;
import br.com.fabriciofaceroli.photo.adapter.in.web.mapper.PhotoWebMapper;
import br.com.fabriciofaceroli.photo.application.port.in.DeletePhotoPort;
import br.com.fabriciofaceroli.photo.application.port.in.ListPhotosPort;
import br.com.fabriciofaceroli.photo.application.port.in.ReorderPhotosPort;
import br.com.fabriciofaceroli.photo.application.port.in.SetCoverPhotoPort;
import br.com.fabriciofaceroli.photo.application.port.in.UploadPhotosCommand;
import br.com.fabriciofaceroli.photo.application.port.in.UploadPhotosPort;
import br.com.fabriciofaceroli.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final SetCoverPhotoPort setCoverPhotoPort;
    private final ReorderPhotosPort reorderPhotosPort;
    private final DeletePhotoPort deletePhotoPort;
    private final PhotoWebMapper photoWebMapper;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<PhotoUploadResponse>>> listPhotos(@PathVariable UUID propertyId) {
        var photos = listPhotosPort.list(propertyId);
        return ResponseEntity.ok(ApiResponse.success("Fotos listadas com sucesso.", photoWebMapper.toResponseList(photos)));
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<PhotoUploadResponse>>> uploadPhotos(
            @PathVariable UUID propertyId,
            @RequestParam("files") List<MultipartFile> files) {
        var command = new UploadPhotosCommand(propertyId, photoWebMapper.toPhotoFiles(files));
        var photos = uploadPhotosPort.upload(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fotos enviadas com sucesso.", photoWebMapper.toResponseList(photos)));
    }

    @Override
    @PatchMapping("/{photoId}/cover")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<PhotoUploadResponse>>> setCover(
            @PathVariable UUID propertyId,
            @PathVariable UUID photoId) {
        var photos = setCoverPhotoPort.setCover(propertyId, photoId);
        return ResponseEntity.ok(ApiResponse.success("Foto de capa atualizada com sucesso.", photoWebMapper.toResponseList(photos)));
    }

    @Override
    @PatchMapping("/order")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<PhotoUploadResponse>>> reorderPhotos(
            @PathVariable UUID propertyId,
            @RequestBody List<PhotoOrderItemRequest> items) {
        var photos = reorderPhotosPort.reorder(propertyId, photoWebMapper.toOrderItems(items));
        return ResponseEntity.ok(ApiResponse.success("Ordem das fotos atualizada com sucesso.", photoWebMapper.toResponseList(photos)));
    }

    @Override
    @DeleteMapping("/{photoId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable UUID propertyId,
            @PathVariable UUID photoId) {
        deletePhotoPort.delete(propertyId, photoId);
        return ResponseEntity.noContent().build();
    }
}
