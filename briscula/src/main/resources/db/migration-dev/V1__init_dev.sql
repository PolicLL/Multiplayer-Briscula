CREATE TABLE photos (
    id VARCHAR PRIMARY KEY,
    photo BYTEA NOT NULL,
    name VARCHAR(50) UNIQUE
);

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
    photo_id VARCHAR,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (photo_id) REFERENCES photos(id) ON DELETE SET NULL
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

INSERT INTO users (id, username, password, role, age, country, email, points, level) VALUES
('123e4567-e89b-12d3-a456-42661417400g', 'user1', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 25, 'USA', 'user1@example.com', 100, 1),
('123e4567-e89b-12d3-a456-426614174002', 'user2', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 30, 'Canada', 'user2@example.com', 150, 2),
('123e4567-e89b-12d3-a456-426614174003', 'user3', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 22, 'UK', 'user3@example.com', 200, 3),
('123e4567-e89b-12d3-a456-426614174004', 'user4', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 28, 'Germany', 'user4@example.com', 250, 4),
('123e4567-e89b-12d3-a456-426614174005', 'user5', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 26, 'France', 'user5@example.com', 300, 5),
('123e4567-e89b-12d3-a456-426614174006', 'user6', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 23, 'India', 'user6@example.com', 120, 1),
('123e4567-e89b-12d3-a456-426614174007', 'user7', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 27, 'Australia', 'user7@example.com', 180, 2),
('123e4567-e89b-12d3-a456-426614174008', 'user8', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 24, 'Spain', 'user8@example.com', 220, 3),
('123e4567-e89b-12d3-a456-426614174009', 'user9', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 31, 'Italy', 'user9@example.com', 270, 4),
('123e4567-e89b-12d3-a456-42661417400a', 'user10', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 29, 'Brazil', 'user10@example.com', 310, 5),
('123e4567-e89b-12d3-a456-42661417400b', 'user11', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 26, 'Mexico', 'user11@example.com', 90, 1),
('123e4567-e89b-12d3-a456-42661417400c', 'user12', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 24, 'Japan', 'user12@example.com', 160, 2),
('123e4567-e89b-12d3-a456-42661417400d', 'user13', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 27, 'South Korea', 'user13@example.com', 230, 3),
('123e4567-e89b-12d3-a456-42661417400e', 'user14', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 32, 'Russia', 'user14@example.com', 260, 4),
('123e4567-e89b-12d3-a456-42661417400f', 'user15', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 21, 'Netherlands', 'user15@example.com', 320, 5),

('123e4567-e89b-12d3-a456-42661417401a', 'bot1', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot1@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401b', 'bot2', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot2@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401c', 'bot3', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot3@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401d', 'bot4', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot4@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401e', 'bot5', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot5@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401f', 'bot6', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot6@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401g', 'bot7', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot7@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401h', 'bot8', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot8@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401i', 'bot9', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot9@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401j', 'bot10', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot10@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401k', 'bot11', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot11@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401l', 'bot12', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot12@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401m', 'bot13', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot13@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401n', 'bot14', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot14@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401o', 'bot15', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot15@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401p', 'bot16', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot16@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401r', 'bot17', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot17@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401s', 'bot18', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot18@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401t', 'bot19', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot19@example.com', 0, 0),
('123e4567-e89b-12d3-a456-42661417401u', 'bot20', '$2a$10$K/VuDn2rmMb0TgzeW7GQfu/Xr5hgEJnFUbY3xCrI3YfhULATbno86', 'ROLE_USER', 18, 'USA', 'bot20@example.com', 0, 0);