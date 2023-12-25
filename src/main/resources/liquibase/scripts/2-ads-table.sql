-- liquibase formatted sql

-- changeset beshik7:6.2
CREATE TABLE ads
(
    id          SERIAL PRIMARY KEY,
    author_id   INTEGER      NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    price       INTEGER      NOT NULL,
    image       BYTEA,
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users (id)
);
