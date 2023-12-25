-- liquibase formatted sql

-- changeset Rndmi:15.2
ALTER TABLE users
    DROP COLUMN image;

-- changeset Rndmi:15.3
ALTER TABLE users
    ADD COLUMN image_id INTEGER;

-- changeset Rndmi:15.4
ALTER TABLE users
    ADD CONSTRAINT user_image_fk
    FOREIGN KEY (image_id) REFERENCES images (id);

-- changeset Rndmi:15.5
ALTER TABLE ads
    DROP COLUMN image;

-- changeset Rndmi:15.6
ALTER TABLE ads
    ADD COLUMN image_id INTEGER;

-- changeset Rndmi:15.7
ALTER TABLE ads
    ADD CONSTRAINT ads_image_fk
    FOREIGN KEY (image_id) REFERENCES images (id);

-- changeset Rndmi:15.8
ALTER TABLE users
    RENAME COLUMN username TO email;
