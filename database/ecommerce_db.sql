CREATE DATABASE IF NOT EXISTS ecommerce_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ecommerce_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
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

CREATE TABLE wishlist_items (
    wishlist_item_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (wishlist_item_id),
    UNIQUE KEY uk_wishlist_user_product (user_id, product_id),
    KEY idx_wishlist_user_id (user_id),
    KEY idx_wishlist_product_id (product_id),
    CONSTRAINT fk_wishlist_user
        FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_product
        FOREIGN KEY (product_id)
        REFERENCES products (product_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE orders (
    order_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    shipping_name VARCHAR(255) NOT NULL,
    shipping_phone VARCHAR(255) NOT NULL,
    shipping_address VARCHAR(1000) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    pincode VARCHAR(255) NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    razorpay_order_id VARCHAR(255),
    razorpay_payment_id VARCHAR(255),
    subtotal DECIMAL(38, 2) NOT NULL,
    tax_amount DECIMAL(38, 2) NOT NULL,
    delivery_fee DECIMAL(38, 2) NOT NULL,
    total_amount DECIMAL(38, 2) NOT NULL,
    total_price DECIMAL(38, 2) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (order_id),
    KEY idx_orders_user_id (user_id),
    UNIQUE KEY uk_orders_razorpay_order_id (razorpay_order_id),
    UNIQUE KEY uk_orders_razorpay_payment_id (razorpay_payment_id),
    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_items (
    order_item_id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    quantity INT NOT NULL,
    unit_price DECIMAL(38, 2) NOT NULL,
    price DECIMAL(38, 2) NOT NULL,
    item_total DECIMAL(38, 2) NOT NULL,
    PRIMARY KEY (order_item_id),
    KEY idx_order_items_order_id (order_id),
    KEY idx_order_items_product_id (product_id),
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders (order_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product
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
    ('Coffee Mug', 'Ceramic mug for hot and cold drinks.', 299.00, 100, 'Home', 'https://images.unsplash.com/photo-1514228742587-6b1558fcca3d', b'1', NOW(6), NOW(6)),
    ('Minimal Desk Lamp', 'A warm dimmable LED desk lamp with a clean silhouette and touch controls.', 2899.00, 24, 'Home', 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=85', b'1', NOW(6), NOW(6)),
    ('Studio Wireless Speaker', 'Compact room-filling speaker with balanced audio, deep bass, and all-day battery life.', 5499.00, 19, 'Electronics', 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=85', b'1', NOW(6), NOW(6)),
    ('Classic Leather Backpack', 'A structured everyday backpack with a padded laptop sleeve and soft premium finish.', 4299.00, 16, 'Fashion', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=85', b'1', NOW(6), NOW(6)),
    ('Mechanical Keyboard', 'Low-profile mechanical keyboard with tactile switches, soft backlighting, and wireless pairing.', 6999.00, 21, 'Electronics', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=85', b'1', NOW(6), NOW(6)),
    ('Stoneware Dinner Set', 'A refined twelve-piece stoneware collection with a soft matte glaze for everyday dining.', 3199.00, 14, 'Home', 'https://images.unsplash.com/photo-1603199506016-b9a594b593c0?auto=format&fit=crop&w=900&q=85', b'1', NOW(6), NOW(6)),
    ('Polarized Sunglasses', 'Lightweight polarized sunglasses with UV protection and a timeless unisex frame.', 1799.00, 38, 'Fashion', 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=900&q=85', b'1', NOW(6), NOW(6)),
    ('Ceramic Pour-Over Set', 'A complete pour-over coffee set designed for consistent brewing and an elegant countertop ritual.', 2399.00, 27, 'Home', 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=85', b'1', NOW(6), NOW(6)),
    ('Everyday Linen Shirt', 'A breathable linen-blend shirt with a relaxed tailored fit for warm, effortless days.', 1899.00, 32, 'Fashion', 'https://images.unsplash.com/photo-1598033129183-c4f50c736f10?auto=format&fit=crop&w=900&q=85', b'1', NOW(6), NOW(6)),
    ('Smart Ambient Light', 'App-controlled ambient light with adjustable color temperature and calm scene presets.', 3799.00, 20, 'Electronics', 'https://images.unsplash.com/photo-1504198453319-5ce911bafcde?auto=format&fit=crop&w=900&q=85', b'1', NOW(6), NOW(6)),
    ('Scented Soy Candle', 'Hand-poured soy candle with cedar, bergamot, and soft amber notes in a reusable glass vessel.', 899.00, 45, 'Home', 'https://images.unsplash.com/photo-1603006905003-be475563bc59?auto=format&fit=crop&w=900&q=85', b'1', NOW(6), NOW(6));
