package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.GetAdminPropertyByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyDetailByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyPhotosPort;
import br.com.fabriciofaceroli.property.domain.model.PhotoSummary;
import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetAdminPropertyByIdUseCase implements GetAdminPropertyByIdPort {

    private final FindPropertyDetailByIdPort findPropertyDetailByIdPort;
    private final FindPropertyPhotosPort findPropertyPhotosPort;

    public GetAdminPropertyByIdUseCase(FindPropertyDetailByIdPort findPropertyDetailByIdPort,
                                       FindPropertyPhotosPort findPropertyPhotosPort) {
        this.findPropertyDetailByIdPort = findPropertyDetailByIdPort;
        this.findPropertyPhotosPort = findPropertyPhotosPort;
    }

    @Override
    public PropertyDetail getById(UUID id) {
        var detail = findPropertyDetailByIdPort.findDetailById(id)
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
