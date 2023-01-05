– liquibase formatted sql

– changeset jrembo:1
CREATE INDEX students_name_index ON student (name);
CREATE INDEX color_name_index ON faculty (color, name);