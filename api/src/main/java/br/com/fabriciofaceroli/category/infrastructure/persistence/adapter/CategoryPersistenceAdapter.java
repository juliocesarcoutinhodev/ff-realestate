package br.com.fabriciofaceroli.category.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.category.application.port.out.FindAllCategoriesPort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryByNamePort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryBySlugPort;
import br.com.fabriciofaceroli.category.application.port.out.SaveCategoryPort;
import br.com.fabriciofaceroli.category.domain.model.Category;
import br.com.fabriciofaceroli.category.infrastructure.persistence.mapper.CategoryMapper;
import br.com.fabriciofaceroli.category.infrastructure.persistence.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements FindAllCategoriesPort, FindCategoryBySlugPort, FindCategoryByNamePort, SaveCategoryPort {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(categoryMapper::toCategory)
                .toList();
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(categoryMapper::toCategory);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .map(categoryMapper::toCategory);
    }

    @Override
    public Category save(Category category) {
        var entity = categoryMapper.toEntity(category);
        return categoryMapper.toCategory(categoryRepository.save(entity));
    }
}
