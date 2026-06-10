package br.com.fabriciofaceroli.testimonial.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.testimonial.application.port.out.FindAllTestimonialsPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindApprovedTestimonialsPort;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import br.com.fabriciofaceroli.testimonial.infrastructure.persistence.mapper.TestimonialMapper;
import br.com.fabriciofaceroli.testimonial.infrastructure.persistence.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestimonialQueryAdapter implements FindApprovedTestimonialsPort, FindAllTestimonialsPort {

    private final TestimonialRepository testimonialRepository;
    private final TestimonialMapper testimonialMapper;

    @Override
    public List<Testimonial> findApproved(UUID propertyId) {
        var entities = propertyId != null
                ? testimonialRepository.findByStatusAndPropertyIdOrderByCreatedAtDesc(TestimonialStatus.APPROVED, propertyId)
                : testimonialRepository.findByStatusOrderByCreatedAtDesc(TestimonialStatus.APPROVED);
        return entities.stream().map(testimonialMapper::toTestimonial).toList();
    }

    @Override
    public Page<Testimonial> findAll(TestimonialStatus status, Pageable pageable) {
        var entities = status != null
                ? testimonialRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                : testimonialRepository.findAllByOrderByCreatedAtDesc(pageable);
        return entities.map(testimonialMapper::toTestimonial);
    }
}
