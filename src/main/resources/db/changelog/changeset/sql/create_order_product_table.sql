CREATE TABLE IF NOT EXISTS order_product
(
    order_id BIGINT REFERENCES "order"(id),
    product_id BIGINT REFERENCES product(id),
    count INT NOT NULL CHECK(count > 0),
    PRIMARY KEY (order_id, product_id)
)