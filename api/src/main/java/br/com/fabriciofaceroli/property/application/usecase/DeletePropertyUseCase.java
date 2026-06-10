package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.DeletePropertyPort;
import br.com.fabriciofaceroli.property.application.port.out.DeletePropertyByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.DeletePropertyPhotosPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyByIdPort;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeletePropertyUseCase implements DeletePropertyPort {

    private final FindPropertyByIdPort findPropertyByIdPort;
    private final DeletePropertyPhotosPort deletePropertyPhotosPort;
    private final DeletePropertyByIdPort deletePropertyByIdPort;

    public DeletePropertyUseCase(FindPropertyByIdPort findPropertyByIdPort,
                                  DeletePropertyPhotosPort deletePropertyPhotosPort,
                                  DeletePropertyByIdPort deletePropertyByIdPort) {
        this.findPropertyByIdPort = findPropertyByIdPort;
        this.deletePropertyPhotosPort = deletePropertyPhotosPort;
        this.deletePropertyByIdPort = deletePropertyByIdPort;
    }

    @Override
    public void delete(UUID id) {
        findPropertyByIdPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel não encontrado."));
        deletePropertyPhotosPort.deleteByPropertyId(id);
        deletePropertyByIdPort.deleteById(id);
    }
}
