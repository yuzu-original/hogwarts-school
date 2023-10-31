-- liquibase formatted sql

-- changeset yuzu:1
CREATE INDEX user_name_index ON student (name);
CREATE INDEX faculty_color_index ON faculty (color);