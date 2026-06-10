package br.com.fabriciofaceroli.testimonial.adapter.in.web.mapper;

import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.PropertySummaryResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.ReviewTestimonialRequest;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.SubmitTestimonialRequest;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialAdminListResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialListResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialReviewResponse;
import br.com.fabriciofaceroli.testimonial.adapter.in.web.dto.TestimonialSubmitResponse;
import br.com.fabriciofaceroli.testimonial.application.port.in.ReviewTestimonialCommand;
import br.com.fabriciofaceroli.testimonial.application.port.in.SubmitTestimonialCommand;
import br.com.fabriciofaceroli.testimonial.application.port.in.TestimonialView;
import br.com.fabriciofaceroli.testimonial.domain.model.PropertySummary;
import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TestimonialWebMapper {

    SubmitTestimonialCommand toCommand(SubmitTestimonialRequest request);

    TestimonialSubmitResponse toSubmitResponse(Testimonial testimonial);

    TestimonialListResponse toListResponse(TestimonialView view);

    TestimonialAdminListResponse toAdminListResponse(TestimonialView view);

    TestimonialReviewResponse toReviewResponse(Testimonial testimonial);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "newStatus", source = "request.status")
    ReviewTestimonialCommand toCommand(UUID id, ReviewTestimonialRequest request);

    PropertySummaryResponse toPropertySummaryResponse(PropertySummary summary);
}
