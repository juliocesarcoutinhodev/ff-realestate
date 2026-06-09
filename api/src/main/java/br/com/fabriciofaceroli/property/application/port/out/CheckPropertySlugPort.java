package br.com.fabriciofaceroli.property.application.port.out;

public interface CheckPropertySlugPort {

    boolean existsBySlug(String slug);
}
