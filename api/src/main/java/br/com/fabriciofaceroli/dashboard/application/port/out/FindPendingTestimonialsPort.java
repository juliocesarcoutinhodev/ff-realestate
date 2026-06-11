package br.com.fabriciofaceroli.dashboard.application.port.out;

import br.com.fabriciofaceroli.dashboard.domain.model.PendingTestimonial;

import java.util.List;

public interface FindPendingTestimonialsPort {
    List<PendingTestimonial> findPending(int limit);
}
