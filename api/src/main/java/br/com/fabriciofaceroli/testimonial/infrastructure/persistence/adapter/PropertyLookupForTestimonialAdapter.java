package br.com.fabriciofaceroli.testimonial.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.property.application.port.out.FindPropertyByIdPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindPropertyForTestimonialPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PropertyLookupForTestimonialAdapter implements FindPropertyForTestimonialPort {

    private final FindPropertyByIdPort findPropertyByIdPort;

    @Override
    public boolean existsById(UUID propertyId) {
        return findPropertyByIdPort.findById(propertyId).isPresent();
    }
}
