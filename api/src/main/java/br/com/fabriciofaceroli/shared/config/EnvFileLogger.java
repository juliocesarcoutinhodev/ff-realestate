package br.com.fabriciofaceroli.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class EnvFileLogger {

    @EventListener(ApplicationReadyEvent.class)
    public void log() {
        for (var filename : new String[]{".env.local", ".env"}) {
            if (new File(filename).exists()) {
                log.info("Loaded environment from: \"{}\"", filename);
                return;
            }
        }
        log.info("No .env file found — using system environment variables.");
    }
}
