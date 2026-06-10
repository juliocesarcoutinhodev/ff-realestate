package br.com.fabriciofaceroli.testimonial.adapter.in.web.controller;

import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.shared.response.PageResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.ReviewTestimonialRequest;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialAdminListResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialReviewResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.mapper.TestimonialWebMapper;
import br.com.fabriciofaceroli.testimonial.application.port.in.DeleteTestimonialPort;
import br.com.fabriciofaceroli.testimonial.application.port.in.ListAllTestimonialsPort;
import br.com.fabriciofaceroli.testimonial.application.port.in.ListAllTestimonialsQuery;
import br.com.fabriciofaceroli.testimonial.application.port.in.ReviewTestimonialPort;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/testimonials")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class TestimonialAdminController implements TestimonialAdminApiDocs {

    private final ListAllTestimonialsPort listAllTestimonialsPort;
    private final ReviewTestimonialPort reviewTestimonialPort;
    private final DeleteTestimonialPort deleteTestimonialPort;
    private final TestimonialWebMapper testimonialWebMapper;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TestimonialAdminListResponse>>> listAll(
            @RequestParam(required = false) TestimonialStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        var query = new ListAllTestimonialsQuery(status, PageRequest.of(page, size));
        var result = listAllTestimonialsPort.listAll(query);
        return ResponseEntity.ok(ApiResponse.success("Depoimentos listados com sucesso.",
                PageResponse.from(result.map(testimonialWebMapper::toAdminListResponse))));
    }

    @Override
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TestimonialReviewResponse>> review(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewTestimonialRequest request
    ) {
        var testimonial = reviewTestimonialPort.review(testimonialWebMapper.toCommand(id, request));
        return ResponseEntity.ok(ApiResponse.success("Depoimento revisado com sucesso.",
                testimonialWebMapper.toReviewResponse(testimonial)));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteTestimonialPort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
