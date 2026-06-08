package br.com.fabriciofaceroli.shared.response;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public record ErrorResponse(int status, String error, String message, String timestamp) {

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message,
                DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
    }
}
