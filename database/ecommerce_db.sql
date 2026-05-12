CREATE DATABASE IF NOT EXISTS ecommerce_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ecommerce_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL DEFAULT 'USER',
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE products (
    product_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(38, 2) NOT NULL,
    stock INT NOT NULL,
    category VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    active BIT(1) NOT NULL DEFAULT b'1',
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE carts (
    cart_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (cart_id),
    UNIQUE KEY uk_carts_user_id (user_id),
    CONSTRAINT fk_carts_user
        FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cart_items (
    cart_item_id BIGINT NOT NULL AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(38, 2) NOT NULL,
    PRIMARY KEY (cart_item_id),
    KEY idx_cart_items_cart_id (cart_id),
    KEY idx_cart_items_product_id (product_id),
    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id)
        REFERENCES carts (cart_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product
        FOREIGN KEY (product_id)
        REFERENCES products (product_id)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (name, email, password, role, created_at, updated_at)
VALUES
    ('Admin', 'admin@example.com', '$2a$10$EZJqFO5WDhXkS4xdwLU0KuQz86GBy9i73DNzNP7dkv7fDZkGKgSou', 'ADMIN', NOW(6), NOW(6));

INSERT INTO products (name, description, price, stock, category, image_url, active, created_at, updated_at)
VALUES
    ('Wireless Headphones', 'Comfortable Bluetooth headphones with clear sound.', 2499.00, 25, 'Electronics', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e', b'1', NOW(6), NOW(6)),
    ('Smart Watch', 'Fitness tracking smart watch with heart-rate monitor.', 3499.00, 18, 'Electronics', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30', b'1', NOW(6), NOW(6)),
    ('Cotton T-Shirt', 'Soft everyday cotton t-shirt.', 599.00, 60, 'Fashion', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab', b'1', NOW(6), NOW(6)),
    ('Running Shoes', 'Lightweight shoes for daily running and walking.', 1999.00, 30, 'Fashion', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', b'1', NOW(6), NOW(6)),
    ('Coffee Mug', 'Ceramic mug for hot and cold drinks.', 299.00, 100, 'Home', 'https://images.unsplash.com/photo-1514228742587-6b1558fcca3d', b'1', NOW(6), NOW(6));
