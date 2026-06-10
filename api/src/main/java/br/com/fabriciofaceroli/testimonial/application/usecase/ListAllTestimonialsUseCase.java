package br.com.fabriciofaceroli.testimonial.application.usecase;

import br.com.fabriciofaceroli.testimonial.application.port.in.ListAllTestimonialsPort;
import br.com.fabriciofaceroli.testimonial.application.port.in.ListAllTestimonialsQuery;
import br.com.fabriciofaceroli.testimonial.application.port.in.TestimonialView;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindAllTestimonialsPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindPropertySummaryForTestimonialPort;
import br.com.fabriciofaceroli.testimonial.domain.model.PropertySummary;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListAllTestimonialsUseCase implements ListAllTestimonialsPort {

    private final FindAllTestimonialsPort findAllTestimonialsPort;
    private final FindPropertySummaryForTestimonialPort findPropertySummaryPort;

    public ListAllTestimonialsUseCase(FindAllTestimonialsPort findAllTestimonialsPort,
                                      FindPropertySummaryForTestimonialPort findPropertySummaryPort) {
        this.findAllTestimonialsPort = findAllTestimonialsPort;
        this.findPropertySummaryPort = findPropertySummaryPort;
    }

    @Override
    public Page<TestimonialView> listAll(ListAllTestimonialsQuery query) {
        return findAllTestimonialsPort.findAll(query.status(), query.pageable())
                .map(this::toView);
    }

    private TestimonialView toView(Testimonial t) {
        PropertySummary property = null;
        if (t.propertyId() != null) {
            property = findPropertySummaryPort.findById(t.propertyId()).orElse(null);
        }
        return new TestimonialView(t.id(), t.clientName(), t.text(), t.rating(), t.status(), property, t.createdAt());
    }
}
