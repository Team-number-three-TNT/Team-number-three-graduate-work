-- liquibase formatted sql

-- changeset Rndmi:15.1
CREATE TABLE images
(
    id         SERIAL PRIMARY KEY,
    file_path  TEXT    NOT NULL,
    file_size  BIGINT  NOT NULL,
    media_type TEXT    NOT NULL,
    ad_id      INTEGER NOT NULL,
    user_id    INTEGER NOT NULL,
    FOREIGN KEY (ad_id) REFERENCES ads (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);
