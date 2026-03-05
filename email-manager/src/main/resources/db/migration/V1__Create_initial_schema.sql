-- 1. Users
CREATE TABLE tusers (
    user_id      SERIAL PRIMARY KEY,
    user_email   VARCHAR(255) UNIQUE,
    user_name    VARCHAR(255),
    user_surname VARCHAR(255)
);

-- 2. Emails
--    state: 1=Enviado, 2=Borrador, 3=Eliminado, 4=Spam
CREATE TABLE temails (
    email_id         SERIAL PRIMARY KEY,
    email_from       VARCHAR(255),
    email_to         TEXT,
    email_cc         TEXT,
    email_subject    VARCHAR(255),
    email_body       TEXT,
    email_state      INTEGER NOT NULL DEFAULT 2,
    email_updated_at TIMESTAMP
);
