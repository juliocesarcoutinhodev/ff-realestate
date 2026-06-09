package br.com.fabriciofaceroli.category.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.category.application.port.out.DeleteCategoryByIdPort;
import br.com.fabriciofaceroli.category.application.port.out.FindAllCategoriesPort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryByIdPort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryByNamePort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryBySlugPort;
import br.com.fabriciofaceroli.category.application.port.out.SaveCategoryPort;
import br.com.fabriciofaceroli.category.application.port.out.ValidateCategoryExistsPort;
import br.com.fabriciofaceroli.category.domain.model.Category;
import br.com.fabriciofaceroli.category.infrastructure.persistence.mapper.CategoryMapper;
import br.com.fabriciofaceroli.category.infrastructure.persistence.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements FindAllCategoriesPort, FindCategoryByIdPort, FindCategoryBySlugPort, FindCategoryByNamePort, SaveCategoryPort, DeleteCategoryByIdPort, ValidateCategoryExistsPort {

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
    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toCategory);
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

    @Override
    public void deleteById(UUID id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean exists(UUID categoryId) {
        return categoryRepository.existsById(categoryId);
    }
}
