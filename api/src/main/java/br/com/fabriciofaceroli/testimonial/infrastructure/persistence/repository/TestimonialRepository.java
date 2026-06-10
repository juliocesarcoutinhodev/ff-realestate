package br.com.fabriciofaceroli.testimonial.infrastructure.persistence.repository;

import br.com.fabriciofaceroli.testimonial.infrastructure.persistence.entity.TestimonialEntity;
import br.com.fabriciofaceroli.testimonial.domain.model.TestimonialStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestimonialRepository extends JpaRepository<TestimonialEntity, UUID> {

    List<TestimonialEntity> findByStatusOrderByCreatedAtDesc(TestimonialStatus status);

    List<TestimonialEntity> findByStatusAndPropertyIdOrderByCreatedAtDesc(TestimonialStatus status, UUID propertyId);

    Page<TestimonialEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<TestimonialEntity> findByStatusOrderByCreatedAtDesc(TestimonialStatus status, Pageable pageable);
}
