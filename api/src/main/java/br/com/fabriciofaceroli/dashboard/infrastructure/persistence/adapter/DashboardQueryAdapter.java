package br.com.fabriciofaceroli.dashboard.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.dashboard.application.port.out.CountActivePropertiesPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.CountCategoriesPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.CountInactivePropertiesPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.CountPendingTestimonialsPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.FindPendingTestimonialsPort;
import br.com.fabriciofaceroli.dashboard.application.port.out.FindRecentPropertiesPort;
import br.com.fabriciofaceroli.dashboard.domain.model.PendingTestimonial;
import br.com.fabriciofaceroli.dashboard.domain.model.RecentProperty;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DashboardQueryAdapter implements
        CountActivePropertiesPort,
        CountInactivePropertiesPort,
        CountCategoriesPort,
        CountPendingTestimonialsPort,
        FindRecentPropertiesPort,
        FindPendingTestimonialsPort {

    private final EntityManager entityManager;

    @Override
    public long countActive() {
        return toCount(entityManager
                .createNativeQuery("SELECT COUNT(*) FROM properties WHERE status = 'ACTIVE'")
                .getSingleResult());
    }

    @Override
    public long countInactive() {
        return toCount(entityManager
                .createNativeQuery("SELECT COUNT(*) FROM properties WHERE status = 'INACTIVE'")
                .getSingleResult());
    }

    @Override
    public long countCategories() {
        return toCount(entityManager
                .createNativeQuery("SELECT COUNT(*) FROM categories")
                .getSingleResult());
    }

    @Override
    public long countPendingTestimonials() {
        return toCount(entityManager
                .createNativeQuery("SELECT COUNT(*) FROM testimonials WHERE status = 'PENDING'")
                .getSingleResult());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RecentProperty> findRecent(int limit) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT id, title, slug, price, status, deal_type, created_at
                FROM properties
                ORDER BY created_at DESC
                LIMIT :limit
                """)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(row -> new RecentProperty(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2],
                        (BigDecimal) row[3],
                        (String) row[4],
                        (String) row[5],
                        toLocalDateTime(row[6])
                ))
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PendingTestimonial> findPending(int limit) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT id, client_name, text, rating, created_at
                FROM testimonials
                WHERE status = 'PENDING'
                ORDER BY created_at DESC
                LIMIT :limit
                """)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream()
                .map(row -> new PendingTestimonial(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2],
                        (Integer) row[3],
                        toLocalDateTime(row[4])
                ))
                .toList();
    }

    private static long toCount(Object result) {
        return ((Number) result).longValue();
    }

    private static LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof LocalDateTime ldt) return ldt;
        if (value instanceof Timestamp ts) return ts.toLocalDateTime();
        throw new IllegalArgumentException("Tipo inesperado para conversão de data: " + value.getClass());
    }
}
