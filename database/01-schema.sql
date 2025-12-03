SET search_path TO public;

BEGIN;

CREATE TABLE admins (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    status VARCHAR(10) DEFAULT 'ACTIVO' CHECK (status IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE customers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    firebase_uid VARCHAR(255) UNIQUE,
    name VARCHAR(150),
    email VARCHAR(150),
    phone VARCHAR(20),
    status VARCHAR(10) DEFAULT 'ACTIVO' CHECK (status IN ('ACTIVO', 'INACTIVO')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_methods (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    payment_token VARCHAR(255) NOT NULL,
    last4 VARCHAR(4),
    brand VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE restaurants (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    admin_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    logo_url TEXT,
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admins (id)
);

CREATE TABLE restaurant_activation_requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    FOREIGN KEY (admin_id) REFERENCES admins(id)
);

CREATE TABLE branches (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    restaurant_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    schedule TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
);

CREATE TABLE dining_tables (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    branch_id BIGINT NOT NULL,
    table_number INTEGER NOT NULL,
    qr_code VARCHAR(255) NOT NULL UNIQUE,
    FOREIGN KEY (branch_id) REFERENCES branches (id)
);

CREATE TABLE categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
);

CREATE TABLE products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    image_url TEXT,
    available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE offers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    restaurant_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    offer_discount_percentage DECIMAL(5,2) CHECK (offer_discount_percentage >= 0 AND offer_discount_percentage <= 100),
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
);

CREATE TABLE offer_products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    offer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER DEFAULT 1,
    FOREIGN KEY (offer_id) REFERENCES offers(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE orders (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    table_id BIGINT NOT NULL,
    customer_id BIGINT,
    status VARCHAR(50) DEFAULT 'PENDIENTE' CHECK (status IN ('PENDIENTE', 'EN_PROGRESO', 'COMPLETADO', 'CANCELADO')),
    total DECIMAL(10,2),
    people INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers (id),
    FOREIGN KEY (table_id) REFERENCES dining_tables (id)
);

CREATE TABLE order_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE employees (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    admin_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    branch_id BIGINT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE,
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL CHECK (role IN ('MESERO', 'CAJERO', 'COCINA', 'ADMIN_RESTAURANTE')),
    status VARCHAR(10) DEFAULT 'ACTIVO' CHECK (status IN ('ACTIVO', 'INACTIVO')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admins (id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
    FOREIGN KEY (branch_id) REFERENCES branches (id)
);

CREATE TABLE employee_permissions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    branch_id BIGINT,
    permission VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    FOREIGN KEY (branch_id) REFERENCES branches(id)
);

CREATE TABLE app_admins (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    role VARCHAR(50) DEFAULT 'SUPER_ADMIN' CHECK (role IN ('SUPER_ADMIN', 'SUPPORT')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMIT;
