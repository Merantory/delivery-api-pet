CREATE TABLE IF NOT EXISTS person
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR NOT NULL UNIQUE ,
    password VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    phone_number VARCHAR UNIQUE NOT NULL,
    address VARCHAR,
    role VARCHAR NOT NULL
);