--liquibase formatted sql

--changeset isswmq:5
CREATE TABLE IF NOT EXISTS discounts(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_percent INTEGER NOT NULL CHECK (discount_percent >= 1 and discount_percent <= 99)
);

--changeset isswmq:6
CREATE TABLE IF NOT EXISTS products(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    description TEXT,
    sku VARCHAR(64) NOT NULl,
    category VARCHAR(64) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    discount_id INTEGER,
    FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE SET NULL
);

