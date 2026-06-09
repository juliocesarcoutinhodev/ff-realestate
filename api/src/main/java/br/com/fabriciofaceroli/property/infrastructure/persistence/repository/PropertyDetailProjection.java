package br.com.fabriciofaceroli.property.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface PropertyDetailProjection {
    UUID getId();
    String getTitle();
    String getSlug();
    String getDescription();
    BigDecimal getPrice();
    BigDecimal getArea();
    Integer getBedrooms();
    Integer getSuites();
    Integer getBathrooms();
    Integer getParkingSpots();
    String getAddress();
    String getNeighborhood();
    String getCity();
    String getState();
    String getZipCode();
    String getDealType();
    Boolean getFeatured();
    String getStatus();
    String getExternalUrl();
    UUID getCategoryId();
    String getCategoryName();
    String getCategorySlug();
}
