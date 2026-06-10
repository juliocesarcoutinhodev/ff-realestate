package br.com.fabriciofaceroli.photo.application.usecase;

import br.com.fabriciofaceroli.photo.application.port.in.PhotoOrderItem;
import br.com.fabriciofaceroli.photo.application.port.in.ReorderPhotosPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPhotosByPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.SavePhotosPort;
import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReorderPhotosUseCase implements ReorderPhotosPort {

    private final FindPropertyPort findPropertyPort;
    private final FindPhotosByPropertyPort findPhotosByPropertyPort;
    private final SavePhotosPort savePhotosPort;

    public ReorderPhotosUseCase(FindPropertyPort findPropertyPort,
                                FindPhotosByPropertyPort findPhotosByPropertyPort,
                                SavePhotosPort savePhotosPort) {
        this.findPropertyPort = findPropertyPort;
        this.findPhotosByPropertyPort = findPhotosByPropertyPort;
        this.savePhotosPort = savePhotosPort;
    }

    @Override
    @Transactional
    public List<PropertyPhoto> reorder(UUID propertyId, List<PhotoOrderItem> items) {
        if (!findPropertyPort.existsById(propertyId)) {
            throw new ResourceNotFoundException("Imóvel não encontrado.");
        }

        var photos = findPhotosByPropertyPort.findByPropertyId(propertyId);
        Set<UUID> existingIds = photos.stream().map(PropertyPhoto::id).collect(Collectors.toSet());

        for (var item : items) {
            if (!existingIds.contains(item.id())) {
                throw new ResourceNotFoundException("Foto não encontrada.");
            }
        }

        Map<UUID, Integer> orderMap = items.stream()
                .collect(Collectors.toMap(PhotoOrderItem::id, PhotoOrderItem::orderIndex));

        var updated = photos.stream()
                .map(p -> orderMap.containsKey(p.id())
                        ? new PropertyPhoto(p.id(), p.propertyId(), p.url(), p.fileName(), p.cover(), orderMap.get(p.id()))
                        : p)
                .toList();

        return savePhotosPort.saveAll(updated);
    }
}
