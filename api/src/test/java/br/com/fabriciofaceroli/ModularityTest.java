package br.com.fabriciofaceroli;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

    @Test
    void verifyModularStructure() {
        ApplicationModules.of(ApiApplication.class).verify();
    }
}
