package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.GetPropertyBySlugPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyBySlugPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyPhotosPort;
import br.com.fabriciofaceroli.property.domain.model.PhotoSummary;
import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetPropertyBySlugUseCase implements GetPropertyBySlugPort {

    private final FindPropertyBySlugPort findPropertyBySlugPort;
    private final FindPropertyPhotosPort findPropertyPhotosPort;

    public GetPropertyBySlugUseCase(FindPropertyBySlugPort findPropertyBySlugPort,
                                    FindPropertyPhotosPort findPropertyPhotosPort) {
        this.findPropertyBySlugPort = findPropertyBySlugPort;
        this.findPropertyPhotosPort = findPropertyPhotosPort;
    }

    @Override
    public PropertyDetail getBySlug(String slug) {
        var detail = findPropertyBySlugPort.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel não encontrado."));
        List<PhotoSummary> photos = findPropertyPhotosPort.findByPropertyId(detail.id());
        return withPhotos(detail, photos);
    }

    private PropertyDetail withPhotos(PropertyDetail d, List<PhotoSummary> photos) {
        return new PropertyDetail(
                d.id(), d.title(), d.slug(), d.description(),
                d.price(), d.area(), d.bedrooms(), d.suites(),
                d.bathrooms(), d.parkingSpots(), d.address(),
                d.neighborhood(), d.city(), d.state(), d.zipCode(),
                d.dealType(), d.featured(), d.status(), d.externalUrl(),
                d.category(), photos
        );
    }
}
