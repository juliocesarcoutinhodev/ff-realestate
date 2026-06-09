CREATE TABLE property_photos (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    url         TEXT NOT NULL,
    file_name   VARCHAR(255) NOT NULL,
    cover       BOOLEAN NOT NULL DEFAULT FALSE,
    order_index INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
