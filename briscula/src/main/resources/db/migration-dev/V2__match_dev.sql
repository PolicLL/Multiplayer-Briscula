CREATE TABLE tournament
(
    id                VARCHAR PRIMARY KEY,
    name              VARCHAR(100) UNIQUE,
    number_of_players INT CHECK (number_of_players IN (2, 4, 8, 16, 32)),
    rounds_to_win     INT CHECK (rounds_to_win IN (1, 2, 3, 4)),
    number_of_bots    INT DEFAULT 0,
    status            VARCHAR(20) NOT NULL CHECK (status IN ('INITIALIZING', 'IN_PROGRESS', 'DONE'))
);

CREATE TABLE match
(
    id                VARCHAR PRIMARY KEY,
    number_of_players INT NOT NULL CHECK (number_of_players IN (2, 3, 4)),
    tournament_id     VARCHAR,
    FOREIGN KEY (tournament_id) REFERENCES tournament (id)
);

CREATE TABLE user_tournament
(
    user_id       VARCHAR NOT NULL,
    tournament_id VARCHAR NOT NULL,
    PRIMARY KEY (user_id, tournament_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (tournament_id) REFERENCES tournament (id)
);

CREATE TABLE user_match
(
    user_id  VARCHAR NOT NULL,
    match_id VARCHAR NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (match_id) REFERENCES match (id),
    PRIMARY KEY (user_id, match_id)
);

CREATE TABLE match_details
(
    id             VARCHAR PRIMARY KEY,
    user_id        VARCHAR NOT NULL,
    match_id       VARCHAR NOT NULL,
    points         INT     NOT NULL,
    number_of_wins INT     NOT NULL DEFAULT 0,
    group_number   INT     NOT NULL CHECK (group_number >= 0 AND group_number <= 2),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (match_id) REFERENCES match (id)
);

INSERT INTO match (id, number_of_players)
VALUES ('1', 2);
INSERT INTO match (id, number_of_players)
VALUES ('2', 3);
INSERT INTO match (id, number_of_players)
VALUES ('3', 4);
INSERT INTO match (id, number_of_players)
VALUES ('4', 2);
INSERT INTO match (id, number_of_players)
VALUES ('5', 4);
INSERT INTO match (id, number_of_players)
VALUES ('6', 3);
INSERT INTO match (id, number_of_players)
VALUES ('7', 2);
INSERT INTO match (id, number_of_players)
VALUES ('8', 4);
INSERT INTO match (id, number_of_players)
VALUES ('9', 2);
INSERT INTO match (id, number_of_players)
VALUES ('10', 3);
INSERT INTO match (id, number_of_players)
VALUES ('11', 4);
INSERT INTO match (id, number_of_players)
VALUES ('12', 2);
INSERT INTO match (id, number_of_players)
VALUES ('13', 4);
INSERT INTO match (id, number_of_players)
VALUES ('14', 3);
INSERT INTO match (id, number_of_players)
VALUES ('15', 2);
INSERT INTO match (id, number_of_players)
VALUES ('16', 4);
INSERT INTO match (id, number_of_players)
VALUES ('17', 2);
INSERT INTO match (id, number_of_players)
VALUES ('18', 3);
INSERT INTO match (id, number_of_players)
VALUES ('19', 4);
INSERT INTO match (id, number_of_players)
VALUES ('20', 2);
INSERT INTO match (id, number_of_players)
VALUES ('21', 3);
INSERT INTO match (id, number_of_players)
VALUES ('22', 4);
INSERT INTO match (id, number_of_players)
VALUES ('23', 2);
INSERT INTO match (id, number_of_players)
VALUES ('24', 3);
INSERT INTO match (id, number_of_players)
VALUES ('25', 4);
INSERT INTO match (id, number_of_players)
VALUES ('26', 2);
INSERT INTO match (id, number_of_players)
VALUES ('27', 3);
INSERT INTO match (id, number_of_players)
VALUES ('28', 4);
INSERT INTO match (id, number_of_players)
VALUES ('29', 2);
INSERT INTO match (id, number_of_players)
VALUES ('30', 4);

-- Match 1 (2 players, group = 0)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('1a', '123e4567-e89b-12d3-a456-426614174003', '1', 70, 1, 0),
       ('1b', '123e4567-e89b-12d3-a456-426614174009', '1', 50, 0, 0);

-- Match 2 (3 players, group = 0)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('2a', '123e4567-e89b-12d3-a456-426614174007', '2', 70, 1, 0),
       ('2b', '123e4567-e89b-12d3-a456-42661417400d', '2', 30, 0, 0),
       ('2c', '123e4567-e89b-12d3-a456-42661417400b', '2', 20, 0, 0);

-- Match 3 (4 players, teams)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('3a', '123e4567-e89b-12d3-a456-426614174002', '3', 35, 1, 1),
       ('3b', '123e4567-e89b-12d3-a456-42661417400a', '3', 35, 1, 1),
       ('3c', '123e4567-e89b-12d3-a456-426614174006', '3', 25, 0, 2),
       ('3d', '123e4567-e89b-12d3-a456-426614174008', '3', 25, 0, 2);

-- Match 4 (2 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('4a', '123e4567-e89b-12d3-a456-426614174004', '4', 65, 1, 0),
       ('4b', '123e4567-e89b-12d3-a456-426614174005', '4', 55, 0, 0);

-- Match 5 (3 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('5a', '123e4567-e89b-12d3-a456-426614174007', '5', 65, 1, 0),
       ('5b', '123e4567-e89b-12d3-a456-42661417400f', '5', 40, 0, 0),
       ('5c', '123e4567-e89b-12d3-a456-42661417400c', '5', 15, 0, 0);

-- Match 6 (4 players, teams)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('6a', '123e4567-e89b-12d3-a456-426614174003', '6', 40, 1, 1),
       ('6b', '123e4567-e89b-12d3-a456-42661417400e', '6', 40, 1, 1),
       ('6c', '123e4567-e89b-12d3-a456-42661417400b', '6', 20, 0, 2),
       ('6d', '123e4567-e89b-12d3-a456-42661417400c', '6', 20, 0, 2);

-- Match 7 (2 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('7a', '123e4567-e89b-12d3-a456-426614174009', '7', 65, 1, 0),
       ('7b', '123e4567-e89b-12d3-a456-42661417400a', '7', 55, 0, 0);

-- Match 8 (3 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('8a', '123e4567-e89b-12d3-a456-426614174006', '8', 50, 1, 0),
       ('8b', '123e4567-e89b-12d3-a456-426614174002', '8', 40, 0, 0),
       ('8c', '123e4567-e89b-12d3-a456-426614174007', '8', 30, 0, 0);

-- Match 9 (4 players, teams)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('9a', '123e4567-e89b-12d3-a456-42661417400d', '9', 35, 1, 1),
       ('9b', '123e4567-e89b-12d3-a456-42661417400f', '9', 35, 1, 1),
       ('9c', '123e4567-e89b-12d3-a456-426614174008', '9', 25, 0, 2),
       ('9d', '123e4567-e89b-12d3-a456-42661417400c', '9', 25, 0, 2);

-- Match 10 (2 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('10a', '123e4567-e89b-12d3-a456-426614174005', '10', 70, 1, 0),
       ('10b', '123e4567-e89b-12d3-a456-42661417400a', '10', 50, 0, 0);

-- Match 11 (3 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('11a', '123e4567-e89b-12d3-a456-42661417400e', '11', 60, 1, 0),
       ('11b', '123e4567-e89b-12d3-a456-426614174003', '11', 45, 0, 0),
       ('11c', '123e4567-e89b-12d3-a456-426614174007', '11', 15, 0, 0);

-- Match 12 (4 players, teams)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('12a', '123e4567-e89b-12d3-a456-426614174009', '12', 50, 1, 1),
       ('12b', '123e4567-e89b-12d3-a456-42661417400a', '12', 50, 1, 1),
       ('12c', '123e4567-e89b-12d3-a456-42661417400b', '12', 10, 0, 2),
       ('12d', '123e4567-e89b-12d3-a456-426614174008', '12', 10, 0, 2);

-- Match 13 (2 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('13a', '123e4567-e89b-12d3-a456-42661417400d', '13', 75, 1, 0),
       ('13b', '123e4567-e89b-12d3-a456-426614174005', '13', 35, 0, 0);

-- Match 14 (3 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('14a', '123e4567-e89b-12d3-a456-426614174006', '14', 65, 1, 0),
       ('14b', '123e4567-e89b-12d3-a456-426614174002', '14', 55, 0, 0),
       ('14c', '123e4567-e89b-12d3-a456-42661417400f', '14', 0, 0, 0);

-- Match 15 (4 players, teams)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('15a', '123e4567-e89b-12d3-a456-426614174003', '15', 40, 1, 1),
       ('15b', '123e4567-e89b-12d3-a456-42661417400e', '15', 40, 1, 1),
       ('15c', '123e4567-e89b-12d3-a456-426614174004', '15', 20, 0, 2),
       ('15d', '123e4567-e89b-12d3-a456-42661417400c', '15', 20, 0, 2);

-- Match 16 (2 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('16a', '123e4567-e89b-12d3-a456-42661417400a', '16', 70, 1, 0),
       ('16b', '123e4567-e89b-12d3-a456-426614174008', '16', 50, 0, 0);

-- Match 17 (3 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('17a', '123e4567-e89b-12d3-a456-426614174007', '17', 70, 1, 0),
       ('17b', '123e4567-e89b-12d3-a456-426614174002', '17', 20, 0, 0),
       ('17c', '123e4567-e89b-12d3-a456-42661417400b', '17', 30, 0, 0);

-- Match 18 (4 players, teams)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('18a', '123e4567-e89b-12d3-a456-42661417400d', '18', 50, 1, 1),
       ('18b', '123e4567-e89b-12d3-a456-426614174005', '18', 50, 1, 1),
       ('18c', '123e4567-e89b-12d3-a456-42661417400f', '18', 10, 0, 2),
       ('18d', '123e4567-e89b-12d3-a456-426614174009', '18', 20, 0, 2);

-- Match 19 (2 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('19a', '123e4567-e89b-12d3-a456-42661417400e', '19', 70, 1, 0),
       ('19b', '123e4567-e89b-12d3-a456-426614174006', '19', 50, 0, 0);

-- Match 20 (3 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('20a', '123e4567-e89b-12d3-a456-42661417400a', '20', 55, 1, 0),
       ('20b', '123e4567-e89b-12d3-a456-426614174004', '20', 45, 0, 0),
       ('20c', '123e4567-e89b-12d3-a456-426614174003', '20', 30, 0, 0);

-- Match 21 (4 players, teams)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('21a', '123e4567-e89b-12d3-a456-426614174002', '21', 40, 1, 1),
       ('21b', '123e4567-e89b-12d3-a456-42661417400c', '21', 40, 1, 1),
       ('21c', '123e4567-e89b-12d3-a456-426614174008', '21', 20, 0, 2),
       ('21d', '123e4567-e89b-12d3-a456-42661417400f', '21', 20, 0, 2);

-- Match 22 (2 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('22a', '123e4567-e89b-12d3-a456-426614174007', '22', 75, 1, 0),
       ('22b', '123e4567-e89b-12d3-a456-42661417400d', '22', 45, 0, 0);

-- Match 23 (3 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('23a', '123e4567-e89b-12d3-a456-426614174005', '23', 65, 1, 0),
       ('23b', '123e4567-e89b-12d3-a456-426614174009', '23', 55, 0, 0),
       ('23c', '123e4567-e89b-12d3-a456-426614174003', '23', 0, 0, 0);

-- Match 24 (4 players, teams)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('24a', '123e4567-e89b-12d3-a456-42661417400b', '24', 40, 1, 1),
       ('24b', '123e4567-e89b-12d3-a456-426614174006', '24', 40, 1, 1),
       ('24c', '123e4567-e89b-12d3-a456-426614174004', '24', 20, 0, 2),
       ('24d', '123e4567-e89b-12d3-a456-42661417400e', '24', 20, 0, 2);

-- Match 25 (2 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('25a', '123e4567-e89b-12d3-a456-42661417400a', '25', 80, 1, 0),
       ('25b', '123e4567-e89b-12d3-a456-426614174007', '25', 40, 0, 0);

-- Match 26 (3 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('26a', '123e4567-e89b-12d3-a456-42661417400d', '26', 75, 1, 0),
       ('26b', '123e4567-e89b-12d3-a456-426614174002', '26', 20, 0, 0),
       ('26c', '123e4567-e89b-12d3-a456-42661417400f', '26', 25, 0, 0);

-- Match 27 (4 players, teams)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('27a', '123e4567-e89b-12d3-a456-426614174003', '27', 60, 1, 1),
       ('27b', '123e4567-e89b-12d3-a456-42661417400e', '27', 60, 1, 1),
       ('27c', '123e4567-e89b-12d3-a456-42661417400a', '27', 0, 0, 2),
       ('27d', '123e4567-e89b-12d3-a456-426614174008', '27', 0, 0, 2);

-- Match 28 (2 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('28a', '123e4567-e89b-12d3-a456-42661417400c', '28', 70, 1, 0),
       ('28b', '123e4567-e89b-12d3-a456-426614174006', '28', 50, 0, 0);

-- Match 29 (3 players)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('29a', '123e4567-e89b-12d3-a456-426614174007', '29', 65, 1, 0),
       ('29b', '123e4567-e89b-12d3-a456-42661417400d', '29', 45, 0, 0),
       ('29c', '123e4567-e89b-12d3-a456-426614174002', '29', 10, 0, 0);

-- Match 30 (4 players, teams)
INSERT INTO match_details (id, user_id, match_id, points, number_of_wins, group_number)
VALUES ('30a', '123e4567-e89b-12d3-a456-42661417400b', '30', 55, 1, 1),
       ('30b', '123e4567-e89b-12d3-a456-42661417400f', '30', 55, 1, 1),
       ('30c', '123e4567-e89b-12d3-a456-426614174009', '30', 5, 0, 2),
       ('30d', '123e4567-e89b-12d3-a456-42661417400c', '30', 5, 0, 2);
