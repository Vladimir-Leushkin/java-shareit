CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(1000) NOT NULL,
    requestor_id   BIGINT REFERENCES users (id),
    created     TIMESTAMP default current_timestamp,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    is_available   BOOLEAN NOT NULL,
    owner_id       BIGINT NOT NULL REFERENCES users (id),
    request_id  BIGINT REFERENCES requests (id),
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP NOT NULL,
    item_id    BIGINT REFERENCES items (id) NOT NULL,
    booker_id    BIGINT REFERENCES users (id),
    status     varchar(25) NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      varchar(1500) NOT NULL,
    item_id   BIGINT REFERENCES items (id) NOT NULL,
    author_id BIGINT REFERENCES users (id) NOT NULL,
    created   TIMESTAMP default current_timestamp
);