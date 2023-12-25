-- liquibase formatted sql

-- changeset beshik7:6.3
CREATE TABLE comments
(
    id         SERIAL PRIMARY KEY,
    ad_id      INTEGER NOT NULL,
    author_id  INTEGER NOT NULL,
    text       TEXT    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ad_id) REFERENCES ads (id),
    FOREIGN KEY (author_id) REFERENCES users (id)
);
