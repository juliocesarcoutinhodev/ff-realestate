package br.com.fabriciofaceroli.category.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.category.application.port.out.CountPropertiesByCategoryPort;
import br.com.fabriciofaceroli.category.application.port.out.CountPropertiesByCategoryPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Stub temporário — substituir pela implementação real em property/infrastructure
 * quando o módulo property for implementado com sua tabela e repositório JPA.
 */
@Component
public class PropertyCountAdapter implements CountPropertiesByCategoryPort {

    @Override
    public long countByCategory(UUID categoryId) {
        return 0L;
    }
}
