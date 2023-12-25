-- liquibase formatted sql

-- changeset beshik7:6.1
CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    first_name VARCHAR(255)        NOT NULL,
    last_name  VARCHAR(255)        NOT NULL,
    phone      VARCHAR(20) UNIQUE  NOT NULL,
    role       VARCHAR(50)         NOT NULL,
    image      BYTEA
);