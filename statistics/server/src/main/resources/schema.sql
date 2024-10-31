CREATE TABLE IF NOT EXISTS statistics
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app        VARCHAR(512)                         NOT NULL,
    uri        VARCHAR(512)                         NOT NULL,
    ip         VARCHAR(128)                         NOT NULL,
    "timestamp" TIMESTAMP WITHOUT TIME ZONE         NOT NULL
    );
