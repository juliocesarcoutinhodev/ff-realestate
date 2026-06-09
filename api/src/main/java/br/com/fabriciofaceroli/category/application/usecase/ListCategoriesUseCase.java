package br.com.fabriciofaceroli.category.application.usecase;

import br.com.fabriciofaceroli.category.application.port.in.ListCategoriesPort;
import br.com.fabriciofaceroli.category.application.port.out.FindAllCategoriesPort;
import br.com.fabriciofaceroli.category.domain.model.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListCategoriesUseCase implements ListCategoriesPort {

    private final FindAllCategoriesPort findAllCategoriesPort;

    public ListCategoriesUseCase(FindAllCategoriesPort findAllCategoriesPort) {
        this.findAllCategoriesPort = findAllCategoriesPort;
    }

    @Override
    public List<Category> listAll() {
        return findAllCategoriesPort.findAll();
    }
}
