--liquibase formatted sql

--changeset isswmq:1
CREATE TABLE IF NOT EXISTS users(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    email VARCHAR(32) NOT NULL UNIQUE,
    password VARCHAR(64),
    role VARCHAR(32) NOT NULL,
    is_account_non_locked BOOLEAN DEFAULT TRUE,
    avatar VARCHAR(64)
);
--changeset isswmq:2
CREATE TABLE IF NOT EXISTS address(
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    street VARCHAR(64) NOT NULL,
    city VARCHAR(64) NOT NULL,
    state VARCHAR(64) NOT NULL,
    postal_code VARCHAR(64) NOT NULL,
    country VARCHAR(64) NOT NULL,
    address_line VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
--changeset isswmq:3
CREATE TABLE IF NOT EXISTS refresh_token(
    username VARCHAR(32) PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL
);
--changeset isswmq:4
CREATE TABLE IF NOT EXISTS forgot_password(
    id SERIAL PRIMARY KEY,
    otp INT NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

--changeset isswmq:5
CREATE TABLE IF NOT EXISTS discounts(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    discount_percent INTEGER NOT NULL CHECK (discount_percent >= 1 and discount_percent <= 99)
);

--changeset isswmq:6
CREATE TABLE IF NOT EXISTS products(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    description TEXT,
    sku VARCHAR(64) UNIQUE NOT NULL,
    category VARCHAR(64) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    discount_id INTEGER,
    FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE SET NULL
);

--changeset isswmq:7
CREATE TABLE IF NOT EXISTS shopping_sessions(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--changeset isswmq:8
CREATE TABLE IF NOT EXISTS cart_items(
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (session_id) REFERENCES shopping_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

--changeset isswmq:9
CREATE TABLE IF NOT EXISTS payment_details(
    id SERIAL PRIMARY KEY,
    /*order_id INT NOT NULL,*/
    amount DECIMAL(10, 2) NOT NULL,
    provider VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL
);

--changeset isswmq:10
CREATE TABLE IF NOT EXISTS order_details(
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    payment_id INT,
    total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (payment_id) REFERENCES payment_details(id) ON DELETE SET NULL
);

--changeset isswmq:11
CREATE TABLE IF NOT EXISTS order_items(
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES order_details(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

