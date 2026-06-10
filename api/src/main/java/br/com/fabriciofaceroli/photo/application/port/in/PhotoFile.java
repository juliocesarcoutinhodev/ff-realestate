package br.com.fabriciofaceroli.photo.application.port.in;

import java.io.InputStream;

public record PhotoFile(String originalFileName, String contentType, InputStream inputStream) {}
