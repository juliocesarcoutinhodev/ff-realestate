package br.com.fabriciofaceroli.testimonial.infrastructure.persistence.mapper;

import br.com.fabriciofaceroli.testimonial.domain.model.Testimonial;
import br.com.fabriciofaceroli.testimonial.infrastructure.persistence.entity.TestimonialEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestimonialMapper {

    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt().toInstant(java.time.ZoneOffset.UTC))")
    Testimonial toTestimonial(TestimonialEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TestimonialEntity toEntity(Testimonial testimonial);
}
