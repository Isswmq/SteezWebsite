--liquibase formatted sql

--changeset isswmq:1
CREATE TABLE IF NOT EXISTS users(
    id SERIAL PRIMARY KEY,
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
    user_id INT NOT NULL,
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
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);