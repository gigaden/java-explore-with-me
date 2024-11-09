-- DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(256) NOT NULL,
    name  VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(256)                NOT NULL,
    category           BIGINT REFERENCES categories (id) ON DELETE CASCADE,
    initiator          BIGINT REFERENCES users (id) ON DELETE CASCADE,
    description        VARCHAR(512)                NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    location_lat       VARCHAR(128)                NOT NULL,
    location_lon       VARCHAR(128)                NOT NULL,
    paid               BOOLEAN                     NOT NULL,
    participant_limit  INTEGER                     NOT NULL,
    request_moderation BOOLEAN                     NOT NULL,
    title              VARCHAR(256)                NOT NULL
);