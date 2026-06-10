package br.com.fabriciofaceroli.photo.application.usecase;

import br.com.fabriciofaceroli.photo.application.port.in.DeletePhotoPort;
import br.com.fabriciofaceroli.photo.application.port.out.DeletePhotoFilePort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPhotosByPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.RemovePhotoPort;
import br.com.fabriciofaceroli.photo.application.port.out.SavePhotosPort;
import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class DeletePhotoUseCase implements DeletePhotoPort {

    private final FindPhotosByPropertyPort findPhotosByPropertyPort;
    private final DeletePhotoFilePort deletePhotoFilePort;
    private final RemovePhotoPort removePhotoPort;
    private final SavePhotosPort savePhotosPort;

    public DeletePhotoUseCase(FindPhotosByPropertyPort findPhotosByPropertyPort,
                              DeletePhotoFilePort deletePhotoFilePort,
                              RemovePhotoPort removePhotoPort,
                              SavePhotosPort savePhotosPort) {
        this.findPhotosByPropertyPort = findPhotosByPropertyPort;
        this.deletePhotoFilePort = deletePhotoFilePort;
        this.removePhotoPort = removePhotoPort;
        this.savePhotosPort = savePhotosPort;
    }

    @Override
    @Transactional
    public void delete(UUID propertyId, UUID photoId) {
        var allPhotos = findPhotosByPropertyPort.findByPropertyId(propertyId);

        var photo = allPhotos.stream()
                .filter(p -> photoId.equals(p.id()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada."));

        deletePhotoFilePort.delete(photo.url());
        removePhotoPort.deleteById(photoId);

        var remaining = allPhotos.stream()
                .filter(p -> !photoId.equals(p.id()))
                .sorted(Comparator.comparingInt(PropertyPhoto::orderIndex))
                .toList();

        if (remaining.isEmpty()) return;

        boolean wasCover = photo.cover();
        var reordered = new ArrayList<PropertyPhoto>();
        for (int i = 0; i < remaining.size(); i++) {
            var p = remaining.get(i);
            boolean cover = wasCover ? (i == 0) : p.cover();
            reordered.add(new PropertyPhoto(p.id(), p.propertyId(), p.url(), p.fileName(), cover, i));
        }
        savePhotosPort.saveAll(reordered);
    }
}
