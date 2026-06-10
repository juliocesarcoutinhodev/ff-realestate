package br.com.fabriciofaceroli.testimonial.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.property.application.port.out.FindPropertyByIdPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindPropertySummaryForTestimonialPort;
import br.com.fabriciofaceroli.testimonial.domain.model.PropertySummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PropertySummaryLookupForTestimonialAdapter implements FindPropertySummaryForTestimonialPort {

    private final FindPropertyByIdPort findPropertyByIdPort;

    @Override
    public Optional<PropertySummary> findById(UUID propertyId) {
        return findPropertyByIdPort.findById(propertyId)
                .map(p -> new PropertySummary(p.id(), p.title(), p.slug()));
    }
}
