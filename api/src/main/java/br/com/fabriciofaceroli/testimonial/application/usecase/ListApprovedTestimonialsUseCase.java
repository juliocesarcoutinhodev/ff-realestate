package br.com.fabriciofaceroli.testimonial.application.usecase;

import br.com.fabriciofaceroli.testimonial.application.port.in.ListApprovedTestimonialsPort;
import br.com.fabriciofaceroli.testimonial.application.port.in.TestimonialView;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindApprovedTestimonialsPort;
import br.com.fabriciofaceroli.testimonial.application.port.out.FindPropertySummaryForTestimonialPort;
import br.com.fabriciofaceroli.testimonial.domain.model.PropertySummary;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ListApprovedTestimonialsUseCase implements ListApprovedTestimonialsPort {

    private final FindApprovedTestimonialsPort findApprovedTestimonialsPort;
    private final FindPropertySummaryForTestimonialPort findPropertySummaryPort;

    public ListApprovedTestimonialsUseCase(FindApprovedTestimonialsPort findApprovedTestimonialsPort,
                                           FindPropertySummaryForTestimonialPort findPropertySummaryPort) {
        this.findApprovedTestimonialsPort = findApprovedTestimonialsPort;
        this.findPropertySummaryPort = findPropertySummaryPort;
    }

    @Override
    public List<TestimonialView> list(UUID propertyId) {
        return findApprovedTestimonialsPort.findApproved(propertyId)
                .stream()
                .map(this::toView)
                .toList();
    }

    private TestimonialView toView(Testimonial t) {
        PropertySummary property = null;
        if (t.propertyId() != null) {
            property = findPropertySummaryPort.findById(t.propertyId()).orElse(null);
        }
        return new TestimonialView(t.id(), t.clientName(), t.text(), t.rating(), t.status(), property, t.createdAt());
    }
}
