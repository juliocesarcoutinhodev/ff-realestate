CREATE TABLE testimonials (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    client_name VARCHAR(100) NOT NULL,
    text        VARCHAR(500) NOT NULL,
    rating      INTEGER      NOT NULL CHECK (rating BETWEEN 1 AND 5),
    status      VARCHAR(10)  NOT NULL DEFAULT 'PENDING',
    property_id UUID         REFERENCES properties(id) ON DELETE SET NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
