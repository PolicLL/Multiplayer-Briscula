CREATE TABLE users (
    id VARCHAR PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(150) NOT NULL,
    role VARCHAR(40) NOT NULL,
    age INT CHECK (age >= 3 AND age <= 100) NOT NULL,
    country VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    points INT DEFAULT 0,
    level INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (id, username, password, role, age, country, email, points, level)
VALUES (
    '123e4567-e89b-12d3-a456-426614174001',
    'user',
    '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86',
    'ROLE_USER', 25, 'USA', 'user@example.com', 0, 1
);


INSERT INTO users (id, username, password, role, age, country, email, points, level)
VALUES (
    '123e4567-e89b-12d3-a456-426614174000',
    'admin',
    '$2a$10$dcCL0ARQfBY.kqI1uaiepuFIoeHIQ9Pblj9LmWyxIg1ydj5hRCEPG',
    'ROLE_ADMIN', 25, 'USA', 'admin@example.com', 0, 1
);
