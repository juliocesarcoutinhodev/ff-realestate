package br.com.fabriciofaceroli.testimonial.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindTestimonialByIdPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.RemoveTestimonialPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.SaveTestimonialPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.UpdateTestimonialStatusPort;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import br.com.fabriciofaceroli.testimonial.infrastructure.persistence.mapper.TestimonialMapper;
import br.com.fabriciofaceroli.testimonial.infrastructure.persistence.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestimonialPersistenceAdapter implements SaveTestimonialPort, FindTestimonialByIdPort, UpdateTestimonialStatusPort, RemoveTestimonialPort {

    private final TestimonialRepository testimonialRepository;
    private final TestimonialMapper testimonialMapper;

    @Override
    public Testimonial save(Testimonial testimonial) {
        var entity = testimonialMapper.toEntity(testimonial);
        return testimonialMapper.toTestimonial(testimonialRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<Testimonial> findById(UUID id) {
        return testimonialRepository.findById(id).map(testimonialMapper::toTestimonial);
    }

    @Override
    public void deleteById(UUID id) {
        testimonialRepository.deleteById(id);
    }

    @Override
    public Testimonial updateStatus(UUID id, String newStatus) {
        var entity = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depoimento não encontrado."));
        entity.setStatus(TestimonialStatus.valueOf(newStatus));
        return testimonialMapper.toTestimonial(testimonialRepository.saveAndFlush(entity));
    }
}
