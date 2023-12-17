CREATE TABLE IF NOT EXISTS comment
(
    product_id BIGINT NOT NULL REFERENCES product(id),
    person_id BIGINT NOT NULL REFERENCES person(id),
    content TEXT NOT NULL,
    PRIMARY KEY (product_id, person_id)
);