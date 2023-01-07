-- liquibase formatted sql

-- changeset natalya:1
CREATE INDEX name_index ON student (name);

-- changeset natalya:2
CREATE INDEX color_name_index ON faculty (color, name);