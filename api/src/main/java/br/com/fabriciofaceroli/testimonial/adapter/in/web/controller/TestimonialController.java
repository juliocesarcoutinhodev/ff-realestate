package br.com.fabriciofaceroli.testimonial.adapter.in.web.controller;

import br.com.fabriciofaceroli.shared.response.ApiResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.SubmitTestimonialRequest;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialListResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialSubmitResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.mapper.TestimonialWebMapper;
import br.com.fabriciofaceroli.testimonial.application.port.in.ListApprovedTestimonialsPort;
import br.com.fabriciofaceroli.testimonial.application.port.in.SubmitTestimonialPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/testimonials")
@RequiredArgsConstructor
public class TestimonialController implements TestimonialApiDocs {

    private final ListApprovedTestimonialsPort listApprovedTestimonialsPort;
    private final SubmitTestimonialPort submitTestimonialPort;
    private final TestimonialWebMapper testimonialWebMapper;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<TestimonialListResponse>>> list(@RequestParam(required = false) UUID propertyId) {
        var views = listApprovedTestimonialsPort.list(propertyId);
        return ResponseEntity.ok(ApiResponse.success("Depoimentos listados com sucesso.",
                views.stream().map(testimonialWebMapper::toListResponse).toList()));
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<TestimonialSubmitResponse>> submit(@Valid @RequestBody SubmitTestimonialRequest request) {
        var testimonial = submitTestimonialPort.submit(testimonialWebMapper.toCommand(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Depoimento enviado com sucesso.", testimonialWebMapper.toSubmitResponse(testimonial)));
    }
}
