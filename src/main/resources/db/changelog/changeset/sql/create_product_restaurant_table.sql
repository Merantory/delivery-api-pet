CREATE TABLE IF NOT EXISTS product_restaurant (
    product_id BIGINT REFERENCES product(id),
    restaurant_id BIGINT REFERENCES restaurant(id),
    count INT NOT NULL CHECK (count > 0),
    PRIMARY KEY (product_id, restaurant_id)
);