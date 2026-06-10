package br.com.fabriciofaceroli.testimonial.application.usecase;

import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.testimonial.application.port.in.DeleteTestimonialPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindTestimonialByIdPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.RemoveTestimonialPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteTestimonialUseCase implements DeleteTestimonialPort {

    private final FindTestimonialByIdPort findTestimonialByIdPort;
    private final RemoveTestimonialPort removeTestimonialPort;

    public DeleteTestimonialUseCase(FindTestimonialByIdPort findTestimonialByIdPort,
                                    RemoveTestimonialPort removeTestimonialPort) {
        this.findTestimonialByIdPort = findTestimonialByIdPort;
        this.removeTestimonialPort = removeTestimonialPort;
    }

    @Override
    public void delete(UUID id) {
        findTestimonialByIdPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depoimento não encontrado."));
        removeTestimonialPort.deleteById(id);
    }
}
