CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    age INT CHECK (age >= 18),
    country VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    points INT DEFAULT 0 CHECK (points >= 0),
    level INT DEFAULT 1 CHECK (level >= 1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
