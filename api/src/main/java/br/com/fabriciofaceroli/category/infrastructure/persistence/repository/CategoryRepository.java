package br.com.fabriciofaceroli.category.infrastructure.persistence.repository;

import br.com.fabriciofaceroli.category.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    List<CategoryEntity> findAllByOrderByNameAsc();

    Optional<CategoryEntity> findBySlug(String slug);

    Optional<CategoryEntity> findByNameIgnoreCase(String name);
}
