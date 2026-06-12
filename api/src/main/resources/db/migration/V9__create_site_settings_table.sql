CREATE TABLE site_settings (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    broker_name         VARCHAR(100)  NOT NULL,
    broker_creci        VARCHAR(30)   NOT NULL,
    broker_bio          TEXT,
    broker_photo_url    TEXT,
    hero_image_url      TEXT,
    hero_title          VARCHAR(200),
    hero_subtitle       VARCHAR(300),
    phone               VARCHAR(20),
    whatsapp            VARCHAR(20)   NOT NULL,
    whatsapp_message    VARCHAR(300),
    email               VARCHAR(150),
    instagram_url       TEXT,
    facebook_url        TEXT,
    linkedin_url        TEXT,
    meta_description    VARCHAR(300),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW()
);
